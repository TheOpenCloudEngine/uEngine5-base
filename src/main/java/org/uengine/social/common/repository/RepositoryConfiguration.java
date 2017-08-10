package org.uengine.social.common.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.uengine.social.service.instance.ProcessEventHandler;

/**
 * Created by uengine on 2017. 8. 4..
 */
@Configuration
public class RepositoryConfiguration extends RepositoryRestConfigurerAdapter {

    @Bean
    ProcessEventHandler processEventHandler() {
        return new ProcessEventHandler();
    }

//    @Override
//    public void configureJacksonObjectMapper(ObjectMapper objectMapper) {
//
//        objectMapper.registerModule(new SimpleModule("CustomModule") {
//            @Override
//            public void setupModule(SetupContext context) {
//                SimpleSerializers serializers = new SimpleSerializers();
//                //SimpleDeserializers deserializers = new SimpleDeserializers();
//
//                serializers.addSerializer(ProcessInstance.class, new JsonSerializer<ProcessInstance>() {
//                    @Override
//                    public void serialize(ProcessInstance processInstance, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
//                        jsonGenerator.writeStartObject();
//                        jsonGenerator.writeFieldName("dummyWorkLists");
//                        jsonGenerator.writeObject(processInstance.getDummyWorkLists());
//
//                        jsonGenerator.writeEndObject();
//                    }
//                });
//                //deserializers.addDeserializer(MyEntity.class, new MyEntityDeserializer());
//
//                context.addSerializers(serializers);
//                //context.addDeserializers(deserializers);
//            }
//        });
//    }
}
