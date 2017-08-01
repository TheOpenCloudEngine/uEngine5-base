package org.uengine.social;

import javax.persistence.*;

/**
 * Created by uengine on 2017. 8. 1..
 */
@Entity
@Table(name = "BPM_ROLEMAPPING")
public class RoleMapping {//implements RoleMappingDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long roleMappingId;

//    @ManyToOne(targetEntity = ProcessInstance.class)
//    Long instId;
//
//    @ManyToOne(targetEntity = ProcessInstance.class)
//    Long rootInstId;

    @ManyToOne
    @JoinColumn(name="instId")
    ProcessInstance processInstance;
    public ProcessInstance getProcessInstance() {
        return processInstance;
    }
    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    String roleName;

    String value;

    String endpoint;

    String resName;

    String groupId;

    Number assignType;

    String assignParam1;

    String dispatchParam1;

    Number dispatchOption;


    public Long getRoleMappingId() {
        return roleMappingId;
    }

    public void setRoleMappingId(Long roleMappingId) {
        this.roleMappingId = roleMappingId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Number getAssignType() {
        return assignType;
    }

    public void setAssignType(Number assignType) {
        this.assignType = assignType;
    }

    public String getAssignParam1() {
        return assignParam1;
    }

    public void setAssignParam1(String assignParam1) {
        this.assignParam1 = assignParam1;
    }

    public String getDispatchParam1() {
        return dispatchParam1;
    }

    public void setDispatchParam1(String dispatchParam1) {
        this.dispatchParam1 = dispatchParam1;
    }

    public Number getDispatchOption() {
        return dispatchOption;
    }

    public void setDispatchOption(Number dispatchOption) {
        this.dispatchOption = dispatchOption;
    }
}
