package org.uengine.five.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.uengine.five.framework.ProcessTransactional;
import org.uengine.kernel.*;

import java.io.Serializable;

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

    @RequestMapping(value = "/instance/{instId}/variable/{varName}", method = RequestMethod.GET)
    @ProcessTransactional(readOnly = true)
    public Serializable getVariable(@PathVariable("instId") String instId, @PathVariable("varName") String varName) throws Exception {

        ProcessInstance instance = instanceService.getProcessInstanceLocal(instId);
        /*ProcessVariable processVariable = (ProcessVariable) instance.get("",varName);
        if ( processVariable == null){
            processVariable =  instance.getProcessDefinition().getProcessVariable(varName);
        }*/

        return instance.get("",varName);
    }

    @RequestMapping(value = "/instance/{instanceId}/variable/{varName}", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    @ProcessTransactional
    public Object setVariable(@PathVariable("instanceId") String instanceId, @PathVariable("varName") String varName, @RequestBody  Object varValue) throws Exception {
        ProcessInstance instance = instanceService.getProcessInstanceLocal(instanceId);
        instance.set("",varName, (Serializable)varValue);
        return varValue;
    }


    @Autowired
    ApplicationContext applicationContext;

}
