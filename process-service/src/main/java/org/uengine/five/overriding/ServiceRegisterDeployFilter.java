package org.uengine.five.overriding;

import org.metaworks.dwr.MetaworksRemoteService;
import org.uengine.five.entity.ServiceEndpointEntity;
import org.uengine.five.repository.ServiceEndpointRepository;
import org.uengine.kernel.Activity;
import org.uengine.kernel.DeployFilter;
import org.uengine.kernel.ProcessDefinition;
import org.uengine.kernel.bpmn.CatchingRestMessageEvent;
import org.uengine.processmanager.ProcessTransactionContext;

import java.util.List;

/**
 * Created by uengine on 2018. 1. 8..
 */
public class ServiceRegisterDeployFilter implements DeployFilter {

    @Override
    public void beforeDeploy(ProcessDefinition definition, ProcessTransactionContext tc, String path, boolean isNew) throws Exception {

        if(!definition.isInitiateByFirstWorkitem()) return;

        List<Activity> startActivities = definition.getStartActivities();

        for(Activity activity : startActivities){

            if(activity instanceof CatchingRestMessageEvent){

                CatchingRestMessageEvent catchingMessageEvent = (CatchingRestMessageEvent) activity;

                ServiceEndpointRepository serviceEndpointRepository = MetaworksRemoteService.getComponent(ServiceEndpointRepository.class);

                ServiceEndpointEntity serviceEndpointEntity = new ServiceEndpointEntity();

                serviceEndpointEntity.setPath(catchingMessageEvent.getServicePath());
                serviceEndpointEntity.setCorrelationKey(catchingMessageEvent.getCorrelationKey());
                serviceEndpointEntity.setDefId(path);

                serviceEndpointRepository.save(serviceEndpointEntity);

            }

        }

    }
}
