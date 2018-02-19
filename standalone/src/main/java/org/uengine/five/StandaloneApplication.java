package org.uengine.five;

import javax.sql.DataSource;

import org.metaworks.multitenancy.persistence.MultitenantRepositoryImpl;
import org.metaworks.springboot.configuration.Metaworks4BaseApplication;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@Profile("standalone")
//WAS의 JMX MBeanServer와 충돌문제 발생 시 아래 주석 해지. Spring-Boot의 JmxAutoConfiguration을 사용하지 않는 기능임  
//@EnableAutoConfiguration(exclude = {JmxAutoConfiguration.class, EndpointMBeanExportAutoConfiguration.class})
@EnableJpaRepositories(repositoryBaseClass = MultitenantRepositoryImpl.class)

public class StandaloneApplication extends Metaworks4BaseApplication {

    /**
     * @param dataSource
     * @param properties
     * @param jtaTransactionManagerProvider
     * @param transactionManagerCustomizers
     */
    protected StandaloneApplication(DataSource dataSource, JpaProperties properties,
                                    ObjectProvider<JtaTransactionManager> jtaTransactionManagerProvider,
                                    ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        super(dataSource, properties, jtaTransactionManagerProvider, transactionManagerCustomizers);
    }

    public static void main(String[] args) {
        SpringApplication.run(StandaloneApplication.class, args);
    }

    @Override
    @Bean
    @Primary
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

}