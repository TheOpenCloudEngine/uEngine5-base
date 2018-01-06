package org.uengine.five.entity;

import org.uengine.kernel.ProcessDefinition;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by uengine on 2018. 1. 5..
 */
@Entity
@Table(name="bpm_service")
public class ServiceEndpointEntity {

   @Id
    String path;
    String messageClass;
    String correlationKey;
    String defId;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessageClass() {
        return messageClass;
    }

    public void setMessageClass(String messageClass) {
        this.messageClass = messageClass;
    }

    public String getCorrelationKey() {
        return correlationKey;
    }

    public void setCorrelationKey(String correlationKey) {
        this.correlationKey = correlationKey;
    }

    public String getDefId() {
        return defId;
    }

    public void setDefId(String defId) {
        this.defId = defId;
    }


}
