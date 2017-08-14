package org.uengine.five;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.uengine.kernel.DefaultProcessInstance;
import org.uengine.kernel.ProcessDefinition;
import org.uengine.social.service.instance.ProcessInstanceEntity;
import org.uengine.social.service.instance.ProcessInstanceRepository;
import org.uengine.webservices.worklist.WorkList;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by uengine on 2017. 8. 9..
 */
@Component
@Scope("prototype")
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

    public JPAProcessInstance(ProcessDefinition procDefinition, String instanceId, Map options) throws Exception {
        super(procDefinition, instanceId, options);

        setProcessInstanceEntity(new ProcessInstanceEntity());
        getProcessInstanceEntity().setName(instanceId);
    }

    @PostConstruct
    public void init(){
        processInstanceRepository.save(getProcessInstanceEntity());
        setInstanceId(String.valueOf(getProcessInstanceEntity().getInstId()));
    }



    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(){



    }

    @Override
    public WorkList getWorkList() {
        return jpaWorkList;
    }

    @Autowired
    JPAWorkList jpaWorkList;
}
