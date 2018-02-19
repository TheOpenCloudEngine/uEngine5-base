package org.uengine.five.entity;


import org.metaworks.annotation.RestAssociation;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by uengine on 2017. 8. 1..
 */
@Entity
@Table(name = "BPM_WORKLIST")
public class WorklistEntity {//implements WorkListDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long taskId;


    Long instId;
    @PrimaryKeyJoinColumn
        public Long getInstId() {
            return instId;
        }
        public void setInstId(Long instId) {
            this.instId = instId;
        }

    @ManyToOne
    @PrimaryKeyJoinColumn(name="instId")
    ProcessInstanceEntity processInstance;
        public ProcessInstanceEntity getProcessInstance() {
            return processInstance;
        }
        public void setProcessInstance(ProcessInstanceEntity processInstance) {
            this.processInstance = processInstance;
        }

    @Transient
    @RestAssociation(path="/work-item/{taskId}")
    String workItem;

    String title;
    String description;
    String endpoint;
    String roleName;
    String refRoleName;
    String resName;
    String defId;
    String defName;
    String trcTag;
    String tool;
    String parameter;
    Number priority;
    
    @Temporal(TemporalType.DATE)
    Date startDate;
    
    @Temporal(TemporalType.DATE)
    Date endDate;
    
    @Temporal(TemporalType.DATE)
    Date saveDate;
    
    @Temporal(TemporalType.DATE)
    Date dueDate;
    
    String status;
    int dispatchOption;
    String dispatchParam1;
    String prevUserName;
    Number rootInstId;
    
    @Temporal(TemporalType.DATE)    
    Date readDate;
    
    String actType;
    String absTrcTag;
    Boolean delegated;
    Boolean urget;
    String execScope;
    String ext1;
    String ext2;
    String ext3;
    String ext4;
    String ext5;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRefRoleName() {
        return refRoleName;
    }

    public void setRefRoleName(String refRoleName) {
        this.refRoleName = refRoleName;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }



    public String getDefId() {
        return defId;
    }

    public void setDefId(String defId) {
        this.defId = defId;
    }

    public String getDefName() {
        return defName;
    }

    public void setDefName(String defName) {
        this.defName = defName;
    }

    public String getTrcTag() {
        return trcTag;
    }

    public void setTrcTag(String trcTag) {
        this.trcTag = trcTag;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Number getPriority() {
        return priority;
    }

    public void setPriority(Number priority) {
        this.priority = priority;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(Date saveDate) {
        this.saveDate = saveDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDispatchOption() {
        return dispatchOption;
    }

    public void setDispatchOption(int dispatchOption) {
        this.dispatchOption = dispatchOption;
    }

    public String getDispatchParam1() {
        return dispatchParam1;
    }

    public void setDispatchParam1(String dispatchParam1) {
        this.dispatchParam1 = dispatchParam1;
    }

    public String getPrevUserName() {
        return prevUserName;
    }

    public void setPrevUserName(String prevUserName) {
        this.prevUserName = prevUserName;
    }

    public Number getRootInstId() {
        return rootInstId;
    }

    public void setRootInstId(Number rootInstId) {
        this.rootInstId = rootInstId;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public String getAbsTrcTag() {
        return absTrcTag;
    }

    public void setAbsTrcTag(String absTrcTag) {
        this.absTrcTag = absTrcTag;
    }

    public Boolean getDelegated() {
        return delegated;
    }

    public void setDelegated(Boolean delegated) {
        this.delegated = delegated;
    }

    public Boolean getUrget() {
        return urget;
    }

    public void setUrget(Boolean urget) {
        this.urget = urget;
    }

    public String getExecScope() {
        return execScope;
    }

    public void setExecScope(String execScope) {
        this.execScope = execScope;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    public String getExt4() {
        return ext4;
    }

    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }

    public String getExt5() {
        return ext5;
    }

    public void setExt5(String ext5) {
        this.ext5 = ext5;
    }


}
