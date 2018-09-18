package org.uengine.five.service;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * Created by uengine on 2018. 1. 5..
 */
@FeignClient("bpm/service")
public interface ServiceEndpointService {//extends GenericService<ServiceEndpointEntity> {
}
