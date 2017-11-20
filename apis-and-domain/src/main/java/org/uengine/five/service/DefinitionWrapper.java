package org.uengine.five.service;

import java.io.Serializable;

/**
 * Created by uengine on 2017. 8. 20..
 *
 * Stands for typed json formatting of definitions.
 * Normal json doesn't contains any class type information of each properties, it ignores the polymorphism of classes.
 * To keep these meta-data, we introduced "typed-json", the root of definition also need the _type property,
 * so we came here to create a wrapper for just container for _type information of the root object.
 *
 * @see DefinitionServiceImpl.createTypedJsonObjectMapper()
 */
public class DefinitionWrapper implements Serializable{

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
