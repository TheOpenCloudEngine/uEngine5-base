package org.uengine.social.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.uengine.kernel.*;
import org.uengine.modeling.resource.ResourceManager;
import org.uengine.social.entity.WorklistEntity;
import org.uengine.social.repository.WorklistRepository;

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
public class RoleMappingService {

    // ---------------- CRUD mappings -------------------- //

    @RequestMapping(value = "/instance/{instId}/role-mapping/{roleName}", method = RequestMethod.GET)
    public RoleMapping getRoleMapping(@PathVariable("instId") String instId, @PathVariable("roleName") String roleName) throws Exception {

        ProcessInstance instance = applicationContext.getBean(
                ProcessInstance.class,
                new Object[]{
                        null,
                        instId,
                        null
                }
        );

        return instance.getRoleMapping(roleName);
    }

    @RequestMapping(value = "/instance/{instId}/role-mapping/{roleName}", method = RequestMethod.POST)
    public RoleMapping setRoleMapping(@PathVariable("instId") String instId, @PathVariable("roleName") String roleName, @RequestBody RoleMapping roleMapping) throws Exception {

        ProcessInstance instance = applicationContext.getBean(
                ProcessInstance.class,
                new Object[]{
                        null,
                        instId,
                        null
                }
        );

        instance.putRoleMapping(roleName, roleMapping);

        roleMapping.setName(roleName);

        return roleMapping;
    }


    @Autowired
    ApplicationContext applicationContext;

}
