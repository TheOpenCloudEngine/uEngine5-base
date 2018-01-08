package org.uengine.five;

import org.metaworks.springboot.configuration.Metaworks4BaseApplication;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.uengine.five.eventsourcing.EventSender;
import org.uengine.five.overriding.ServiceRegisterDeployFilter;
import org.uengine.five.service.DefinitionService;
import org.uengine.five.service.DefinitionServiceUtil;
import org.uengine.kernel.ProcessDefinition;

import javax.sql.DataSource;

@SpringCloudApplication
@EnableEurekaClient
@EnableFeignClients(basePackageClasses = {DefinitionService.class})
@Profile("msa")
@EnableKafka
public class ProcessServiceApplication extends Metaworks4BaseApplication {

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
    public EventSender eventSender(){
        return new EventSender();
    }


    @Bean
    public ServiceRegisterDeployFilter serviceRegisterDeployFilter(){
        return new ServiceRegisterDeployFilter();
    }
}