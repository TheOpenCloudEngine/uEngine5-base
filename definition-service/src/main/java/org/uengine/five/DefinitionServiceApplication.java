package org.uengine.five;

import org.metaworks.springboot.configuration.Metaworks4BaseApplication;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.uengine.five.service.DefinitionServiceImpl;
import org.uengine.five.service.HomeService;
import org.uengine.modeling.resource.*;

import javax.sql.DataSource;

@EnableEurekaClient
@SpringBootApplication
@EnableWebMvc
@Configuration
@Profile("msa")
@ComponentScan(basePackageClasses = {DefinitionServiceImpl.class})
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


    @Bean
    public ResourceManager resourceManager() {
        ResourceManager resourceManager = new CachedResourceManager();
        resourceManager.setStorage(storage());
        return resourceManager;
    }


    @Bean
    /**
     *
     * <bean class="CouchbaseStorage">
     *    <property name="basePath" value="/"/>
     <property name="bucketName" value="default"/>
     <property name="serverIp" value="localhost"/>
     </bean>
     */
    public Storage storage() {
        LocalFileStorage storage = new LocalFileStorage();
        storage.setBasePath("/tmp");

        try {
            System.out.println("-------------------> " + storage.exists(new DefaultResource(".")) + " ---> file system is mounted.");
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        };

        return storage;
    }


    //-------------------------------
    @Autowired
    HomeService homeService;


    @RequestMapping("/home")
    public String home() throws Exception {
        return homeService.getHome();
    }



}