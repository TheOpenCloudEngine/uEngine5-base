package org.uengine.five.overriding;

import org.uengine.kernel.*;
import org.uengine.kernel.bpmn.CatchingRestMessageEvent;
import org.uengine.processmanager.ProcessTransactionContext;

import java.util.List;

/**
 * Created by uengine on 2018. 1. 5..
 */
public class ServiceRegisterDeployFilter implements DeployFilter{
    @Override
    public void beforeDeploy(ProcessDefinition definition, ProcessTransactionContext tc, String folder, boolean isNew) throws Exception {
        List<Activity> startActivities = definition.getStartActivities();

        for(Activity activity : startActivities){

            if(activity instanceof CatchingRestMessageEvent){

                CatchingRestMessageEvent catchingMessageEvent = (CatchingRestMessageEvent) activity;

//                ServiceEndpointRepository serviceEndpointRepository = MetaworksRemoteService.getComponent(ServiceEndpointRepository.class);
//
//                ServiceEndpointEntity serviceEndpointEntity = new ServiceEndpointEntity();
//
//                serviceEndpointEntity.setPath(catchingMessageEvent.getServicePath());
//                serviceEndpointEntity.setCorrelationKey(catchingMessageEvent.getCorrelationKey());
//                serviceEndpointEntity.setDefId(folder);
//
//                serviceEndpointRepository.save(serviceEndpointEntity);

            }

        }

    }
}
