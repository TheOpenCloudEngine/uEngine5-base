package org.uengine.five;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.uengine.kernel.DefaultProcessInstance;
import org.uengine.kernel.ProcessDefinition;
import org.uengine.kernel.ProcessInstance;
import org.uengine.modeling.resource.DefaultResource;
import org.uengine.modeling.resource.IResource;
import org.uengine.modeling.resource.ResourceManager;
import org.uengine.social.entity.ProcessInstanceEntity;
import org.uengine.social.repository.ProcessInstanceRepository;
import org.uengine.social.service.DefinitionService;
import org.uengine.webservices.worklist.WorkList;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Map;

/**
 * Created by uengine on 2017. 8. 9..
 */
@Component
@Transactional
//@Scope("prototype")
public class JPAProcessInstance extends DefaultProcessInstance {

    ProcessInstanceEntity processInstanceEntity;
        public ProcessInstanceEntity getProcessInstanceEntity() {
            return processInstanceEntity;
        }
        public void setProcessInstanceEntity(ProcessInstanceEntity processInstanceEntity) {
            this.processInstanceEntity = processInstanceEntity;
        }

    @Autowired
    ProcessInstanceRepository processInstanceRepository;

    boolean newInstance;

        public boolean isNewInstance() {
            return newInstance;
        }

        public void setNewInstance(boolean newInstance) {
            this.newInstance = newInstance;
        }

    public JPAProcessInstance(ProcessDefinition procDefinition, String instanceId, Map options) throws Exception {
        super(procDefinition, instanceId, options);

        if(instanceId==null){
            setNewInstance(true);
            setProcessInstanceEntity(new ProcessInstanceEntity());
            getProcessInstanceEntity().setName(instanceId);
            getProcessInstanceEntity().setDefId(procDefinition.getId());
        }
    }

    @Autowired
    ApplicationEventPublisher applicationEventPublisher; //TODO see the DefinitionService.beforeProcessInstanceCommit() and move to here someday

    @PostConstruct
    public void init() throws Exception {

        if(isNewInstance()) { //if new instance, create one
            processInstanceRepository.save(getProcessInstanceEntity());
        }else{ //else, load the instance
            setProcessInstanceEntity(processInstanceRepository.findOne(Long.valueOf(getInstanceId())));

            IResource resource = new DefaultResource("instances/" + getInstanceId());
            Map variables = (Map) resourceManager.getObject(resource);

            setVariables(variables); //variable from CGLIB cannot change the base class?
        }

        setInstanceId(String.valueOf(getProcessInstanceEntity().getInstId()));

        applicationEventPublisher.publishEvent(new ProcessInstanceChangeEvent(this));
    }

    @Autowired
    DefinitionService definitionService;

    @Override
    public ProcessDefinition getProcessDefinition() throws Exception {
        ProcessDefinition definition = super.getProcessDefinition();

        if(definition==null){
            definition = (ProcessDefinition) definitionService.getDefinitionLocal(processInstanceEntity.getDefId());
        }

        return definition;
    }


    @Override
    public WorkList getWorkList() {
        return jpaWorkList;
    }

    @Autowired
    JPAWorkList jpaWorkList;

    @Autowired
    ResourceManager resourceManager;
}
