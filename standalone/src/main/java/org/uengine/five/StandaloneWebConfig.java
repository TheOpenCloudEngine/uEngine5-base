package org.uengine.five;

import org.metaworks.multitenancy.ClassManager;
import org.metaworks.multitenancy.DefaultMetadataService;
import org.metaworks.multitenancy.MetadataService;
import org.metaworks.multitenancy.persistence.MultitenantRepositoryImpl;
import org.metaworks.multitenancy.tenantawarefilter.TenantAwareFilter;
import org.metaworks.rest.MetaworksRestService;
import org.metaworks.springboot.configuration.CorsFilter;
import org.metaworks.springboot.configuration.Metaworks4WebConfig;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.uengine.five.entity.ProcessInstanceEntity;
import org.uengine.five.overriding.*;
import org.uengine.five.repository.ProcessInstanceRepository;
import org.uengine.five.service.DefinitionServiceImpl;
import org.uengine.kernel.ActivityFilter;
import org.uengine.kernel.ProcessDefinition;
import org.uengine.kernel.ProcessInstance;
import org.uengine.modeling.resource.CachedResourceManager;
import org.uengine.modeling.resource.LocalFileStorage;
import org.uengine.modeling.resource.ResourceManager;
import org.uengine.modeling.resource.Storage;
import org.uengine.webservices.worklist.WorkList;

import javax.servlet.Filter;
import java.util.Map;

@EnableWebMvc
@Configuration
@ComponentScan(basePackageClasses = {DefinitionServiceImpl.class, ProcessDefinitionFactory.class, ProcessInstanceEntity.class, MetaworksRestService.class, ClassManager.class, MetadataService.class, MultitenantRepositoryImpl.class})
@EnableJpaRepositories(basePackageClasses = {MultitenantRepositoryImpl.class, ProcessInstanceRepository.class})
@Profile("standalone")
public class StandaloneWebConfig extends Metaworks4WebConfig{

    public StandaloneWebConfig(ApplicationContext context, ObjectFactory<ConversionService> conversionService) {
        super(context, conversionService);
    }

    /**
     * Uncomment if this needs a CORS setting by itself
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter();
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
        storage.setBasePath("/oce/repository");

        return storage;
    }


    @Bean
    public MetadataService metadataService() {
        DefaultMetadataService metadataService = new DefaultMetadataService();
        metadataService.setResourceManager(resourceManager());

        return metadataService;
    }


    @Bean
    @Scope("prototype")
    public ProcessInstance processInstance(ProcessDefinition procDefinition, String instanceId, Map options) throws Exception {
        return new CLOBProcessInstance(procDefinition, instanceId, options);
    }

    @Bean
    public ActivityFilter instanceDataAppendingFilter(){
        return new InstanceDataAppendingActivityFilter();
    }

    @Bean
    public WorkList workList() {
        return new JPAWorkList();
    }

    @Bean
    public Filter webFilter() {
        return new TenantAwareFilter();
    }

    @Bean
    public ProcessDefinitionFactory processDefinitionFactory(){
        return new ProcessDefinitionFactory();
    }

    @Override
    public JpaProperties jpaProperties() {
        JpaProperties jpaProperties = super.jpaProperties();

        jpaProperties.getProperties().put("hibernate.hbm2ddl.auto", "create");

        return jpaProperties;
    }
}


