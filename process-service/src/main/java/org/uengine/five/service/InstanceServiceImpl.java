package org.uengine.five.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;
import org.uengine.five.entity.ProcessInstanceEntity;
import org.uengine.five.entity.ServiceEndpointEntity;
import org.uengine.five.framework.ProcessTransactionContext;
import org.uengine.five.framework.ProcessTransactional;
import org.uengine.five.overriding.JPAProcessInstance;
import org.uengine.five.repository.ProcessInstanceRepository;
import org.uengine.five.repository.ServiceEndpointRepository;
import org.uengine.kernel.*;
import org.uengine.kernel.bpmn.CatchingRestMessageEvent;
import org.uengine.kernel.bpmn.MessageEvent;
import org.uengine.kernel.bpmn.SendTask;
import org.uengine.uml.model.ObjectInstance;
import org.uengine.util.UEngineUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

/**
 * Created by uengine on 2017. 8. 9..
 *
 * Implementation Principles:
 *  - REST Maturity Level : 2
 *  - Not using old uEngine ProcessManagerBean, this replaces the ProcessManagerBean
 *  - ResourceManager and CachedResourceManager will be used for definition caching (Not to use the old DefinitionFactory)
 *  - json must be Typed JSON to enable object polymorphism - need to change the jackson engine. TODO: accept? typed json is sometimes hard to read
 */
@RestController
public class InstanceServiceImpl implements InstanceService {

    @Autowired
    DefinitionServiceUtil definitionService;



    // ----------------- execution services -------------------- //
    @RequestMapping(value = "/instance", method = {RequestMethod.POST, RequestMethod.PUT})
    @Transactional(rollbackFor={Exception.class})
    @ProcessTransactional
    public InstanceResource runDefinition(@RequestParam("defPath") String filePath, @QueryParam("simulation") boolean simulation) throws Exception {

        Object definition = definitionService.getDefinition(filePath, !simulation); //if simulation time, use the version under construction

        if(definition instanceof ProcessDefinition){
            ProcessDefinition processDefinition = (ProcessDefinition) definition;

            org.uengine.kernel.ProcessInstance instance = applicationContext.getBean(
                    org.uengine.kernel.ProcessInstance.class,
                    //new Object[]{
                    processDefinition,
                    null,
                    null
                    //}
            );

            instance.execute();

            return new InstanceResource(instance); //TODO: returns HATEOAS _self link instead.
        }
        return null;

    }


    @RequestMapping(value = "/instance/{instanceId}/variables", method = RequestMethod.GET)
    @ProcessTransactional(readOnly = true)
    public Map getProcessVariables(@PathVariable("instanceId") String instanceId) throws Exception {

        ProcessInstance instance = getProcessInstanceLocal(instanceId);

        //여기서도 롤매핑이 들어가면 시리얼라이즈 에러가 나옴.
        Map variables = ((DefaultProcessInstance) instance).getVariables();

        return variables;
    }

    @RequestMapping(value = "/instance/{instanceId}/start", method = RequestMethod.POST)
    @ProcessTransactional
    public InstanceResource start(@PathVariable("instanceId") String instanceId) throws Exception {

        ProcessInstance instance = getProcessInstanceLocal(instanceId);

        if(!instance.isRunning(""))
            instance.execute();

        return new InstanceResource(instance);
    }

    @RequestMapping(value = "/instance/{instanceId}/stop", method = RequestMethod.POST)
    @ProcessTransactional
    public InstanceResource stop(@PathVariable("instanceId") String instanceId) throws Exception {

        ProcessInstance instance = getProcessInstanceLocal(instanceId);

        if(instance.isRunning(""))
            instance.stop();

        return new InstanceResource(instance);
    }

    @RequestMapping(value = "/instance/{instanceId}/resume", method = RequestMethod.POST)
    @ProcessTransactional
    public InstanceResource resume(@PathVariable("instanceId") String instanceId) throws Exception {

        ProcessInstance instance = getProcessInstanceLocal(instanceId);

        if(instance.isRunning("")) {
            instance.stop();

        }

        return new InstanceResource(instance);
    }

    @RequestMapping(value = "/instance/{instanceId}", method = RequestMethod.GET)
    @ProcessTransactional(readOnly = true)
    public InstanceResource getInstance(@PathVariable("instanceId") String instanceId) throws Exception {

        ProcessInstance instance = getProcessInstanceLocal(instanceId);

        if(instance==null) throw new ResourceNotFoundException(); // make 404 error


        return new InstanceResource(instance);
    }

    @RequestMapping(value = "/instance/{instanceId}/activity/{tracingTag}/backToHere", method = RequestMethod.POST)
    @ProcessTransactional
    public InstanceResource backToHere(@PathVariable("instanceId") String instanceId, @PathVariable("tracingTag") String tracingTag) throws Exception {

        ProcessInstance instance = getProcessInstanceLocal(instanceId);
        ProcessDefinition definition = instance.getProcessDefinition();
        List<Activity> list = new ArrayList<Activity>();

        Activity returningActivity = definition.getActivity(tracingTag);
        definition.gatherPropagatedActivitiesOf(instance, returningActivity, list);
        Activity proActiviy;
        for(int i=0; i<list.size(); i++){
            proActiviy = list.get(i);
            //compensate
            proActiviy.compensate(instance);
        }

        returningActivity.resume(instance);
/*
        ProcessDefinition extends FlowActivity 상속하고 있기 때문에,
        List list = new ArrayList();
        definition.gatherPropagatedActivitiesOf(instance, definition.getWholeChildActivity(tracingTag), list);

        list 를 역순으로 하여 발견된 각 activity 들에 대해 compensate() 호출
*/



        return new InstanceResource(instance);
    }


    /**
     * use this rather ProcessManagerRemote.getProcessInstance() method instead
     * @param instanceId
     * @return
     */
    public ProcessInstance getProcessInstanceLocal(String instanceId) {

        ProcessInstance instance = ProcessTransactionContext.getThreadLocalInstance().getProcessInstanceInTransaction(instanceId);
        if (instance != null) {
            return instance;
        }
        instance = applicationContext.getBean(
                ProcessInstance.class,
                new Object[]{null, instanceId, null}
        );
        return instance;

    }


    final static String SERVICES_ROOT = "services";

    @Autowired
    ServiceEndpointRepository serviceEndpointRepository;

    @Autowired
    ProcessInstanceRepository processInstanceRepository;

    @ProcessTransactional
    @RequestMapping(value = SERVICES_ROOT+ "/**", method = {RequestMethod.GET, RequestMethod.POST})
    public Object serviceMessage(HttpServletRequest request) throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        ServiceEndpointEntity serviceEndpointEntity = serviceEndpointRepository.findOne(path.substring(SERVICES_ROOT.length()+2));

        if(serviceEndpointEntity==null)
            throw new ResourceNotFoundException();

        //find the correlated instance:
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        UEngineUtil.copyStream(request.getInputStream(), bao);

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(bao.toByteArray());


        // convert jsonNode to object instance.
        ObjectInstance objectInstance = new ObjectInstance();
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while(fieldNames.hasNext()){
            String fieldName = fieldNames.next();

            Object childNode = jsonNode.get(fieldName);
            Object converted = null;

            if(childNode instanceof TextNode){
                converted = ((TextNode)childNode).asText();
            }else if(childNode instanceof ValueNode){
                converted = ((ValueNode)childNode).asText();
            }else
                converted = childNode;

            objectInstance.setBeanProperty(fieldName, jsonNode.get(fieldName));
        }


        Object correlationData = jsonNode.get(serviceEndpointEntity.getCorrelationKey());
        ProcessInstanceEntity processInstanceEntity = processInstanceRepository.findByCorrKey(correlationData.toString());
        JPAProcessInstance instance = null;

        // case that correlation instance exists and is running:
        if(processInstanceEntity!=null && Activity.STATUS_RUNNING.equals(processInstanceEntity.getStatus())){
            instance = (JPAProcessInstance) getProcessInstanceLocal(String.valueOf(processInstanceEntity.getInstId()));

        }else { // if no instances running, create new instance:
            Object definition = definitionService.getDefinition(serviceEndpointEntity.getDefId(), true);

            ProcessDefinition processDefinition = (ProcessDefinition) definition;

            instance = (JPAProcessInstance) applicationContext.getBean(
                    ProcessInstance.class,
                    //new Object[]{
                    processDefinition,
                    null,
                    null
                    //}
            );

            instance.execute();
        }

        // trigger the start or intermediate message catch events:
        List<Activity> runningActivities = instance.getCurrentRunningActivities();

        if(runningActivities!=null)
        for(Activity activity: runningActivities){
            if(activity instanceof CatchingRestMessageEvent){
                CatchingMessageEvent catchingMessageEvent = (CatchingMessageEvent) activity;

                catchingMessageEvent.onMessage(instance, objectInstance);
            }
        }

        //set correlation key so that this instance could be re-visited by the recurring requester.
        if(instance.isNew())
            instance.getProcessInstanceEntity().setCorrKey(correlationData.toString());


        List<String> history = instance.getActivityCompletionHistory();
        if(history!=null){
            for(String tracingTag : history){

                Activity activityDone = instance.getProcessDefinition().getActivity(tracingTag);

                if(activityDone instanceof SendTask){
                    SendTask sendTask = (SendTask) activityDone;

                    return sendTask.getDataInput().get(instance, "");
                }

            }

        }

        return null;
    }

//    public ProcessInstance createProcessInstanceLocal(ProcessDefinition processDefinition){
//
//        org.uengine.kernel.ProcessInstance instance = applicationContext.getBean(
//                org.uengine.kernel.ProcessInstance.class,
//                //new Object[]{
//                processDefinition,
//                null,
//                null
//                //}
//        );
//
//    };

    @Autowired
    ApplicationContext applicationContext;

}
