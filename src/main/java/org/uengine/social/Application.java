package org.uengine.social;

import org.metaworks.springboot.configuration.Metaworks4BaseApplication;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;

@SpringBootApplication
public class Application extends Metaworks4BaseApplication {

    /**
     * @param dataSource
     * @param properties
     * @param jtaTransactionManagerProvider
     * @param transactionManagerCustomizers
     */
    protected Application(DataSource dataSource, JpaProperties properties,
                          ObjectProvider<JtaTransactionManager> jtaTransactionManagerProvider,
                          ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        super(dataSource, properties, jtaTransactionManagerProvider, transactionManagerCustomizers);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration#createJpaVendorAdapter()
     */
    @Override
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new EclipseLinkJpaVendorAdapter();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration#getVendorProperties()
     */
    @Override
    protected Map<String, Object> getVendorProperties() {

        // Turn off dynamic weaving to disable LTW lookup in static weaving mode
        return Collections.singletonMap("eclipselink.weaving", (Object) "false");
    }
}