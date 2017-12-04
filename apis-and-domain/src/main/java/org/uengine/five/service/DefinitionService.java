package org.uengine.five.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.*;

/**
 * Created by uengine on 2017. 8. 9..
 *
 * Implementation Principles:
 *  - REST Maturity Level : 3 (Hateoas)
 *  - Not using old uEngine ProcessManagerBean, this replaces the ProcessManagerBean
 *  - ResourceManager and CachedResourceManager will be used for definition caching (Not to use the old DefinitionFactory)
 *  - json must be Typed JSON to enable object polymorphism - need to change the jackson engine. TODO: accept? typed json is sometimes hard to read
 */
@FeignClient("definition")
public interface DefinitionService {

    public static final String DEFINITION_RAW = "/definition/raw";
    public static final String DEFINITION = "/definition";

    @RequestMapping(value = DEFINITION, method = RequestMethod.GET)
    public ResourceSupport listDefinition(String basePath) throws Exception;


    @RequestMapping(value = DEFINITION + "/{defPath}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResourceSupport getDefinition(@PathVariable("defPath") String definitionPath) throws Exception;


    // ----------------- raw definition services -------------------- //


//    @RequestMapping(value = DEFINITION_RAW + "/{defPath}", method = RequestMethod.GET)
//    public DefinitionWrapper getRawDefinition(@PathVariable("defPath") String definitionPath) throws Exception;


    @RequestMapping(value = DEFINITION + "/xml/{defPath}", method = RequestMethod.GET, produces = "application/xml;charset=UTF-8")
    public String getXMLDefinition(@PathVariable("defPath") String definitionPath) throws Exception;

}