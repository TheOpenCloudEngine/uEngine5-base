package org.uengine.five;

import javax.sql.DataSource;

import org.metaworks.springboot.configuration.Metaworks4BaseApplication;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.transaction.jta.JtaTransactionManager;

@SpringBootApplication
//WAS의 JMX MBeanServer와 충돌문제 발생 시 아래 주석 해지. Spring-Boot의 JmxAutoConfiguration을 사용하지 않는 기능임  
//@EnableAutoConfiguration(exclude = {JmxAutoConfiguration.class, EndpointMBeanExportAutoConfiguration.class})
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

}