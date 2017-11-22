package org.uengine.five.overriding;

import org.metaworks.dwr.MetaworksRemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.uengine.five.entity.ProcessInstanceEntity;
import org.uengine.five.framework.ProcessTransactionContext;
import org.uengine.five.framework.ProcessTransactionListener;
import org.uengine.five.repository.ProcessInstanceRepository;
import org.uengine.five.service.DefinitionService;
import org.uengine.five.service.DefinitionServiceUtil;
import org.uengine.five.service.InstanceService;
import org.uengine.five.service.InstanceServiceImpl;
import org.uengine.kernel.*;
import org.uengine.modeling.resource.DefaultResource;
import org.uengine.modeling.resource.IResource;
import org.uengine.modeling.resource.ResourceManager;
import org.uengine.webservices.worklist.WorkList;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Map;

/**
 * Created by uengine on 2017. 8. 9..
 */
//@Component
@Transactional
//@Scope("prototype")
public class JPAProcessInstance extends DefaultProcessInstance implements ProcessTransactionListener {

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


    boolean prototype;
    public boolean isPrototype() {
        return prototype;
    }
    public void setPrototype(boolean prototype) {
        this.prototype = prototype;
    }


    public JPAProcessInstance(ProcessDefinition procDefinition, String instanceId, Map options) throws Exception {
        super(procDefinition, instanceId, options);

        if (instanceId == null && procDefinition == null){ //return prototype only
            setPrototype(true);
            return;
        }

        if(instanceId==null){ //means new InstanceResource, if exists, loading existing instance.
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

            if(options != null){
                if(options.containsKey(DefaultProcessInstance.ROOT_PROCESS)) {
                    getProcessInstanceEntity().setRootInstId(new Long((String) options.get(DefaultProcessInstance.ROOT_PROCESS)));
                }

                if(options.containsKey(DefaultProcessInstance.SIMULATIONPROCESS)){
                    setSimulation(true);
                }
            }

        }
    }

//    @Autowired
//    ApplicationEventPublisher applicationEventPublisher; //TODO see the DefinitionService.beforeProcessInstanceCommit() and move to here someday

    @PostConstruct
    public void init() throws Exception {

        if(isPrototype()) return;

        if(isNewInstance()) { //if new instance, create one
            processInstanceRepository.save(getProcessInstanceEntity());

            if(getProcessInstanceEntity().getRootInstId()==null){
                getProcessInstanceEntity().setRootInstId(getProcessInstanceEntity().getInstId());
            }

        }else{ //else, load the instance
            setProcessInstanceEntity(processInstanceRepository.findOne(Long.valueOf(getInstanceId())));

            Map variables = loadVariables();

            setVariables(variables);
        }


        setInstanceId(String.valueOf(getProcessInstanceEntity().getInstId()));

        //Add this instance as transaction listener and register this so that it can be cached.
        ProcessTransactionContext.getThreadLocalInstance().addTransactionListener(this);
        ProcessTransactionContext.getThreadLocalInstance().registerProcessInstance(this);

//        applicationEventPublisher.publishEvent(new ProcessInstanceChangeEvent(this));
    }

    protected Map loadVariables() throws Exception {
        IResource resource = new DefaultResource("instances/" + getInstanceId());
        return (Map) resourceManager.getObject(resource);
    }

    @Autowired
    DefinitionServiceUtil definitionService;

    @Override
    public ProcessDefinition getProcessDefinition() throws Exception {
        ProcessDefinition definition = super.getProcessDefinition();

        if(definition==null){
            definition = (ProcessDefinition) definitionService.getDefinition(processInstanceEntity.getDefId());
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

    @Override
    public ProcessInstance getInstance(String instanceId, Map options) throws Exception {

        return instanceService.getProcessInstanceLocal(instanceId);
    }


    @Override
    public String getRootProcessInstanceId() {
        return String.valueOf(getProcessInstanceEntity().getRootInstId());
    }

    @Override
    public String getMainProcessInstanceId() {
        return String.valueOf(getProcessInstanceEntity().getMainInstId());
    }

    @Override
    public String getMainActivityTracingTag() {
        return String.valueOf(getProcessInstanceEntity().getMainActTrcTag());
    }

    @Override
    public RoleMapping getRoleMapping(String roleName) throws Exception {
        return super.getRoleMapping(roleName);
    }

    @Override
    public void putRoleMapping(RoleMapping roleMap) throws Exception {
        super.putRoleMapping(roleMap);
    }

    @Autowired
    InstanceServiceImpl instanceService;

    @Override
    public ProcessInstance getMainProcessInstance() throws Exception {
        if(getMainProcessInstanceId() == null) return null;

        return instanceService.getProcessInstanceLocal(getMainProcessInstanceId());

    }

    @Override
    public ProcessInstance getRootProcessInstance() throws Exception {
        if(getRootProcessInstanceId() == null) return null;

        return instanceService.getProcessInstanceLocal(getRootProcessInstanceId());
    }

    @Override
    public void beforeCommit(ProcessTransactionContext tx) throws Exception {

        processInstanceRepository.save(getProcessInstanceEntity());

        saveVariables();
    }

    protected void saveVariables() throws Exception {
        IResource resource = new DefaultResource("instances/" + getInstanceId());
        resourceManager.save(resource, getVariables());
    }

    @Override
    public void beforeRollback(ProcessTransactionContext tx) throws Exception {

    }

    @Override
    public void afterCommit(ProcessTransactionContext tx) throws Exception {

    }

    @Override
    public void afterRollback(ProcessTransactionContext tx) throws Exception {

    }
}
