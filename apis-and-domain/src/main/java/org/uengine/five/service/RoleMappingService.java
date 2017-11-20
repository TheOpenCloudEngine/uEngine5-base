package org.uengine.five.service;

import org.springframework.web.bind.annotation.*;
import org.uengine.kernel.*;

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
public interface RoleMappingService {

    // ---------------- CRUD mappings -------------------- //

    @RequestMapping(value = "/instance/{instId}/role-mapping/{roleName}", method = RequestMethod.GET)
    public RoleMapping getRoleMapping(@PathVariable("instId") String instId, @PathVariable("roleName") String roleName) throws Exception;

    //Spring Data rest 에서는 자동객체를 JSON으로 바인딩 해주지만, 원래 스프링에서는 리스폰스에 대해 스프링 프레임웤이 해석할 수 있는 미디어타입을 xml 에 일일히 설정했었음.
    //produces 의 의미는. 리스폰스 헤더에 콘텐트타입을 설정해줌. 그래야 브라우저가 json 객체로 받아들인다.
    @RequestMapping(value = "/instance/{instanceId}/role-mapping/{roleName}", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public Object setRoleMapping(@PathVariable("instanceId") String instanceId, @PathVariable("roleName") String roleName, @RequestBody RoleMapping roleMapping) throws Exception;

}
