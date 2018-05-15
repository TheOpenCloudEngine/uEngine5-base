package org.uengine.five.overriding;

import org.metaworks.dwr.MetaworksRemoteService;
import org.uengine.five.entity.AuditEntity;
import org.uengine.five.repository.AuditEntityRepository;
import org.uengine.kernel.Activity;
import org.uengine.kernel.ActivityFilter;
import org.uengine.kernel.ProcessDefinition;
import org.uengine.kernel.ProcessInstance;

/**
 * Created by uengine on 2017. 12. 21..
 */
public class InstanceNameFilter implements ActivityFilter {
    @Override
    public void beforeExecute(Activity activity, ProcessInstance instance) throws Exception {

    }

    @Override
    public void afterExecute(Activity activity, ProcessInstance instance) throws Exception {
        if(activity.getProcessDefinition().getInstanceNamePattern()!=null){
            StringBuffer instanceName = activity.getProcessDefinition().evaluateContent(instance, activity.getProcessDefinition().getInstanceNamePattern());

            if(instanceName!=null){
                instance.setName(instanceName.toString());
            }
        }

    }

    @Override
    public void afterComplete(Activity activity, ProcessInstance instance) throws Exception {

    }

    @Override
    public void onPropertyChange(Activity activity, ProcessInstance instance, String propertyName, Object changedValue) throws Exception {

    }

    @Override
    public void onDeploy(ProcessDefinition definition) throws Exception {

    }
}
