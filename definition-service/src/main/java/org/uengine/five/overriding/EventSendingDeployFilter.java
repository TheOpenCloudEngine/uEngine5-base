package org.uengine.five.overriding;

import org.metaworks.dwr.MetaworksRemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.metaworks.eventsourcing.EventSender;
import org.springframework.context.annotation.Lazy;
import org.uengine.kernel.Activity;
import org.uengine.kernel.DeployFilter;
import org.uengine.kernel.ProcessDefinition;
import org.uengine.kernel.bpmn.CatchingRestMessageEvent;
import org.uengine.processmanager.ProcessTransactionContext;

import java.util.List;

/**
 * Created by uengine on 2018. 1. 5..
 */
public class EventSendingDeployFilter implements DeployFilter {



    @Override
    public void beforeDeploy(ProcessDefinition definition, ProcessTransactionContext tc, String path, boolean isNew) throws Exception {
        EventSender eventSender = MetaworksRemoteService.getComponent(EventSender.class);

        //eventSender.sendBusinessEvent("deploy:" + path);

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
