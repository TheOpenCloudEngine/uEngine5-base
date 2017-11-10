package org.uengine.social.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.uengine.five.ProcessTransactionContext;
import org.uengine.kernel.*;
import java.util.Map;

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
public class InstanceService {

    @RequestMapping(value = "/instance/{instanceId}/variables", method = RequestMethod.GET)
    public Map getProcessVariables(@PathVariable("instanceId") String instanceId) throws Exception {

        ProcessInstance instance = getProcessInstanceLocal(instanceId);

        //여기서도 롤매핑이 들어가면 시리얼라이즈 에러가 나옴.
        Map variables = ((DefaultProcessInstance) instance).getVariables();
        return variables;
    }

    @RequestMapping(value = "/instance/{instanceId}/start", method = RequestMethod.POST)
    public void start(@PathVariable("instanceId") String instanceId) throws Exception {

        ProcessInstance instance = getProcessInstanceLocal(instanceId);

        if(!instance.isRunning(""))
            instance.execute();
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
