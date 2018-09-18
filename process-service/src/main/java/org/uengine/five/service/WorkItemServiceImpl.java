package org.uengine.five.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.uengine.five.ProcessServiceApplication;
import org.uengine.five.entity.WorklistEntity;
import org.uengine.five.framework.ProcessTransactional;
import org.uengine.five.overriding.RestResourceProcessVariableValue;
import org.uengine.five.repository.WorklistRepository;
import org.uengine.kernel.*;
import org.uengine.modeling.resource.ResourceManager;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by uengine on 2017. 8. 9..
 * <p>
 * Implementation Principles: - REST Maturity Level : 2
 * - Not using old uEngine ProcessManagerBean, this replaces the ProcessManagerBean
 * - ResourceManager and CachedResourceManager will be used for definition caching (Not to use the old DefinitionFactory)
 * - json must be Typed JSON to enable object polymorphism
 * - need to change the jackson engine.
 * TODO: accept? typed json is sometimes hard to read
 */
@RestController
public class WorkItemServiceImpl {

    @Autowired
    ResourceManager resourceManager;

    @Autowired
    DefinitionServiceUtil definitionService; // bind automatically for the remote api?

    @Autowired
    WorklistRepository worklistRepository;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    InstanceServiceImpl instanceService;

    @PostConstruct
    public void init() {
    }

    @RequestMapping(value = "/work-item/{taskId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public WorkItemResource getWorkItem(@PathVariable("taskId") String taskId) throws Exception {

        WorklistEntity worklistEntity = worklistRepository.findById(new Long(taskId)).orElse(null);
        if (worklistEntity == null) {
            throw new Exception("No such work item where taskId = " + taskId);
        }

        String defId = worklistEntity.getDefId();
        ProcessDefinition definition = (ProcessDefinition) definitionService.getDefinition(defId);
        HumanActivity activity = (HumanActivity) definition.getActivity(worklistEntity.getTrcTag());

        WorkItemResource workItem = new WorkItemResource();
        workItem.setActivity(activity);
        workItem.setWorklist(worklistEntity);

        String instanceId = worklistEntity.getInstId().toString();
        ProcessInstance instance = instanceService.getProcessInstanceLocal(instanceId);

        // get the parameter values and set them to the "workItem.parameterValues" so that WorkItemHandler.vue can insert the default values
        Map parameterValues = new HashMap<String, Object>();
        if (activity.getParameters() != null) {
            for (ParameterContext parameterContext : activity.getParameters()) {
                if (parameterContext.getDirection().indexOf("IN") == 0) {
                    parameterValues.put(parameterContext.getArgument().getText(),
                            parameterContext.getVariable().get(instance, "", ""));
                }
            }
        }

        if (parameterValues.size() > 0) {
            workItem.setParameterValues(parameterValues);
        }

        workItem.worklist.setProcessInstance(null); //disconnect recursive json path

        return workItem;
    }

    @RequestMapping(value = "/work-item/{taskId}", method = RequestMethod.POST)
    @org.springframework.transaction.annotation.Transactional
    @ProcessTransactional // important!
    public void putWorkItem(@PathVariable("taskId") String taskId, @RequestBody WorkItemResource workItem) throws Exception {

        WorklistEntity worklistEntity = worklistRepository.findById(new Long(taskId)).orElse(null);

        String instanceId = worklistEntity.getInstId().toString();
        ProcessInstance instance = instanceService.getProcessInstanceLocal(instanceId);

        HumanActivity humanActivity = ((HumanActivity) instance.getProcessDefinition().getActivity(worklistEntity.getTrcTag()));

        if (!instance.isRunning(humanActivity.getTracingTag()) && !humanActivity.isNotificationWorkitem()) {
            throw new UEngineException("Illegal completion for workitem [" + humanActivity + ":"
                    + humanActivity.getStatus(instance) + "]: Already closed or illegal status.");
        }

        // map the argument list to variables change list
        Map variableChanges = new HashMap<String, Object>();

        if (workItem.getParameterValues() != null
                && humanActivity.getParameters() != null) {
            for (ParameterContext parameterContext : humanActivity.getParameters()) {
                if (parameterContext.getDirection().indexOf("OUT") >= 0
                        && workItem.getParameterValues().containsKey(parameterContext.getArgument().getText())) {

                    Serializable data = (Serializable) workItem.getParameterValues().get(parameterContext.getArgument().getText());
                    if ("REST".equals(parameterContext.getVariable().getPersistOption())) {
                        RestResourceProcessVariableValue restResourceProcessVariableValue = new RestResourceProcessVariableValue();
                        data = restResourceProcessVariableValue.lightweight(data, parameterContext.getVariable(), instance);
                    }


                    if (data instanceof Map && ((Map) data).containsKey("_type")) {
                        String typeName = null;
                        try {
                            typeName = (String) ((Map) data).get("_type");
                            Class classType = Thread.currentThread().getContextClassLoader().loadClass(typeName);
                            data = (Serializable) ProcessServiceApplication.objectMapper.convertValue(data, classType);
                        } catch (Exception e) {
                            throw new Exception("Error while convert map to type: " + typeName, e);
                        }
                    }

                    variableChanges.put(parameterContext.getVariable().getName(),
                            data);
                }
            }
        }

        if ("SAVED".equals(workItem.getWorklist().getStatus())) {
            humanActivity.saveWorkItem(instance, variableChanges);
        } else {
            try {
                humanActivity.fireReceived(instance, variableChanges);
            } catch (Exception e) {
                humanActivity.fireFault(instance, e);

                throw new UEngineException(e.getMessage(), null, new UEngineException(e.getMessage(), e), instance, humanActivity);
            }
        }

    }

}
