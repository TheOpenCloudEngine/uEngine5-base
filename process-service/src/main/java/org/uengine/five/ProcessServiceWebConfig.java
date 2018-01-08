package org.uengine.five;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.metaworks.multitenancy.ClassManager;
import org.metaworks.multitenancy.DefaultMetadataService;
import org.metaworks.multitenancy.MetadataService;
import org.metaworks.multitenancy.tenantawarefilter.TenantAwareFilter;
import org.metaworks.springboot.configuration.Metaworks4WebConfig;
import org.metaworks.multitenancy.persistence.MultitenantRepositoryImpl;
import org.metaworks.rest.MetaworksRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.uengine.five.overriding.InstanceDataAppendingActivityFilter;
import org.uengine.five.overriding.JPAProcessInstance;
import org.uengine.five.overriding.JPAWorkList;
import org.uengine.five.overriding.ProcessDefinitionFactory;
import org.uengine.five.repository.ProcessInstanceRepository;
import org.uengine.kernel.ActivityFilter;
import org.uengine.kernel.ProcessDefinition;
import org.uengine.kernel.ProcessInstance;
import org.uengine.modeling.resource.CachedResourceManager;
import org.uengine.modeling.resource.LocalFileStorage;
import org.uengine.modeling.resource.ResourceManager;
import org.uengine.modeling.resource.Storage;
import org.uengine.five.entity.ProcessInstanceEntity;
import org.uengine.webservices.worklist.WorkList;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

@EnableWebMvc
@Configuration
@Profile("msa")
@ComponentScan(basePackageClasses = {ProcessDefinitionFactory.class, ProcessInstanceEntity.class, MetaworksRestService.class, ClassManager.class, MetadataService.class, MultitenantRepositoryImpl.class})
@EnableJpaRepositories(basePackageClasses = {MultitenantRepositoryImpl.class, ProcessInstanceRepository.class})
public class ProcessServiceWebConfig extends Metaworks4WebConfig {

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.setOrder(10);
//        registry.addResourceHandler("/**").addResourceLocations("classpath:/dev/dist/");
//        super.addResourceHandlers(registry);
//    }
//
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("forward:/index.html");
//        super.addViewControllers(registry);
//    }

    @Bean
    public ResourceManager resourceManager() {
        ResourceManager resourceManager = new CachedResourceManager();
        resourceManager.setStorage(storage());
        return resourceManager;
    }

//    @Bean
//    public DataSource dataSource() {
//        final Properties pool = new Properties();
//        pool.put("driverClassName", "com.mysql.jdbc.Driver");
//        pool.put("url", "jdbc:mysql://localhost:3306/uengine?useUnicode=true&characterEncoding=UTF8&useOldAliasMetadataBehavior=true");
//        pool.put("username", "root");
//        pool.put("password", "");
//        pool.put("minIdle", 1);
//        try {
//            return new org.apache.tomcat.jdbc.pool.DataSourceFactory().createDataSource(pool);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

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

//    @Bean
//    public TenantAwareFilter tenantAwareFilter(){
//        return new TenantAwareFilter();
//    }

    @Bean
    public MetadataService metadataService() {
        DefaultMetadataService metadataService = new DefaultMetadataService();
        metadataService.setResourceManager(resourceManager());

        return metadataService;
    }


    @Bean
    @Scope("prototype")
    public ProcessInstance processInstance(ProcessDefinition procDefinition, String instanceId, Map options) throws Exception {
        return new JPAProcessInstance(procDefinition, instanceId, options);
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



    //// Kafka configurations

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<String, Object>();
        // list of host:port pairs used for establishing the initial connections to the Kakfa cluster
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // allows a pool of processes to divide the work of consuming and processing records
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "bpm");

        return props;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<String, String>(consumerConfigs());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }

}


