package org.uengine.five.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.uengine.five.framework.ProcessTransactional;
import org.uengine.five.repository.WorklistRepository;
import org.uengine.kernel.*;
import org.uengine.modeling.resource.*;
import org.uengine.five.entity.WorklistEntity;

import javax.annotation.PostConstruct;
import java.util.HashMap;
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
public class WorkItemServiceImpl {

    @Autowired
    ResourceManager resourceManager;

    @Autowired
    DefinitionServiceUtil definitionService;  //bind automatically for the remote api?

    @Autowired
    WorklistRepository worklistRepository;

    @PostConstruct
    public void init(){
    }

    // ---------------- CRUD mappings -------------------- //

    @RequestMapping(value = "/work-item/{taskId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public WorkItemResource getWorkItem(@PathVariable("taskId") String taskId) throws Exception {

        WorklistEntity worklistEntity = worklistRepository.findOne(new Long(taskId));

        if(worklistEntity==null)
            throw new Exception("No such work item where taskId = " + taskId);

        String defId = worklistEntity.getDefId();

        ProcessDefinition definition = (ProcessDefinition) definitionService.getDefinition(defId);
        HumanActivity activity = (HumanActivity) definition.getActivity(worklistEntity.getTrcTag());

        WorkItemResource workItem = new WorkItemResource();
        workItem.setActivity(activity);
        workItem.setWorklist(worklistEntity);

        org.uengine.kernel.ProcessInstance instance = applicationContext.getBean(
                org.uengine.kernel.ProcessInstance.class,
                new Object[]{
                        null,
                        worklistEntity.getInstId().toString(),
                        null
                }
        );

        //get the parameter values and set them to the "workItem.parameterValues" so that WorkItemHandler.vue can insert the default values
        Map parameterValues = new HashMap<String, Object>();
        for(ParameterContext parameterContext : activity.getParameters()){
            if(parameterContext.getDirection().indexOf("IN") == 0){
                parameterValues.put(parameterContext.getArgument().getText(), parameterContext.getVariable().get(instance, "", ""));
            }
        }

        if(parameterValues.size() > 0)
            workItem.setParameterValues(parameterValues);

        return workItem;
    }

    @RequestMapping(value = "/work-item/{taskId}", method = RequestMethod.POST)
    @org.springframework.transaction.annotation.Transactional
    @ProcessTransactional //important!
    public void putWorkItem(@PathVariable("taskId") String taskId, @RequestBody WorkItemResource workItem) throws Exception {
        WorklistEntity worklistEntity = worklistRepository.findOne(new Long(taskId));

        Long instId = worklistEntity.getInstId();

       // ProcessDefinition definition = (ProcessDefinition) definitionService.getDefinitionLocal(workItem.getWorklist().getDefId());
        org.uengine.kernel.ProcessInstance instance = applicationContext.getBean(
                org.uengine.kernel.ProcessInstance.class,
                new Object[]{
                        null,
                        instId.toString(),
                        null
                }
        );

        HumanActivity humanActivity = ((HumanActivity)instance.getProcessDefinition().getActivity(worklistEntity.getTrcTag()));

        if(!instance.isRunning(humanActivity.getTracingTag()) && !humanActivity.isNotificationWorkitem()){
            throw new UEngineException("Illegal completion for workitem [" + humanActivity + ":"+ humanActivity.getStatus(instance) +"]: Already closed or illegal status.");
        }


        //map the argument list to variables change list
        Map variableChanges = new HashMap<String, Object>();

        if(workItem.getParameterValues()!=null)
        for(ParameterContext parameterContext : humanActivity.getParameters()){
            if(parameterContext.getDirection().indexOf("OUT") >= 0 && workItem.getParameterValues().containsKey(parameterContext.getArgument().getText())){
                variableChanges.put(parameterContext.getVariable().getName(), workItem.getParameterValues().get(parameterContext.getArgument().getText()));
            }
        }

        if("SAVED".equals(workItem.getWorklist().getStatus())){
            humanActivity.saveWorkItem(instance, variableChanges);
        }else{
            try{
                humanActivity.fireReceived(instance, variableChanges);
            }catch(Exception e){
                humanActivity.fireFault(instance, e);

                throw new UEngineException(e.getMessage(), null, new UEngineException(e.getMessage(), e), instance, humanActivity);
            }
        }


    }


    @Autowired
    ApplicationContext applicationContext;




}
