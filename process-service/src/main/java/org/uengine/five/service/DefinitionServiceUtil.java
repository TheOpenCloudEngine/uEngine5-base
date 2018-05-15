package org.uengine.five.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uengine.five.service.DefinitionService;
import org.uengine.modeling.resource.Serializer;

/**
 * Created by uengine on 2017. 11. 15..
 */
@Component
public class DefinitionServiceUtil {

    @Autowired
    DefinitionXMLService definitionService;

    static ObjectMapper objectMapper = createTypedJsonObjectMapper();

    public Object getDefinition(String defPath) throws Exception {
        return getDefinition(defPath, false);
    }

    public Object getDefinition(String defPath, boolean production) throws Exception {

        if (defPath.indexOf(".") == -1) {
            defPath = defPath + ".xml";
        }        
        Object returned = definitionService.getXMLDefinition(defPath, production);
        String xml = (String) returned;

        return Serializer.deserialize(xml);
    }

    public static ObjectMapper createTypedJsonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // ignore null
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT); // ignore zero and false when it is int or boolean

        objectMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, "_type");
        return objectMapper;
    }

}
