package org.uengine.social;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.spi.EvaluationContextExtension;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.uengine.social.common.filter.TenantAwareFilter;
import org.uengine.social.common.repository.PersistentRepository;
import org.uengine.social.common.repository.PersistentRepositoryImpl;
import org.uengine.social.common.security.SecurityEvaluationContextExtension;
import org.uengine.social.service.instance.ProcessInstance;

import javax.sql.DataSource;
import java.util.Properties;

@EnableWebMvc
@Configuration
@ComponentScan(basePackageClasses = {WebConfig.class})
@EnableJpaRepositories(repositoryBaseClass = PersistentRepositoryImpl.class)
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .maxAge(3600)
                .allowedMethods("POST", "GET", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("access_token", "Content-Type", "x-requested-with", "origin", "accept",
                        "authorization", "Location");
    }

    @Bean
    public TenantAwareFilter tenantAwareFilter() {
        return new TenantAwareFilter();
    }


    @Bean
    public DataSource dataSource() {
        final Properties pool = new Properties();
        pool.put("driverClassName", "com.mysql.jdbc.Driver");
        pool.put("url", "jdbc:mysql://localhost:3306/uengine?useUnicode=true&characterEncoding=UTF8&useOldAliasMetadataBehavior=true");
        pool.put("username", "root");
        pool.put("password", "");
        pool.put("minIdle", 1);
        try {
            return new org.apache.tomcat.jdbc.pool.DataSourceFactory().createDataSource(pool);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Primary
    public JpaProperties jpaProperties() {
        JpaProperties propertiesMap = new JpaProperties();
        propertiesMap.getProperties().put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_OR_EXTEND);
        propertiesMap.getProperties().put("eclipselink.logging.level", "FINE");
        return propertiesMap;
    }

    @Bean
    EvaluationContextExtension securityExtension() {
        return new SecurityEvaluationContextExtension();
    }

}


