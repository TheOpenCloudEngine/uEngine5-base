package org.uengine.five;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.uengine.kernel.*;
import org.uengine.modeling.resource.DefaultResource;
import org.uengine.modeling.resource.IResource;
import org.uengine.modeling.resource.ResourceManager;
import org.uengine.persistence.dao.UniqueKeyGenerator;
import org.uengine.persistence.processinstance.ProcessInstanceDAOType;
import org.uengine.social.entity.ProcessInstanceEntity;
import org.uengine.social.repository.ProcessInstanceRepository;
import org.uengine.social.service.DefinitionService;
import org.uengine.util.UEngineUtil;
import org.uengine.webservices.worklist.WorkList;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
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

        if(instanceId==null){ //means new Instance, if exists, loading existing instance.
            setNewInstance(true);
            setProcessInstanceEntity(new ProcessInstanceEntity());
            getProcessInstanceEntity().setName(instanceId);
            getProcessInstanceEntity().setDefId(procDefinition.getId());

            getProcessInstanceEntity().setStatus(Activity.STATUS_READY);
            getProcessInstanceEntity().setDefName(procDefinition.getName());

//            if(procDef.getModifiedDate()!=null)
//                processInstanceDAO.setDefModDate(procDef.getModifiedDate().getTime());

//            if(!UEngineUtil.isNotEmpty(name))
//                name = procDef.getName() + instanceId;
//
//            processInstanceDAO.setName(name);
//            setName(name);


            getProcessInstanceEntity().setStartedDate(new Date());

            boolean isSubProcess =
                    (	options!=null
                            && options.containsKey("isSubProcess")
                            && options.get("isSubProcess").equals("yes")
                    );

            if(isSubProcess){
                getProcessInstanceEntity().setIsSubProcess(true);
                getProcessInstanceEntity().setMainInstId(new Long((String)options.get(DefaultProcessInstance.RETURNING_PROCESS)));
                getProcessInstanceEntity().setMainActTrcTag((String)options.get(DefaultProcessInstance.RETURNING_TRACINGTAG));
                getProcessInstanceEntity().setMainExecScope((String)options.get(DefaultProcessInstance.RETURNING_EXECSCOPE));
                getProcessInstanceEntity().setDontReturn(((Boolean)options.get(DefaultProcessInstance.DONT_RETURN)).booleanValue());
                getProcessInstanceEntity().setIsEventHandler(options.containsKey("isEventHandler"));
            }

            if(options.containsKey(DefaultProcessInstance.ROOT_PROCESS)) {
                getProcessInstanceEntity().setRootInstId(new Long((String) options.get(DefaultProcessInstance.ROOT_PROCESS)));
            }

            if(options.containsKey(DefaultProcessInstance.SIMULATIONPROCESS)){
                setSimulation(true);
            }

        }
    }

    @Autowired
    ApplicationEventPublisher applicationEventPublisher; //TODO see the DefinitionService.beforeProcessInstanceCommit() and move to here someday

    @PostConstruct
    public void init() throws Exception {

        if(isNewInstance()) { //if new instance, create one
            processInstanceRepository.save(getProcessInstanceEntity());

            if(getProcessInstanceEntity().getRootInstId()==null){
                getProcessInstanceEntity().setRootInstId(getProcessInstanceEntity().getInstId());
            }

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
