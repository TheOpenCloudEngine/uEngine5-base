package org.uengine.five.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.uengine.five.framework.ProcessTransactionContext;
import org.uengine.five.framework.ProcessTransactional;
import org.uengine.kernel.Activity;
import org.uengine.kernel.DefaultProcessInstance;
import org.uengine.kernel.ProcessDefinition;
import org.uengine.kernel.ProcessInstance;

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
    public InstanceResource runDefinition(@RequestParam("defPath") String filePath) throws Exception {

        Object definition = definitionService.getDefinition(filePath);

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
    public ProcessInstance getProcessInstanceLocal(String instanceId){

        //lookup cached one in same transaction
        ProcessInstance instance = ProcessTransactionContext.getThreadLocalInstance().getProcessInstanceInTransaction(instanceId);

        if(instance!=null) return instance;

        //if not found, create one
        //ProcessInstance
        instance = applicationContext.getBean(
                ProcessInstance.class,
                new Object[]{
                        null,
                        instanceId,
                        null
                }
        );

        return instance;

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
