package org.uengine.social.service;

import java.io.Serializable;

/**
 * Created by uengine on 2017. 8. 20..
 */
public class DefinitionWrapper {

    Serializable definition;

    public DefinitionWrapper(Serializable object) {
        setDefinition(object);
    }

    public Serializable getDefinition() {
            return definition;
        }
        public void setDefinition(Serializable definition) {
            this.definition = definition;
        }

    public DefinitionWrapper(){}

}
