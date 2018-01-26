package org.uengine.five.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;
import org.metaworks.annotation.RestAssociation;
import org.metaworks.dwr.MetaworksRemoteService;
import org.metaworks.multitenancy.persistence.AfterLoadOne;
import org.metaworks.multitenancy.persistence.BeforeSave;
import org.uengine.five.DefinitionServiceApplication;
import org.uengine.five.repository.DefinitionVersionRepository;
import org.uengine.five.service.DefinitionWrapper;
import org.uengine.kernel.ProcessDefinition;
import org.uengine.modeling.resource.Serializer;
import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Test scripts
 *

 http localhost:8080/definitions defId="def1" name="aaa" definitionJson="{\"_type\":\"java.lang.String\", \"definition\":\"Version1\"}"          #create new definition
 http PATCH localhost:8080/definitions/def1 name="aab" definitionJson="{\"_type\":\"java.lang.String\", \"definition\":\"Version2\"}"  #version up
 http PATCH localhost:8080/definitions/def1 name="aac" definitionJson="{\"_type\":\"java.lang.String\", \"definition\":\"Version3\"}"  #version up again

 http localhost:8080/definitions/def1/versions
 http localhost:8080/definitions/def1      #returns definitionJson = Version3

 http PATCH localhost:8080/definitions/def1 prodVerId=1        #mark 1 as production (1 is versionId, not version number)
 http localhost:8080/definitions/def1      #returns definitionJson = Version1

 *
 *
 */

@Entity
//@Multitenant
//@TenantDiscriminatorColumn(
//        name = "TENANTID",
//        contextProperty = "tenant-id"
//)
public class Definition implements BeforeSave, AfterLoadOne {//implements ProcessDefinitionRepositoryLocal {

    @Id //@GeneratedValue
    String defId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "definition")
//    @JoinColumn(name = "defId", referencedColumnName = "defId")
    List<DefinitionVersion> versions;
        public List<DefinitionVersion> getVersions() {
            return versions;
        }
        public void setVersions(List<DefinitionVersion> versions) {
            this.versions = versions;
        }


    String description;
    boolean isFolder;
    boolean isAdhoc;
    Long parentFolder;
    int prodVer;
    String objType;
    Long prodVerId;
    String name;
    boolean isDeleted;
    boolean isVisible;
    String alias;
    String superDefId;

    @Transient
            @JsonIgnore
    String definitionJson;
        public String getDefinitionJson() {
            return definitionJson;
        }
        public void setDefinitionJson(String definitionJson) {
            this.definitionJson = definitionJson;
        }

    @Transient
    @JsonIgnore
    String definitionXml;
        public String getDefinitionXml() {
            return definitionXml;
        }
        public void setDefinitionXml(String definitionXml) {
            this.definitionXml = definitionXml;
        }

    @Transient
    Long versionId;
        public Long getVersionId() {
            return versionId;
        }
        public void setVersionId(Long versionId) {
            this.versionId = versionId;
        }

    @Transient
    @RestAssociation(path="/instance?defPath={defId}")
    String instantiation;

    @Transient
    @RestAssociation(path="/definition/raw/{defId}@last")
    String raw;

    public String getDefId() {
        return defId;
    }

    public void setDefId(String defId) {
        this.defId = defId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public boolean isAdhoc() {
        return isAdhoc;
    }

    public void setAdhoc(boolean adhoc) {
        isAdhoc = adhoc;
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
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
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

    @Override
    public void beforeSave() {
        // intent is setting production version id
        if(getDefinitionJson()==null && getProdVerId() != null){
            // do nothing only update the prodVerId
        }else{ // intent is adding new version
            DefinitionVersion definitionVersion = addNewVersion();
            if(getVersions()==null)
                setVersions(new ArrayList<DefinitionVersion>());
            getVersions().add(definitionVersion);
        }
    }

    private DefinitionVersion addNewVersion() {
        DefinitionVersion lastVersion = new DefinitionVersion();

        Long version = new Long(0);

        if(getVersions()==null || getVersions().size() == 0){
            lastVersion.setVer(0L);
        }else{
            lastVersion.setVer(getVersions().get(getVersions().size()-1).getVer());
            version = new Long(getVersions().size());
        }

        lastVersion.setDefinition(this);
        lastVersion.setVer(version + 1);
        lastVersion.setDefName(getName());

        byte[] rawFile = new byte[0];
        try {

            DefinitionVersionRepository definitionVersionRepository = MetaworksRemoteService.getComponent(DefinitionVersionRepository.class);
            definitionVersionRepository.save(lastVersion);

            rawFile = createRawFile(lastVersion);

            if(rawFile!=null)
                lastVersion.setRawFile(rawFile);

            definitionVersionRepository.save(lastVersion); //need save twice since we need to create a version id

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return lastVersion;
    }

    private byte[] createRawFile(DefinitionVersion lastVersion) throws Exception {
        if(getDefinitionJson()!=null){

            DefinitionWrapper definitionWrapper = DefinitionServiceApplication.createTypedJsonObjectMapper().readValue(getDefinitionJson(), DefinitionWrapper.class);

            if (definitionWrapper.getDefinition() == null) {
                throw new Exception("DefinitionResource is corrupt.");
            }

            ///// setting the defId from ProcessDefinition object. /////
            if(definitionWrapper.getDefinition() instanceof ProcessDefinition){
                ProcessDefinition definition = (ProcessDefinition) definitionWrapper.getDefinition();
                definition.setId(getDefId() + "@" + lastVersion.getDefVerId());
                setName(((ProcessDefinition) definitionWrapper.getDefinition()).getName());
            }

            String xml = Serializer.serialize(definitionWrapper.getDefinition());

            return xml.getBytes();

        }

        return null;
    }

    @Override
    public void afterLoadOne() {
        if (getVersions() != null || getVersions().size() > 0) {

            DefinitionVersion definitionVersion;

            if(getVersionId()==null) {
                if (getProdVerId() != null && getProdVerId() > 0L) {
                    setVersionId(getProdVerId());
                }
            }

            if(getVersionId()!=null){
                DefinitionVersionRepository definitionVersionRepository = MetaworksRemoteService.getComponent(DefinitionVersionRepository.class);

                definitionVersion = definitionVersionRepository.findOne(getVersionId());
            }else {

                if(getVersions().size() == 0) // there's no version at all
                    return;

                definitionVersion = getVersions().get(getVersions().size() - 1);
            }

            byte[] rawFile = definitionVersion.getRawFile();
            if(rawFile!=null){
                ByteArrayInputStream bai = new ByteArrayInputStream(rawFile);
                try {
                    setDefinitionXml(new String(rawFile));
                    Object definition = Serializer.deserialize(bai);

                    DefinitionWrapper definitionWrapper = new DefinitionWrapper();
                    definitionWrapper.setDefinition((Serializable) definition);

                    String json = DefinitionServiceApplication.createTypedJsonObjectMapper().writeValueAsString(definitionWrapper);

                    setDefinitionJson(json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }



}


