package org.uengine.five.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.RemoveException;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.metaworks.annotation.AddMetadataLink;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import org.uengine.persistence.processinstance.ProcessInstanceDAO;
import org.uengine.util.dao.AbstractGenericDAO;

class DummyWorkList extends WorklistEntity {
}

class DummyRoleMapping extends RoleMappingEntity {
}

/**
 * Created by uengine on 2017. 6. 19..
 */
@Entity
@Table(name = "BPM_PROCINST")
@RepositoryEventHandler(ProcessInstanceEntity.class)
@Component
@AddMetadataLink
public class ProcessInstanceEntity {//implements ProcessInstanceDAO {

    @Transient
    private List<DummyWorkList> dummyWorkLists;
    private String initEp;
    private String initRsNm;
    private String prevCurrEp;
    private String prevCurrRsNm;
    private String currEp;
    private String currRsNm;

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
    Long instId;

    String defVerId;

    String defId;

    String defPath;

    String defName;

    @Temporal(TemporalType.TIMESTAMP)
    Date startedDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date finishedDate;

    Date dueDate;

    Date defModDate;

    Date modDate;

    String status;

    String info;

    String name;

    boolean deleted;

    boolean adhoc;

    boolean subProcess;

    boolean eventHandler;

    Long rootInstId;

    Long mainInstId;

    String mainActTrcTag;

    String mainExecScope;

    Long mainDefVerId;

    boolean archive;

    String absTrcPath;

    boolean dontReturn;

    String ext1;

    String ext2;

    String ext3;

    String ext4;

    String ext5;

    String initComCd;

    String corrKey;


    @Lob
    byte[] varLob;
        public byte[] getVarLob() {
            return varLob;
        }
        public void setVarLob(byte[] varLob) {
            this.varLob = varLob;
        }


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "processInstance")
    List<WorklistEntity> workLists;

    public List<WorklistEntity> getWorkLists() {
        return workLists;
    }

    public void setWorkLists(List<WorklistEntity> workLists) {
        this.workLists = workLists;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "processInstance")
    List<RoleMappingEntity> roleMappings;

    public List<RoleMappingEntity> getRoleMappings() {
        return roleMappings;
    }

    public void setRoleMappings(List<RoleMappingEntity> roleMappings) {
        this.roleMappings = roleMappings;
    }

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
    
    public boolean isDeleted() {
        return deleted;
    }
    
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    public boolean isAdhoc() {
        return adhoc;
    }
    
    public void setAdhoc(boolean adhoc) {
        this.adhoc = adhoc;
    }

    public boolean isSubProcess() {
        return subProcess;
    }
    
    public void setSubProcess(boolean subProcess) {
        this.subProcess = subProcess;
    }
    
    public boolean isEventHandler() {
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
    
    public boolean isArchive() {
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

    public boolean isDontReturn() {
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

    public Number getStrategyId() {
        return null;
    }

    public void setStrategyId(Number strategyId) {

    }


    public String getInitComCd() {
        return initComCd;
    }

    public void setInitComCd(String initComCd) {
        this.initComCd = initComCd;
    }

    ////// moved from ProcessEventHandler for enhancement in cohesion ///////

    @HandleBeforeCreate
    public void handleBeforeCreate(ProcessInstanceEntity instance) {
        this.saveWithRelations(instance);
    }

//    @HandleBeforeSave
//    public void handleBeforeSave(ProcessInstanceEntity instance) {
//        this.saveWithRelations(instance);
//    }

    private void saveWithRelations(ProcessInstanceEntity instance) {
        if(instance.getDefId()!=null) return; //only when the instance is unstructured (social talk, not a BPM process)

        //it is enough to just derive the role mappings and worklist data from the instance data and user token data.

        instance.setRoleMappings(new ArrayList<RoleMappingEntity>());{
            RoleMappingEntity roleMapping = new RoleMappingEntity();
            roleMapping.setRoleName("initiator");
            roleMapping.setEndpoint("jyjang"); //TODO: replaced by user id from Spring security
            roleMapping.setProcessInstance(instance);
            instance.getRoleMappings().add(roleMapping);
        }

        instance.setWorkLists(new ArrayList<WorklistEntity>());{
            WorklistEntity workList = new WorklistEntity();
            workList.setProcessInstance(instance);
            workList.setRoleName("initiator");
            workList.setEndpoint("jyjang"); //TODO: replaced by user id from Spring security
            instance.getWorkLists().add(workList);
        }


        // maybe someday useful:
//        if (instance.getDummyRoleMappings() != null && instance.getDummyRoleMappings().size() > 0) {
//            instance.setRoleMappings(new ArrayList<RoleMappingEntity>());
//            for (DummyRoleMapping dummyRoleMapping : instance.getDummyRoleMappings()) {
//                Map map = new ObjectMapper().convertValue(dummyRoleMapping, Map.class);
//                RoleMappingEntity roleMapping = new ObjectMapper().convertValue(map, RoleMappingEntity.class);
//                roleMapping.setProcessInstance(instance);
//                instance.getRoleMappings().add(roleMapping);
//            }
//        }
//
//        if (instance.getDummyWorkLists() != null && instance.getDummyWorkLists().size() > 0) {
//            instance.setWorkLists(new ArrayList<WorklistEntity>());
//            for (DummyWorkList dummyWorkList : instance.getDummyWorkLists()) {
//                Map map = new ObjectMapper().convertValue(dummyWorkList, Map.class);
//                WorklistEntity workList = new ObjectMapper().convertValue(map, WorklistEntity.class);
//                workList.setProcessInstance(instance);
//                instance.getWorkLists().add(workList);
//            }
//        }



    }

    public void setInitEp(String initEp) {
        this.initEp = initEp;
    }

    public String getInitEp() {
        return initEp;
    }

    public void setPrevCurrEp(String prevCurrEp) {
        this.prevCurrEp = prevCurrEp;
    }

    public String getPrevCurrEp() {
        return prevCurrEp;
    }

    public void setCurrEp(String currEp) {
        this.currEp = currEp;
    }

    public String getCurrEp() {
        return currEp;
    }

    public String getInitRsNm() {
        return initRsNm;
    }

    public void setInitRsNm(String initRsNm) {
        this.initRsNm = initRsNm;
    }

    public String getPrevCurrRsNm() {
        return prevCurrRsNm;
    }

    public void setPrevCurrRsNm(String prevCurrRsNm) {
        this.prevCurrRsNm = prevCurrRsNm;
    }

    public String getCurrRsNm() {
        return currRsNm;
    }

    public void setCurrRsNm(String currRsNm) {
        this.currRsNm = currRsNm;
    }


    public String getCorrKey() {
        return corrKey;
    }

    public void setCorrKey(String corrKey) {
        this.corrKey = corrKey;
    }

}
