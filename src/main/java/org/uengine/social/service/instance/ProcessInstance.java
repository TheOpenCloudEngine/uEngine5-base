package org.uengine.social.service.instance;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.data.annotation.*;
import org.uengine.social.common.configuration.ApplicationContextRegistry;
import org.uengine.social.common.repository.BeforeSave;
import org.uengine.social.service.rolemapping.RoleMapping;
import org.uengine.social.service.worklist.WorkList;
import org.uengine.social.service.worklist.WorkListRepository;

import javax.persistence.*;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

class DummyWorkList extends WorkList {
}

class DummyRoleMapping extends RoleMapping {
}

/**
 * Created by uengine on 2017. 6. 19..
 */
@Entity
@Table(name = "BPM_PROCINST")
public class ProcessInstance implements Serializable {

    @Transient
    private List<DummyWorkList> dummyWorkLists;

    public List<DummyWorkList> getDummyWorkLists() {
        return dummyWorkLists;
    }

    public void setDummyWorkLists(List<DummyWorkList> dummyWorkLists) {
        this.dummyWorkLists = dummyWorkLists;
    }

    @Transient
    private List<DummyRoleMapping> dummyRoleMappings;

    public List<DummyRoleMapping> getDummyRoleMappings() {
        return dummyRoleMappings;
    }

    public void setDummyRoleMappings(List<DummyRoleMapping> dummyRoleMappings) {
        this.dummyRoleMappings = dummyRoleMappings;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long instId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "processInstance")
    private List<WorkList> workLists;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "processInstance")
    private List<RoleMapping> roleMappings;

    private String defVerId;
    private String defId;
    private String defPath;
    private String defName;
    private Date startedDate;
    private Date finishedDate;
    private Date dueDate;
    private Date defModDate;
    private Date modDate;
    private String status;
    private String info;
    private String name;
    private boolean deleted;
    private boolean adhoc;
    private boolean subProcess;
    private boolean eventHandler;
    private Long rootInstId;
    private Long mainInstId;
    private String mainActTrcTag;
    private String mainExecScope;
    private Long mainDefVerId;
    private boolean archive;
    private String absTrcPath;
    private boolean dontReturn;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;
    private String ext5;
    private String initComCd;

    public Long getInstId() {
        return instId;
    }

    public void setInstId(Long instId) {
        this.instId = instId;
    }

    public String getDefVerId() {
        return defVerId;
    }

    public void setDefVerId(String defVerId) {
        this.defVerId = defVerId;
    }

    public String getDefId() {
        return defId;
    }

    public void setDefId(String defId) {
        this.defId = defId;
    }

    public String getDefPath() {
        return defPath;
    }

    public void setDefPath(String defPath) {
        this.defPath = defPath;
    }

    public String getDefName() {
        return defName;
    }

    public void setDefName(String defName) {
        this.defName = defName;
    }

    public Date getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(Date startedDate) {
        this.startedDate = startedDate;
    }

    public Date getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(Date finishedDate) {
        this.finishedDate = finishedDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getDefModDate() {
        return defModDate;
    }

    public void setDefModDate(Date defModDate) {
        this.defModDate = defModDate;
    }

    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean getAdhoc() {
        return adhoc;
    }

    public void setAdhoc(boolean adhoc) {
        this.adhoc = adhoc;
    }

    public boolean getSubProcess() {
        return subProcess;
    }

    public void setSubProcess(boolean subProcess) {
        this.subProcess = subProcess;
    }

    public boolean getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(boolean eventHandler) {
        this.eventHandler = eventHandler;
    }

    public Long getRootInstId() {
        return rootInstId;
    }

    public void setRootInstId(Long rootInstId) {
        this.rootInstId = rootInstId;
    }

    public Long getMainInstId() {
        return mainInstId;
    }

    public void setMainInstId(Long mainInstId) {
        this.mainInstId = mainInstId;
    }

    public String getMainActTrcTag() {
        return mainActTrcTag;
    }

    public void setMainActTrcTag(String mainActTrcTag) {
        this.mainActTrcTag = mainActTrcTag;
    }

    public String getMainExecScope() {
        return mainExecScope;
    }

    public void setMainExecScope(String mainExecScope) {
        this.mainExecScope = mainExecScope;
    }

    public Long getMainDefVerId() {
        return mainDefVerId;
    }

    public void setMainDefVerId(Long mainDefVerId) {
        this.mainDefVerId = mainDefVerId;
    }

    public boolean getArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public String getAbsTrcPath() {
        return absTrcPath;
    }

    public void setAbsTrcPath(String absTrcPath) {
        this.absTrcPath = absTrcPath;
    }

    public boolean getDontReturn() {
        return dontReturn;
    }

    public void setDontReturn(boolean dontReturn) {
        this.dontReturn = dontReturn;
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

    public String getInitComCd() {
        return initComCd;
    }

    public void setInitComCd(String initComCd) {
        this.initComCd = initComCd;
    }

    public List<WorkList> getWorkLists() {
        return workLists;
    }

    public void setWorkLists(List<WorkList> workLists) {
        this.workLists = workLists;
    }

    public List<RoleMapping> getRoleMappings() {
        return roleMappings;
    }

    public void setRoleMappings(List<RoleMapping> roleMappings) {
        this.roleMappings = roleMappings;
    }
}
