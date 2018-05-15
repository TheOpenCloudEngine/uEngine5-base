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
public class AuditActivityFilter implements ActivityFilter {
    @Override
    public void beforeExecute(Activity activity, ProcessInstance instance) throws Exception {

    }

    @Override
    public void afterExecute(Activity activity, ProcessInstance instance) throws Exception {
        AuditEntityRepository auditEntityRepository = MetaworksRemoteService.getComponent(AuditEntityRepository.class);

        AuditEntity auditEntity = new AuditEntity();
        auditEntity.setFullTracingTag(activity.getTracingTag()); //TODO: root 를 찾아서 12@3@5  형식으로 ...
        auditEntity.setTracingTag(activity.getTracingTag());
        auditEntity.setRootProcessInstance((((JPAProcessInstance)instance.getRootProcessInstance()).getProcessInstanceEntity()));
        auditEntity.setProcessInstance((((JPAProcessInstance)instance).getProcessInstanceEntity()));
        auditEntity.setActivityName(activity.getName());

        auditEntityRepository.save(auditEntity);

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
