package org.uengine.five;

import org.metaworks.multitenancy.tenantawarefilter.TenantAwareFilter;
import org.metaworks.springboot.configuration.Metaworks4WebConfig;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.uengine.five.feign.FeignRequestInterceptor;
import org.uengine.five.overriding.EventSendingDeployFilter;
import org.uengine.kernel.DeployFilter;
import org.uengine.modeling.resource.*;

@Configuration
public class WebConfig extends Metaworks4WebConfig {

    public WebConfig(ApplicationContext context, ObjectFactory<ConversionService> conversionService) {
        super(context, conversionService);
    }

    @Bean
    public ResourceManager resourceManager() {
        ResourceManager resourceManager = new ResourceManager();
        resourceManager.setStorage(storage());
        return resourceManager;
    }

    @Bean
    public Storage storage() {
        LocalFileStorage storage = new LocalFileStorage();
        storage.setBasePath("/oce/repository");

        try {
            System.out.println("-------------------> " + storage.exists(new DefaultResource(".")) + " ---> file system is mounted.");
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }
        return storage;
    }


    @Bean
    @Scope("prototype")
    public VersionManager versionManager() {
        SimpleVersionManager simpleVersionManager = new SimpleVersionManager();
        simpleVersionManager.setAppName("codi");
        //simpleVersionManager.setModuleName("definition");

        return simpleVersionManager;
    }


    @Bean
    public DeployFilter serviceRegisterDeployFilter() {
        return new EventSendingDeployFilter();
    }

    @Bean
    public TenantAwareFilter tenantAwareFilter() {
        TenantAwareFilter tenantAwareFilter = new TenantAwareFilter();
        tenantAwareFilter.setAllowAnonymousTenant(true);
        return tenantAwareFilter;
    }

    @Bean
    public FeignRequestInterceptor feignRequestInterceptor() {
        return new FeignRequestInterceptor();
    }

    /**
     * Uncomment if this needs a CORS setting by itself
     *
     * @return
     */
//    @Bean
//    public CorsFilter corsFilter() {
//        return new CorsFilter();
//    }
}
