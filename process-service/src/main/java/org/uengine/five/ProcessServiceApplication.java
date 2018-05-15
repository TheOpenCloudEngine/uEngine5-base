package org.uengine.five;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig;
import org.metaworks.WebFieldDescriptor;
import org.metaworks.springboot.configuration.Metaworks4BaseApplication;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.bind.annotation.RestController;
import org.uengine.five.overriding.InstanceNameFilter;
import org.uengine.five.overriding.ServiceRegisterDeployFilter;
import org.uengine.five.service.DefinitionService;
import org.uengine.five.service.DefinitionServiceUtil;
import org.uengine.five.service.SemanticEntityService;
import org.uengine.kernel.ProcessDefinition;

import javax.sql.DataSource;

@SpringCloudApplication
@EnableEurekaClient
//@EnableDiscoveryClient
@EnableFeignClients(basePackageClasses = {DefinitionService.class, SemanticEntityService.class})
@Profile("msa")
@EnableKafka
@RestController
public class ProcessServiceApplication extends Metaworks4BaseApplication {

    static public ObjectMapper objectMapper = createTypedJsonObjectMapper();

    /**
     * @param dataSource
     * @param properties
     * @param jtaTransactionManagerProvider
     * @param transactionManagerCustomizers
     */
    protected ProcessServiceApplication(DataSource dataSource, JpaProperties properties,
                                        ObjectProvider<JtaTransactionManager> jtaTransactionManagerProvider,
                                        ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        super(dataSource, properties, jtaTransactionManagerProvider, transactionManagerCustomizers);
    }

    public static void main(String[] args) {
        SpringApplication.run(ProcessServiceApplication.class, args);
    }

    @Autowired
    ServiceRegisterDeployFilter serviceRegisterDeployFilter;

    @Autowired
    DefinitionServiceUtil definitionServiceUtil;

    @KafkaListener(topics = "${kafka.topic.bpm}")
    public void receive(String payload) {
        if(payload.startsWith("deploy:")){
            String[] eventNameAndDefinitionPath = payload.split(":");

            String definitionPath = eventNameAndDefinitionPath[1].trim();

            try {
                Object definition = definitionServiceUtil.getDefinition(definitionPath);
                serviceRegisterDeployFilter.beforeDeploy((ProcessDefinition) definition,null, definitionPath, true);
            } catch (Exception e) {
                throw new RuntimeException("failed to dispatch business event :"+ payload,e);
            }

        }
    }

    @Bean
    public ServiceRegisterDeployFilter serviceRegisterDeployFilter(){
        return new ServiceRegisterDeployFilter();
    }

    @Bean
    public InstanceNameFilter instanceNameFilter(){
        return new InstanceNameFilter();
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
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        objectMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS, "_type");
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        return objectMapper;
    }


}