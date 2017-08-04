package org.uengine.social.service.rolemapping;

import org.uengine.social.service.instance.ProcessInstance;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by uengine on 2017. 8. 1..
 */
@Entity
@Table(name = "BPM_ROLEMAPPING")
public class RoleMapping implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long roleMappingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instId")
    private ProcessInstance processInstance;

    private String roleName;
    private String value;
    private String endpoint;
    private String resName;
    private String groupId;
    private Number assignType;
    private String assignParam1;
    private String dispatchParam1;
    private Number dispatchOption;

    public Long getRoleMappingId() {
        return roleMappingId;
    }

    public void setRoleMappingId(Long roleMappingId) {
        this.roleMappingId = roleMappingId;
    }

    public ProcessInstance getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
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
