package org.uengine.five.service;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.*;

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
public interface WorkItemService {

    // ---------------- CRUD mappings -------------------- //

    @RequestMapping(value = "/work-item/{taskId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResourceSupport getWorkItem(@PathVariable("taskId") String taskId) throws Exception;

    @RequestMapping(value = "/work-item/{taskId}", method = RequestMethod.POST)
    public void putWorkItem(@PathVariable("taskId") String taskId, @RequestBody ResourceSupport workItem) throws Exception;


}
