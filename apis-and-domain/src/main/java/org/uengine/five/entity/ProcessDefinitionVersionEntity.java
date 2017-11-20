package org.uengine.five.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by uengine on 2017. 11. 8..
 */
public class ProcessDefinitionVersionEntity{// implements ProcessDefinitionVersionRepositoryLocal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long defVerId;

    Long defId;
    String defName;
    Long ver;
    String filePath;
    Date modDate;
    Date prodStartDate;
    boolean deleted;

    public Long getDefVerId() {
        return defVerId;
    }

    public void setDefVerId(Long defVerId) {
        this.defVerId = defVerId;
    }

    public Long getDefId() {
        return defId;
    }

    public void setDefId(Long defId) {
        this.defId = defId;
    }

    public String getDefName() {
        return defName;
    }

    public void setDefName(String defName) {
        this.defName = defName;
    }

    public Long getVer() {
        return ver;
    }

    public void setVer(Long ver) {
        this.ver = ver;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }

    public Date getProdStartDate() {
        return prodStartDate;
    }

    public void setProdStartDate(Date prodStartDate) {
        this.prodStartDate = prodStartDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
