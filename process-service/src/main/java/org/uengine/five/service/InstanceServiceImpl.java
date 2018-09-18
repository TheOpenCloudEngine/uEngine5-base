package org.uengine.five.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
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
import org.uengine.kernel.bpmn.SendTask;
import org.uengine.kernel.bpmn.SignalEventInstance;
import org.uengine.kernel.bpmn.SignalIntermediateCatchEvent;
import org.uengine.uml.model.ObjectInstance;
import org.uengine.util.UEngineUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by uengine on 2017. 8. 9..
 * <p>
 * Implementation Principles:
 * - REST Maturity Level : 2
 * - Not using old uEngine ProcessManagerBean, this replaces the ProcessManagerBean
 * - ResourceManager and CachedResourceManager will be used for definition caching (Not to use the old DefinitionFactory)
 * - json must be Typed JSON to enable object polymorphism - need to change the jackson engine. TODO: accept? typed json is sometimes hard to read
 */
@RestController
public class InstanceServiceImpl implements InstanceService {

    @Autowired
    DefinitionServiceUtil definitionService;


    // ----------------- execution services -------------------- //
    @RequestMapping(value = "/instance", method = {RequestMethod.POST, RequestMethod.PUT})
    @Transactional(rollbackFor = {Exception.class})
    @ProcessTransactional
    public InstanceResource runDefinition(@RequestParam("defPath") String filePath, @QueryParam("simulation") boolean simulation) throws Exception {

        Object definition = definitionService.getDefinition(filePath, !simulation); //if simulation time, use the version under construction

        if (definition instanceof ProcessDefinition) {
            ProcessDefinition processDefinition = (ProcessDefinition) definition;

            ProcessInstance instance = applicationContext.getBean(
                    ProcessInstance.class,
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


    @RequestMapping(value = "/instance/{instanceId}/start", method = RequestMethod.POST)
    @ProcessTransactional
    public InstanceResource start(@PathVariable("instanceId") String instanceId) throws Exception {

        ProcessInstance instance = getProcessInstanceLocal(instanceId);

        if (!instance.isRunning(""))
            instance.execute();

        return new InstanceResource(instance);
    }

    @RequestMapping(value = "/instance/{instanceId}/stop", method = RequestMethod.POST)
    @ProcessTransactional
    public InstanceResource stop(@PathVariable("instanceId") String instanceId) throws Exception {

        ProcessInstance instance = getProcessInstanceLocal(instanceId);

        if (instance.isRunning(""))
            instance.stop();

        return new InstanceResource(instance);
    }

    @RequestMapping(value = "/instance/{instanceId}/suspend", method = RequestMethod.POST)
    @ProcessTransactional
    public InstanceResource suspend(@PathVariable("instanceId") String instanceId) throws Exception {

        ProcessInstance instance = getProcessInstanceLocal(instanceId);

        if (instance.isRunning("")) {
            List<ActivityInstanceContext> runningContexts = instance.getCurrentRunningActivitiesDeeply();

            for (ActivityInstanceContext runningContext : runningContexts) {

                runningContext.getActivity().suspend(runningContext.getInstance());

            }
        }

        return new InstanceResource(instance);
    }

    @RequestMapping(value = "/instance/{instanceId}/resume", method = RequestMethod.POST)
    @ProcessTransactional
    public InstanceResource resume(@PathVariable("instanceId") String instanceId) throws Exception {

        ProcessInstance instance = getProcessInstanceLocal(instanceId);

        if (instance.isRunning("")) {
            List<ActivityInstanceContext> suspendedContexts = instance.getActivitiesDeeply(Activity.STATUS_SUSPENDED);

            for (ActivityInstanceContext runningContext : suspendedContexts) {

                runningContext.getActivity().resume(runningContext.getInstance());

            }
        }

        return new InstanceResource(instance);
    }


    @RequestMapping(value = "/instance/{instanceId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ProcessTransactional(readOnly = true)
    public InstanceResource getInstance(@PathVariable("instanceId") String instanceId) throws Exception {

        ProcessInstance instance = getProcessInstanceLocal(instanceId);

        if (instance == null) throw new ResourceNotFoundException(); // make 404 error


        return new InstanceResource(instance);
    }

    @RequestMapping(value = "/instance/{instanceId}/activity/{tracingTag}/backToHere", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ProcessTransactional
    public InstanceResource backToHere(@PathVariable("instanceId") String instanceId, @PathVariable("tracingTag") String tracingTag) throws Exception {

        ProcessInstance instance = getProcessInstanceLocal(instanceId);
        ProcessDefinition definition = instance.getProcessDefinition();
        List<Activity> list = new ArrayList<Activity>();

        Activity returningActivity = definition.getActivity(tracingTag);
        definition.gatherPropagatedActivitiesOf(instance, returningActivity, list);
        Activity proActiviy;
        for (int i = 0; i < list.size(); i++) {
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
     *
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
    @RequestMapping(value = "/instance/{instanceId}/signal/{signal}", method = {RequestMethod.POST}, produces = "application/json;charset=UTF-8")
    public Object signal(@PathVariable("instanceId") String instanceId, @PathVariable("signal") String signal) throws Exception {

        ProcessInstance instance = getProcessInstanceLocal(instanceId);
        Map<String, SignalEventInstance> signalEventInstanceMap = SignalIntermediateCatchEvent.getSignalEvents(instance);

        SignalEventInstance signalEventInstance = signalEventInstanceMap.get(signal);

        Activity activity = instance.getProcessDefinition().getActivity(signalEventInstance.getActivityRef());

        if (activity instanceof SignalIntermediateCatchEvent) {
            ((SignalIntermediateCatchEvent) activity).onMessage(instance, null);
        }

        return null;
    }

    @ProcessTransactional
    @RequestMapping(value = SERVICES_ROOT + "/**", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json;charset=UTF-8")
    public Object serviceMessage(HttpServletRequest request) throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        ServiceEndpointEntity serviceEndpointEntity = serviceEndpointRepository.findById(path.substring(SERVICES_ROOT.length() + 2))
                .orElse(null);

        if (serviceEndpointEntity == null)
            throw new ResourceNotFoundException();

        //find the correlated instance:
        List<ProcessInstanceEntity> correlatedProcessInstanceEntities = null;
        Object correlationData = null;
        ObjectInstance objectInstance = new ObjectInstance();

        if ("POST".equals(request.getMethod())) {

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            UEngineUtil.copyStream(request.getInputStream(), bao);

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.readTree(bao.toByteArray());


            // convert jsonNode to object instance.
            Iterator<String> fieldNames = jsonNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();

                Object childNode = jsonNode.get(fieldName);
                Object converted = null;

                if (childNode instanceof TextNode) {
                    converted = ((TextNode) childNode).textValue();
                } else if (childNode instanceof ValueNode) {
                    converted = ((ValueNode) childNode).textValue();
                } else
                    converted = childNode;

                objectInstance.setBeanProperty(fieldName, converted);
            }

            correlationData = jsonNode.get(serviceEndpointEntity.getCorrelationKey());

            if (correlationData != null)
                correlatedProcessInstanceEntities = processInstanceRepository.findByCorrKeyAndStatus(correlationData.toString(), Activity.STATUS_RUNNING);
        }

        ProcessInstanceEntity processInstanceEntity;
        if (correlatedProcessInstanceEntities == null || correlatedProcessInstanceEntities.size() == 0)
            processInstanceEntity = null;
        else {
            processInstanceEntity = correlatedProcessInstanceEntities.get(0);
            if (correlatedProcessInstanceEntities.size() > 1)
                System.err.println("More than one correlated process instance found!");
        }

        JPAProcessInstance instance = null;

        // case that correlation instance exists and is running:
        if (processInstanceEntity != null) {
            instance = (JPAProcessInstance) getProcessInstanceLocal(String.valueOf(processInstanceEntity.getInstId()));

        } else { // if no instances running, create new instance:
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
        List<ActivityInstanceContext> runningActivities = instance.getCurrentRunningActivitiesDeeply();

        boolean neverTreated = true;

        if (runningActivities != null) {
            for (ActivityInstanceContext activityInstanceContext : runningActivities) {
                Activity activity = activityInstanceContext.getActivity();

                if (activity instanceof CatchingRestMessageEvent) {
                    CatchingMessageEvent catchingMessageEvent = (CatchingMessageEvent) activity;

                    boolean treated = catchingMessageEvent.onMessage(activityInstanceContext.getInstance(), objectInstance);
                    if (treated) neverTreated = false;
                }
            }
        }

        if (neverTreated) {
            instance.stop();

            return "문제가 발생하여 처음으로 돌아갑니다.";
        }

        //set correlation key so that this instance could be re-visited by the recurring requester.
        if (instance.isNewInstance() && correlationData != null)
            instance.getProcessInstanceEntity().setCorrKey(correlationData.toString());


//        List<String> history = instance.getActivityCompletionHistory();
//        if(history!=null){
//            for(String tracingTag : history){
//
//                Activity activityDone = instance.getProcessDefinition().getActivity(tracingTag);
//
//                if(activityDone instanceof SendTask){
//                    SendTask sendTask = (SendTask) activityDone;
//
//                    if(sendTask.getDataInput() != null && sendTask.getDataInput().getName() != null)
//                        return sendTask.getDataInput().get(instance, "");
//                    else {
//                        return sendTask.getInputPayloadTemplate();
//                    }
//                }
//
//            }
//
//        }
        List<String> messageQueue = SendTask.getMessageQueue(instance);

        if (messageQueue != null && messageQueue.size() > 0) {

//            StringBuffer fullMessage = new StringBuffer();
//
//            for(String message : messageQueue){
//                fullMessage.append(message);
//            }

            return messageQueue.get(messageQueue.size() - 1).toString().replace("\n", "").replace("\r", "");

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
