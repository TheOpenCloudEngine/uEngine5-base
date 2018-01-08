package org.uengine.five.overriding;

import org.springframework.beans.factory.annotation.Autowired;
import org.uengine.five.eventsourcing.EventSender;
import org.uengine.kernel.*;
import org.uengine.kernel.bpmn.CatchingRestMessageEvent;
import org.uengine.processmanager.ProcessTransactionContext;

import java.util.List;

/**
 * Created by uengine on 2018. 1. 5..
 */
public class EventSendingDeployFilter implements DeployFilter{

    @Autowired
    EventSender eventSender;

    @Override
    public void beforeDeploy(ProcessDefinition definition, ProcessTransactionContext tc, String path, boolean isNew) throws Exception {

        eventSender.sendBusinessEvent("deploy:" + path);

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
