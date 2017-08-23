package org.uengine.social.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.uengine.kernel.*;
import org.uengine.modeling.resource.*;
import org.uengine.social.entity.WorklistEntity;
import org.uengine.social.repository.WorklistRepository;

import javax.annotation.PostConstruct;

/**
 * Created by uengine on 2017. 8. 9..
 *
 * Implementation Principals:
 *  - REST Maturity Level : 2
 *  - Not using old uEngine ProcessManagerBean, this replaces the ProcessManagerBean
 *  - ResourceManager and CachedResourceManager will be used for definition caching (Not to use the old DefinitionFactory)
 *  - json must be Typed JSON to enable object polymorphism - need to change the jackson engine. TODO: accept? typed json is sometimes hard to read
 */
@RestController
public class WorkItemService {

    @Autowired
    ResourceManager resourceManager;

    @Autowired
    DefinitionService definitionService;

    @Autowired
    WorklistRepository worklistRepository;

    @PostConstruct
    public void init(){
    }

    // ---------------- CRUD mappings -------------------- //

    @RequestMapping(value = "/work-item/{taskId}", method = RequestMethod.GET)
    public WorkItem getWorkItem(@PathVariable("taskId") String taskId) throws Exception {

        WorklistEntity worklistEntity = worklistRepository.findOne(new Long(taskId));

        if(worklistEntity==null)
            throw new Exception("No such work item where taskId = " + taskId);

        String defId = worklistEntity.getDefId();

        ProcessDefinition definition = (ProcessDefinition) definitionService.getDefinitionLocal(defId);
        Activity activity = definition.getActivity(worklistEntity.getTrcTag());

        WorkItem workItem = new WorkItem();
        workItem.setActivity(activity);
        workItem.setWorklist(worklistEntity);

        return workItem;
    }

    @RequestMapping(value = "/work-item/{taskId}", method = RequestMethod.POST)
    public void putWorkItem(@PathVariable("taskId") String definitionPath, @RequestBody WorkItem workItem) throws Exception {
        Long taskId = workItem.getWorklist().getTaskId();

        Long instId = workItem.getWorklist().getInstId();

        ProcessDefinition definition = (ProcessDefinition) definitionService.getDefinitionLocal(workItem.getWorklist().getDefId());
        org.uengine.kernel.ProcessInstance instance = applicationContext.getBean(
                org.uengine.kernel.ProcessInstance.class,
                new Object[]{
                        definition,
                        instId.toString(),
                        null
                }
        );

        HumanActivity humanActivity = ((HumanActivity)instance.getProcessDefinition().getActivity(workItem.getWorklist().getTrcTag()));

        if(!instance.isRunning(humanActivity.getTracingTag()) && !humanActivity.isNotificationWorkitem()){
            throw new UEngineException("Illegal completion for workitem [" + humanActivity + ":"+ humanActivity.getStatus(instance) +"]: Already closed or illegal status.");
        }


        if("saved".equals(workItem.getDesiredState())){
            humanActivity.saveWorkItem(instance, null);
        }else{
            try{
                humanActivity.fireReceived(instance, null);
            }catch(Exception e){
                humanActivity.fireFault(instance, e);

                throw new UEngineException(e.getMessage(), null, new UEngineException(e.getMessage(), e), instance, humanActivity);
            }
        }


    }


    @Autowired
    ApplicationContext applicationContext;




}
