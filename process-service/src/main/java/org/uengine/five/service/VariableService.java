package org.uengine.five.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.uengine.five.framework.ProcessTransactional;
import org.uengine.kernel.DefaultProcessInstance;
import org.uengine.kernel.ProcessInstance;

import java.io.Serializable;
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
public class VariableService {

    // ---------------- CRUD mappings -------------------- //


    @Autowired
    InstanceServiceImpl instanceService;

    @RequestMapping(value = "/instance/{instanceId}/variables", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ProcessTransactional(readOnly = true)
    public Map getProcessVariables(@PathVariable("instanceId") String instanceId) throws Exception {

        ProcessInstance instance = instanceService.getProcessInstanceLocal(instanceId);

        //여기서도 롤매핑이 들어가면 시리얼라이즈 에러가 나옴.
        Map variables = ((DefaultProcessInstance) instance).getVariables();

        return variables;
    }


    @RequestMapping(value = "/instance/{instId}/variable/{varName}", method = RequestMethod.GET)
    @ProcessTransactional(readOnly = true)
    public Serializable getVariable(@PathVariable("instId") String instId, @PathVariable("varName") String varName) throws Exception {
        ProcessInstance instance = instanceService.getProcessInstanceLocal(instId);
        return instance.get("",varName);
    }

    @RequestMapping(value = "/instance/{instanceId}/variable/{varName}", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    @ProcessTransactional
    public void setVariable(@PathVariable("instanceId") String instanceId, @PathVariable("varName") String varName, @RequestParam("varValue") String varValue) throws Exception {
        ProcessInstance instance = instanceService.getProcessInstanceLocal(instanceId);
        instance.set("",varName, (Serializable) varValue);
    }

    @Autowired
    ApplicationContext applicationContext;

}
