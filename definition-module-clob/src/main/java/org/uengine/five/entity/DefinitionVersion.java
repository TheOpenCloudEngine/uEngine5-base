package org.uengine.five.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.metaworks.annotation.RestAssociation;
import org.metaworks.multitenancy.persistence.AfterLoadOne;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import javax.persistence.*;
import java.util.Date;

@Entity
//@Multitenant
//@TenantDiscriminatorColumn(
//        name = "TENANTID",
//        contextProperty = "tenant-id"
//)
@RepositoryEventHandler(DefinitionVersion.class)
public class DefinitionVersion implements AfterLoadOne{//} implements ProcessDefinitionVersionRepositoryLocal{

    @Id @GeneratedValue
    Long defVerId;

    @ManyToOne @JoinColumn(name="defId")
    Definition definition;
        public Definition getDefinition() {
            return definition;
        }
        public void setDefinition(Definition definition) {
            this.definition = definition;
        }

    //Long defId;
    String defName;
    Long ver;
    String filePath;
    Date modDate;
    Date prodStartDate;
    boolean isDeleted;

    @Lob
    @JsonIgnore
    byte[] rawFile;
        public byte[] getRawFile() {
            return rawFile;
        }
        public void setRawFile(byte[] rawFile) {
            this.rawFile = rawFile;
        }

    @Transient
    @RestAssociation(path="/version/{defVerId}/production")
    String makeProduction;

    @Transient
    String versionId;
        public String getVersionId() {
            return versionId;
        }
        public void setVersionId(String versionId) {
            this.versionId = versionId;
        }



    public Long getDefVerId() {
        return defVerId;
    }

    public void setDefVerId(Long defVerId) {
        this.defVerId = defVerId;
    }

//    public Long getDefId() {
//        return defId;
//    }
//
//    public void setDefId(Long defId) {
//        this.defId = defId;
//    }

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
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public void afterLoadOne() {
        setVersionId(getDefVerId().toString());
    }


}
