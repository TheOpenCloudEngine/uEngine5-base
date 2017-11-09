package org.uengine.social.entity;

import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * Created by uengine on 2017. 11. 8..
 */
@Entity
@Table(name = "BPM_PROCDEF")
@Component
public class ProcessDefinitionEntity{ // implements ProcessDefinitionRepositoryLocal{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long defId;

    String description;
    boolean folder;
    boolean adhoc;
    Long parentFolder;
    int prodVer;
    String objType;
    Long prodVerId;
    String name;
    boolean deleted;
    boolean visible;
    String alias;
    String superDefId;
    String comCode;


    public Long getDefId() {
        return defId;
    }

    public void setDefId(Long defId) {
        this.defId = defId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    public boolean isAdhoc() {
        return adhoc;
    }

    public void setAdhoc(boolean adhoc) {
        this.adhoc = adhoc;
    }

    public Long getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(Long parentFolder) {
        this.parentFolder = parentFolder;
    }

    public int getProdVer() {
        return prodVer;
    }

    public void setProdVer(int prodVer) {
        this.prodVer = prodVer;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public Long getProdVerId() {
        return prodVerId;
    }

    public void setProdVerId(Long prodVerId) {
        this.prodVerId = prodVerId;
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getSuperDefId() {
        return superDefId;
    }

    public void setSuperDefId(String superDefId) {
        this.superDefId = superDefId;
    }

    public String getComCode() {
        return comCode;
    }

    public void setComCode(String comCode) {
        this.comCode = comCode;
    }
}
