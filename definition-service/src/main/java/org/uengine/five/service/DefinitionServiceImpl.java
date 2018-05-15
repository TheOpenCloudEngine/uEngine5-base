package org.uengine.five.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.metaworks.dwr.MetaworksRemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import org.uengine.kernel.DeployFilter;
import org.uengine.kernel.NeedArrangementToSerialize;
import org.uengine.kernel.ProcessDefinition;
import org.uengine.kernel.UEngineException;
import org.uengine.modeling.resource.*;
import org.uengine.processpublisher.BPMNUtil;
import org.uengine.uml.model.ClassDefinition;
import org.uengine.util.UEngineUtil;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by uengine on 2017. 8. 9..
 *
 * Implementation Principles: - REST Maturity Level : 3 (Hateoas)
 * - Not using old uEngine ProcessManagerBean, this replaces the ProcessManagerBean
 * - ResourceManager and CachedResourceManager will be used for definition caching (Not to use the old DefinitionFactory)
 * - json must be Typed JSON to enable object polymorphism
 * - need to change the jackson engine.
 * TODO: accept? typed json is sometimes hard to read
 */
@RestController
public class DefinitionServiceImpl implements DefinitionService, DefinitionXMLService {

    static protected final String RESOURCE_ROOT = "codi";

    @Autowired
    ResourceManager resourceManager;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    
    @Autowired
    ApplicationContext applicationContext;    

    static ObjectMapper objectMapper = createTypedJsonObjectMapper();

    @PostConstruct
    public void init() {
    }

    @RequestMapping(value = DEFINITION, method = RequestMethod.GET)
    @Override
    public Resources<DefinitionResource> listDefinition(String basePath) throws Exception {
        return _listDefinition(RESOURCE_ROOT, basePath);
    }

    private Resources<DefinitionResource> _listDefinition(String resourceRoot, String basePath) throws Exception {

        if (basePath == null) {
            basePath = "";
        }

        IContainer resource = new ContainerResource();
        resource.setPath(resourceRoot + "/" + basePath);
        List<IResource> resources = resourceManager.listFiles(resource);

        List<DefinitionResource> definitions = new ArrayList<DefinitionResource>();
        for (IResource resource1 : resources) {
            DefinitionResource definition = new DefinitionResource(resource1);
            definitions.add(definition);
        }

        Resources<DefinitionResource> halResources = new Resources<DefinitionResource>(definitions);
        return halResources;
    }

    @RequestMapping(value = "/version/{version}" + DEFINITION + "/", method = RequestMethod.GET)
    public Resources<DefinitionResource> listVersionDefinitions(@PathVariable("version") String version, String basePath) throws Exception {
        VersionManager versionManager = MetaworksRemoteService.getComponent(VersionManager.class);

        return _listDefinition(versionManager.versionDirectoryOf(new Version(version)), basePath);
    }

    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public Resources<VersionResource> listVersions() throws Exception {
        VersionManager versionManager = MetaworksRemoteService.getComponent(VersionManager.class);

        List<VersionResource> versionResources = new ArrayList<VersionResource>();
        for (Version version : versionManager.listVersions()) {
            VersionResource versionResource = new VersionResource(version);
            versionResources.add(versionResource);
        }

        Resources<VersionResource> halResources = new Resources<VersionResource>(versionResources);
        return halResources;
    }


    @RequestMapping(value = "/version", method = RequestMethod.POST)
    public Resources<VersionResource> versionUp(Version version, @QueryParam("major") boolean major, @QueryParam("makeProduction") boolean makeProduction) throws Exception {

        VersionManager versionManager = MetaworksRemoteService.getComponent(VersionManager.class);
        versionManager.load("codi", null);

        if(major)
            versionManager.majorVersionUp();
        else
            versionManager.minorVersionUp();

        return listVersions();

    }

    @RequestMapping(value = "/version/{version:.+}/production", method = RequestMethod.POST)
    public VersionResource makeProduction(@PathVariable("version") String version) throws Exception {

        VersionManager versionManager = MetaworksRemoteService.getComponent(VersionManager.class);
        versionManager.load("codi", null);

        Version versionObj = new Version(version);
        versionManager.makeProductionVersion(versionObj);

        // VersionResource versionResource = new VersionResource(versionObj);

        return getVersion(version);
    }

    @RequestMapping(value = "/version/production", method = RequestMethod.GET)
    public VersionResource getProduction() throws Exception {

        VersionManager versionManager = MetaworksRemoteService.getComponent(VersionManager.class);
        versionManager.load("codi", null);

        Version versionObj = versionManager.getProductionVersion();

        return new VersionResource(versionObj);
    }

    @RequestMapping(value = "/version/{version:.+}", method = RequestMethod.GET)
    public VersionResource getVersion(@PathVariable("version") String version) throws Exception {

        VersionManager versionManager = MetaworksRemoteService.getComponent(VersionManager.class);
        List<Version> versions = versionManager.listVersions();

        for(Version theVersion : versions){
            if(theVersion.equals(new Version(version))){
                VersionResource versionResource = new VersionResource(theVersion);

                return versionResource;
            }
        }

        throw new ResourceNotFoundException(); // make 404 error
    }

    @RequestMapping(value = DEFINITION + "/{defPath:.+}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Override
    public ResourceSupport getDefinition(@PathVariable("defPath") String definitionPath) throws Exception {

        //case of directory:
        IResource resource = new DefaultResource(RESOURCE_ROOT + "/" + definitionPath);
        if (resourceManager.exists(resource) && resourceManager.isContainer(resource)) { // is a folder
            return listDefinition(definitionPath);
        }

        //case of file:
        definitionPath = UEngineUtil.getNamedExtFile(definitionPath, "xml");

        resource = new DefaultResource(RESOURCE_ROOT + "/" + definitionPath);

        if (!resourceManager.exists(resource)) {
            throw new ResourceNotFoundException(); // make 404 error
        }

        DefinitionResource halDefinition = new DefinitionResource(resource);

        return halDefinition;

    }

    @RequestMapping(value = DEFINITION + "/**", method = RequestMethod.GET)
    public Object getDefinition(HttpServletRequest request) throws Exception {
        
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String definitionPath = path.substring(DEFINITION.length() + 1);

        return getDefinition(definitionPath);
        
    }

    /**
     * TODO: need ACL referenced by token
     * 
     * @throws Exception
     */
    @RequestMapping(value = DEFINITION + "/**", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public DefinitionResource renameOrMove(@RequestBody DefinitionResource definition_, HttpServletRequest request) throws Exception {

        DefinitionResource definition = definition_;

        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        String definitionPath = path.substring(DEFINITION.length());
        if (definitionPath.indexOf(".") != -1) {
            definitionPath = UEngineUtil.getNamedExtFile(definitionPath, "xml");
        }
        IResource resource = new DefaultResource(RESOURCE_ROOT + "/" + definitionPath);

        if (!definition.getPath().equals(definitionPath)) {
            String newPath = RESOURCE_ROOT + "/" + definition.getPath();
            resourceManager.rename(resource, newPath);
            return new DefinitionResource(new ContainerResource(newPath));
        }

        return new DefinitionResource(resource);
    }

    @RequestMapping(value = DEFINITION + "/**", method = { RequestMethod.POST })
    public DefinitionResource createFolder(@RequestBody DefinitionResource newResource_, HttpServletRequest request) throws Exception {

        DefinitionResource newResource = newResource_;

        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        String definitionPath = path.substring(DEFINITION.length());

        if (newResource == null) {
            IResource resource = new DefaultResource(RESOURCE_ROOT + definitionPath);
            if (definitionPath.indexOf(".") == -1) { // it is a package (directory)
                IContainer container = new ContainerResource();
                container.setPath(RESOURCE_ROOT + "/" + definitionPath);
                resourceManager.createFolder(container);
                return new DefinitionResource(container);
            } else {
                throw new Exception("Only folder can be created with this method. Use POST : " + DEFINITION_RAW + " instead.");
            }
        } else {
            String example = "e.g.{\"name\": \"folder\", \"directory\":true}";

            Assert.notNull(newResource.getName(), "folder name must be present. " + example);
            Assert.isTrue(newResource.isDirectory(), "On directory can be created with this method. " + example);

            IContainer container = new ContainerResource();
            container.setPath(RESOURCE_ROOT + definitionPath + "/" + newResource.getName());
            resourceManager.createFolder(container);

            return new DefinitionResource(container);
        }

    }

    @RequestMapping(value = DEFINITION + "/**", method = { RequestMethod.DELETE })
    public void deleteDefinition(HttpServletRequest request) throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String definitionPath = path.substring(DEFINITION.length() + 1);
        IResource resource = new DefaultResource(RESOURCE_ROOT + "/" + definitionPath);
        resourceManager.delete(resource);

    }

    // ----------------- raw definition services -------------------- //

    public static ObjectMapper createTypedJsonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // ignore null
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT); // ignore zero and false when it is int or boolean
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        objectMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, "_type");
        return objectMapper;

    }

    /**
     * TODO: need ACL referenced by token
     * 
     * @param definition
     * @throws Exception
     */
    @RequestMapping(value = DEFINITION_RAW + "/**", method = { RequestMethod.POST, RequestMethod.PUT })
    public DefinitionResource putRawDefinition(@RequestBody String definition, HttpServletRequest request) throws Exception {
        
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        String definitionPath = path.substring(DEFINITION_RAW.length());
        if (definitionPath.indexOf(".") == -1) { // it is a package (directory)
            IContainer container = new ContainerResource();
            container.setPath(RESOURCE_ROOT + "/" + definitionPath);
            resourceManager.createFolder(container);
            return new DefinitionResource(container);
        }

        String fileExt = UEngineUtil.getFileExt(definitionPath);

        definitionPath = UEngineUtil.getNamedExtFile(RESOURCE_ROOT + "/" + definitionPath, "xml");

        // 무조건 xml 파일로 결국 저장됨.
        DefaultResource resource = new DefaultResource(definitionPath);

        Object definitionDeployed;
        if (fileExt.endsWith("bpmn")) {
            // TODO [severe] BPMNUtil.importAdapt(InputStream) must be available. using temp file will arise a multi-thread problem.
            ByteArrayInputStream bai = new ByteArrayInputStream(definition.getBytes("UTF-8"));

            UEngineUtil.copyStream(bai, new FileOutputStream("test.bpmn"));
            ProcessDefinition processDefinition = BPMNUtil.importAdapt(new File("test.bpmn"));
            resourceManager.save(resource, processDefinition);
            definitionDeployed = processDefinition;
        } else if (fileExt.endsWith("upd")) {
            ByteArrayInputStream bai = new ByteArrayInputStream(definition.getBytes("UTF-8"));
            ProcessDefinition processDefinition = (ProcessDefinition) org.uengine.modeling.resource.Serializer.deserialize(bai);
            resourceManager.save(resource, processDefinition);
            definitionDeployed = processDefinition;
        } else if (fileExt.endsWith("class")) {
            ClassDefinition classDefinition = objectMapper.readValue(definition, ClassDefinition.class);
            resourceManager.save(resource, classDefinition);
            definitionDeployed = classDefinition;
        } else if (fileExt.endsWith("json")) {
            DefinitionWrapper definitionWrapper = objectMapper.readValue(definition, DefinitionWrapper.class);
            if (definitionWrapper.getDefinition() == null) {
                throw new Exception("DefinitionResource is corrupt.");
            }

            //add additional info if definition is not a process definition!
            if(!(definitionWrapper.getDefinition() instanceof ProcessDefinition) && !resource.getPath().endsWith("."+definitionWrapper.getDefinition().getClass().getSimpleName() + ".xml")){
                resource = new DefaultResource(UEngineUtil.getNamedExtFile(resource.getPath(), definitionWrapper.getDefinition().getClass().getSimpleName() +".xml"));
            }

            resourceManager.save(resource, definitionWrapper.getDefinition());
            definitionDeployed = definitionWrapper.getDefinition();
        } else {
            throw new Exception("unknown resource type: " + definitionPath);
        }
        
        // TODO: deploy filter 로 등록된 bean 들을 호출:
        if (definitionDeployed instanceof ProcessDefinition) {
            invokeDeployFilters((ProcessDefinition) definitionDeployed, resource.getPath().substring(RESOURCE_ROOT.length()+2));
        }

        return new DefinitionResource(resource);
    }

    private void invokeDeployFilters(ProcessDefinition definitionDeployed, String path) throws UEngineException {
        
        Map<String, DeployFilter> filters = MetaworksRemoteService.getInstance().getBeanFactory().getBeansOfType(DeployFilter.class);
        if (filters != null && filters.size() > 0) {
            for (DeployFilter theFilter : filters.values()) {
                try {
                    theFilter.beforeDeploy(definitionDeployed, null, path, true);
                } catch (Exception e) {
                    throw new UEngineException("Error when to invoke DeployFilter: " + theFilter.getClass().getName(), e);
                }
            }
        }
        
    }

    @RequestMapping(value = DEFINITION_RAW + "/{defPath:.+}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Object getRawDefinition(@PathVariable("defPath") String definitionPath/*, @RequestParam(value = "unwrap", required = false) boolean unwrap*/) throws Exception {

        definitionPath = UEngineUtil.getNamedExtFile(RESOURCE_ROOT + "/" + definitionPath, "xml");
        // 무조건 xml 파일로 결국 저장됨.
        DefaultResource resource = new DefaultResource(definitionPath);
        Serializable definition = (Serializable) getDefinitionLocal(resource.getPath());

//        if(unwrap) {
//            return objectMapper.writeValueAsString(definition);
//        }else{
            DefinitionWrapper definitionWrapper = new DefinitionWrapper(definition);
            String uEngineProcessJSON = objectMapper.writeValueAsString(definitionWrapper);
            return uEngineProcessJSON;
//        }
        
    }

    @RequestMapping(value = DEFINITION_RAW + "/**", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Object getRawDefinition(HttpServletRequest request/*, @RequestParam("unwrap") boolean unwrap*/) throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String definitionPath = path.substring(DEFINITION_RAW.length() + 1);


        return getRawDefinition(definitionPath);
        
    }

    @RequestMapping(value = DEFINITION + "/xml/{defPath:.+}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getXMLDefinition(@PathVariable("defPath") String definitionPath, @RequestParam("production") boolean production) throws Exception {

        definitionPath = definitionPath.startsWith(RESOURCE_ROOT) ? definitionPath.replace(RESOURCE_ROOT, "") : definitionPath;
        definitionPath = UEngineUtil.getNamedExtFile(definitionPath, "xml");

        //replace to production version if requested:
        if(production){
            VersionManager versionManager = MetaworksRemoteService.getComponent(VersionManager.class);
            versionManager.load("codi", null);

            definitionPath = versionManager.getProductionResourcePath(definitionPath);
        }

        Serializable definition = (Serializable) getDefinitionLocal(definitionPath);
        String uEngineProcessXML = Serializer.serialize(definition);
        return uEngineProcessXML;
        
    }


    @RequestMapping(value = DEFINITION + "/xml/**", method = RequestMethod.GET, produces = "application/xml;charset=UTF-8")
    public String getXMLDefinition(HttpServletRequest request) throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String definitionPath = path.substring((DEFINITION + "/xml").length() + 1);

        boolean production = "true".equals(request.getParameter("production"));

        return getXMLDefinition(definitionPath, production);
        
    }

    public Object getDefinitionLocal(String definitionPath) throws Exception {

        try {
            if (definitionPath.indexOf(".") == -1) {
                definitionPath = definitionPath + ".xml";
            }

            IResource resource = new DefaultResource((definitionPath.startsWith(RESOURCE_ROOT) ? definitionPath : RESOURCE_ROOT + "/" + definitionPath));
            Object definition = resourceManager.getObject(resource);

            // TODO: move to framework
            if (definition instanceof NeedArrangementToSerialize) {
                ((NeedArrangementToSerialize) definition).afterDeserialization();
            }

            if (definition instanceof ProcessDefinition) {
                ProcessDefinition processDefinition = (ProcessDefinition) definition;
                { // TODO: will be moved to afterDeserialize of ProcessDefinition
                    processDefinition.setId(resource.getPath().substring(RESOURCE_ROOT.length() + 1));
                    if (processDefinition.getName() == null) {
                        processDefinition.setName(resource.getPath());
                    }
                }
            }

            return definition;

        } catch (Exception e) {
            throw new UEngineException("Error when to load definition: " + definitionPath, e);
        }

    }


}
