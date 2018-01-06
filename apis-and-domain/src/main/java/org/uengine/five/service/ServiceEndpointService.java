package org.uengine.five.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.uengine.five.entity.ServiceEndpointEntity;

/**
 * Created by uengine on 2018. 1. 5..
 */
@FeignClient("bpm/service")
public interface ServiceEndpointService {//extends GenericService<ServiceEndpointEntity> {
}
