package org.uengine.five;

import org.metaworks.multitenancy.persistence.MultitenantRepositoryImpl;
import org.metaworks.springboot.configuration.Metaworks4BaseApplication;
import org.metaworks.springboot.configuration.Metaworks4WebConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
//import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.uengine.cloud.services.AppService;
import org.uengine.five.overriding.EventSendingDeployFilter;
import org.uengine.five.service.*;
import org.uengine.kernel.DeployFilter;
import org.uengine.modeling.resource.*;

import javax.sql.DataSource;

@EnableEurekaClient
@SpringBootApplication
@EnableWebMvc
@Configuration
@EnableCircuitBreaker
//@ComponentScan(basePackageClasses = {Metaworks4WebConfig.class, DefinitionServiceImpl.class, AppGenerationService.class})
@EnableFeignClients(basePackageClasses = {CloudAppService.class})
@EnableJpaRepositories(repositoryBaseClass = MultitenantRepositoryImpl.class)
@RestController
public class DefinitionServiceApplication extends Metaworks4BaseApplication {

    /**
     * @param dataSource
     * @param properties
     * @param jtaTransactionManagerProvider
     * @param transactionManagerCustomizers
     */
    protected DefinitionServiceApplication(DataSource dataSource, JpaProperties properties,
                                           ObjectProvider<JtaTransactionManager> jtaTransactionManagerProvider,
                                           ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        super(dataSource, properties, jtaTransactionManagerProvider, transactionManagerCustomizers);
    }

    public static void main(String[] args) {
        SpringApplication.run(DefinitionServiceApplication.class, args);
    }

    @RequestMapping("/health")
    public String home() throws Exception {
        return "";
    }
}