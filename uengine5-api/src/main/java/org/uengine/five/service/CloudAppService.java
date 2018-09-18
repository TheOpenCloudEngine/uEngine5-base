package org.uengine.five.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.uengine.cloud.app.AppCreate;
import org.uengine.cloud.app.AppEntity;

import java.util.Map;

/**
 * Created by uengine on 2018. 2. 6..
 */
@FeignClient("uengine-cloud-server")
public interface CloudAppService {

    @RequestMapping(value = "/app", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public AppEntity createApp(@RequestBody AppCreate appCreate, @RequestHeader("access_token") String token) throws Exception;

    @RequestMapping(value = "/app/{appName}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Map getApp(@PathVariable("appName") String appName, @RequestHeader("access_token") String token);


}