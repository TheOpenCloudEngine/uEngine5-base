package org.uengine.social.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.bind.annotation.*;
import org.uengine.five.ChangeEvent;
import org.uengine.five.ProcessTransactional;
import org.uengine.kernel.*;
import org.uengine.kernel.bpmn.CallActivity;
import org.uengine.modeling.resource.*;
import org.uengine.processpublisher.BPMNUtil;
import org.uengine.uml.model.ClassDefinition;
import org.uengine.util.UEngineUtil;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by uengine on 2017. 8. 9..
 *
 * Implementation Principles:
 *  - REST Maturity Level : 2
 *  - Not using old uEngine ProcessManagerBean, this replaces the ProcessManagerBean
 *  - ResourceManager and CachedResourceManager will be used for definition caching (Not to use the old DefinitionFactory)
 *  - json must be Typed JSON to enable object polymorphism - need to change the jackson engine. TODO: accept? typed json is sometimes hard to read
 */
@RestController
public class DefinitionService {

    final String resourceRoot = "codi";

    @Autowired
    ResourceManager resourceManager;


    @Autowired
    ApplicationEventPublisher applicationEventPublisher;



    @PostConstruct
    public void init(){
    }

    // ---------------- CRUD mappings -------------------- //

    @RequestMapping(value = "/definitions", method = RequestMethod.GET)
    public List<String> listDefinition() throws Exception {
        return viewList("");  //TODO: Need to be changed to HATEOAS _self link instead
    }


    @RequestMapping(value = "/definitions/packages/{packagePath}/processes", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public List<String> listPackage(@PathVariable("packagePath") String packagePath) throws Exception {
        return viewList(packagePath);  //TODO: Need to be changed to HATEOAS _self link instead
    }

    @RequestMapping(value = "/definitions/all", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public List<String> allListDefinition() throws Exception {
        List<String> allDefinitionList = new ArrayList<String>();
        List<String> rootDefinitionList = viewList("");
        List<String> packageList = new ArrayList<String>();

        for (int i = 0; i < rootDefinitionList.size(); i++) {
            if(rootDefinitionList.get(i).endsWith(".json")) {
                allDefinitionList.add(rootDefinitionList.get(i).replace("/", ""));
            } else {
                packageList.add(rootDefinitionList.get(i).replace("/", ""));
            }
        }

        for (int j = 0; j < packageList.size(); j++) {
            List<String> definitionList = viewList(packageList.get(j));
            for(int k = 0; k < definitionList.size(); k++) {
                allDefinitionList.add(definitionList.get(k).substring(1, definitionList.get(k).length()));
            }
        }

        return allDefinitionList;  //TODO: Need to be changed to HATEOAS _self link instead
    }


    private List<String> viewList(String definitionPath) throws Exception {
        IContainer resource = new ContainerResource();
        resource.setPath(resourceRoot + "/" + definitionPath);
        List<IResource> resources = resourceManager.listFiles(resource);

        List<String> resourcesList = new ArrayList<String>();

        for(IResource resource1 : resources){
            resourcesList.add(resource1.getPath().substring(resourceRoot.length()));
        }
        return resourcesList;
    }


    @RequestMapping(value = "/definition/packages/{packagePath}", method = RequestMethod.POST)
    public void putPackage(@PathVariable("packagePath") String packagePath) throws Exception {
        IContainer folderResource = new ContainerResource();
        folderResource.setPath(resourceRoot + "/" + packagePath);

        resourceManager.createFolder(folderResource);
    }


    @RequestMapping(value = "/definitions/packages/{packagePath}/{newName}", method = RequestMethod.POST)
    public void renamePackage(@PathVariable("packagePath") String packagePath, @PathVariable("newName") String newName) throws Exception {
        IResource resource = new DefaultResource(resourceRoot + "/" + packagePath);
        String newPath = resourceRoot + "/" + newName;

        List<String> oldDefinitionList = allListDefinition();

        resourceManager.rename(resource, newPath);

        callActivityModify(oldDefinitionList, newName);
    }


    @RequestMapping(value = "/definitions/packages/{packagePath}", method = RequestMethod.DELETE)
    public void deletePackage(@PathVariable("packagePath") String packagePath) throws Exception {
        IResource resource = new DefaultResource(resourceRoot + "/" + packagePath);
        List<String> oldDefinitionList = viewList(packagePath);
        for(int idx = 0; idx < oldDefinitionList.size(); idx++) {
            String str = oldDefinitionList.get(idx).substring(1, oldDefinitionList.get(idx).length());
            oldDefinitionList.set(idx, str);
        }

        resourceManager.delete(resource);

        callActivityDelete(oldDefinitionList);
    }


    @RequestMapping(value = "/definitions/processes/{filePath:.+}", method = RequestMethod.POST)
    public void moveRootProcess(@PathVariable("filePath") String filePath, @RequestBody String jsonData) throws Exception {
        moveProcess("", filePath, jsonData);
    }


    @RequestMapping(value = "/definitions/packages/{packagePath}/processes/{filePath:.+}", method = RequestMethod.POST)
    public void moveProcess(@PathVariable("packagePath") String packagePath, @PathVariable("filePath") String filePath, @RequestBody String jsonData) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<String, String>();
        map = mapper.readValue(jsonData, new TypeReference<Map<String, String>>(){});

        String targetPath = map.get("targetPath");

        if(!packagePath.equals("")) packagePath += "/";

        IResource resource = new DefaultResource(resourceRoot + "/" + packagePath + filePath);
        IContainer fileResource = new ContainerResource();
        fileResource.setPath(resourceRoot + "/" + targetPath);

        List<String> oldDefinitionList = allListDefinition();
        String newPackage = targetPath;

        IResource newResource = new DefaultResource(resourceRoot + "/" + newPackage + "/" + filePath);

        if(!resourceManager.exists(newResource)) {
            resourceManager.move(resource, fileResource);
            callActivityModify(oldDefinitionList, newPackage);
        } else
            throw new Exception("file exist");
    }

    public void callActivityModify(List<String> oldDefinitionList, String newPackage) throws Exception {
        List<String> allDefinitionList = allListDefinition();
        List<String> newDefinitionList = viewList(newPackage);
        for(int idx = 0; idx < newDefinitionList.size(); idx++) {
            newDefinitionList.set(idx, newDefinitionList.get(idx).substring(newDefinitionList.get(idx).lastIndexOf("/")+1, newDefinitionList.get(idx).length()));
        }

        for (int i = 0; i < allDefinitionList.size(); i++) {
            String definitionName = allDefinitionList.get(i).replace(".json", "");
            IResource targetResource = new DefaultResource(resourceRoot + "/" + allDefinitionList.get(i));
            Object definition = getDefinitionLocal(definitionName);

            if (definition instanceof ProcessDefinition) {
                ProcessDefinition processDefinition = (ProcessDefinition) definition;
                //getChildActivities 요소 찾기
                for (Activity activity : processDefinition.getChildActivities()) {
                    //callActivity 요소 찾기
                    if (activity instanceof CallActivity) {
                        CallActivity callActivity = (CallActivity) activity;
                        //변경 되기 전 프로세스 아이디와 현 패키지의 definitionId 비교
                        for(int j = 0; j < oldDefinitionList.size(); j++) {
                            String oldDefinitionId = oldDefinitionList.get(j).replace(".json", "");
                            if(callActivity.getDefinitionId().equals(oldDefinitionId)) {
                                // 변경 되기 전 프로세스 아이디와 변경 된 프로세스의 definitionId 비교
                                for(int k = 0; k < newDefinitionList.size(); k++) {
                                    String newDefinitionId = newDefinitionList.get(k).replace(".json", "");
                                    oldDefinitionId = oldDefinitionId.substring(oldDefinitionId.lastIndexOf("/")+1, oldDefinitionId.length());
                                    if(oldDefinitionId.equals(newDefinitionId)) {
                                        callActivity.setDefinitionId(newPackage + "/" + newDefinitionId);
                                        resourceManager.save(targetResource, definition);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void callActivityDelete(List<String> oldDefinitionList) throws Exception {
        List<String> allDefinitionList = allListDefinition();

        for (int i = 0; i < allDefinitionList.size(); i++) {
            String definitionName = allDefinitionList.get(i).replace(".json", "");
            IResource targetResource = new DefaultResource(resourceRoot + "/" + allDefinitionList.get(i));
            Object definition = getDefinitionLocal(definitionName);

            if (definition instanceof ProcessDefinition) {
                ProcessDefinition processDefinition = (ProcessDefinition) definition;
                //getChildActivities 요소 찾기
                for (Activity activity : processDefinition.getChildActivities()) {
                    //callActivity 요소 찾기
                    if (activity instanceof CallActivity) {
                        CallActivity callActivity = (CallActivity) activity;

                        //변경 되기 전 프로세스 아이디와 현 패키지의 definitionId 비교
                        for (int j = 0; j < oldDefinitionList.size(); j++) {
                            String oldDefinitionId = oldDefinitionList.get(j).replace(".json", "");
                            if (callActivity.getDefinitionId().equals(oldDefinitionId)) {
                                callActivity.setDefinitionId(""); //reset
                                resourceManager.save(targetResource, definition);
                            }
                        }
                    }
                }
            }
        }
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); //ignore null
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT); //ignore zero and false when it is int or boolean

        objectMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, "_type");
        return objectMapper;
    }

    @RequestMapping(value = "/definition/{defPath:.+}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Object getRootDefinition(@PathVariable("defPath") String definitionPath) throws Exception {
        return getDefinition("", definitionPath);
    }

    @RequestMapping(value = "/definition/{packagePath}/{defPath:.+}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Object getDefinition(@PathVariable("packagePath") String packagePath, @PathVariable("defPath") String definitionPath) throws Exception {

        if(!packagePath.equals("")) packagePath+= "/";

        IResource resource = new DefaultResource(resourceRoot + "/" + packagePath + definitionPath);
        Object object = resourceManager.getObject(resource);


        ObjectMapper objectMapper = createObjectMapper();
        DefinitionWrapper definitionWrapper = new DefinitionWrapper((Serializable) object);
        String uEngineProcessJSON = objectMapper.writeValueAsString(definitionWrapper);

        ///// Later we will use BPMN exporter: after 2017. 9.
//        TDefinitions tDefinitions = (TDefinitions) BPMNUtil.exportAdapt(object);
//
//        for(JAXBElement element : tDefinitions.getRootElement()) {
//            if (element.getValue() instanceof TProcess) {
//                TProcess bpmnProcess = (TProcess) element.getValue();
//
//                TExtensionElements tExtensionElements = new TExtensionElements();
//
//                org.uengine.bpmn.Field field = new org.uengine.bpmn.Field();
//                field.setName("object");
//                field.setStringValue("{a:'a'}");
//
//                tExtensionElements.getAny().add(field);
//                bpmnProcess.setExtensionElements(tExtensionElements);
//            }
//        }
//
//        org.omg.spec.bpmn._20100524.model.ObjectFactory objectFactory = new org.omg.spec.bpmn._20100524.model.ObjectFactory();
//
//        JAXBElement<TDefinitions> element = objectFactory.createDefinitions(tDefinitions);
//        JAXBContext jaxbContext = JAXBContext.newInstance(TDefinitions.class, org.uengine.bpmn.Field.class);
//
//        Marshaller marshaller = jaxbContext.createMarshaller();
//        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//
//        ByteArrayOutputStream bao = new ByteArrayOutputStream();
//        marshaller.marshal(element, bao);
//        String bpmn = new String(bao.toByteArray(), "UTF-8");
//
//        //return bpmn;

        return uEngineProcessJSON;
    }

    /**
     * TODO: need ACL referenced by token
     * @param definitionPath
     * @param definition
     * @throws Exception
     */
    @RequestMapping(value = "/definition/{defPath:.+}", method = RequestMethod.POST)
    public void putDefinition(@PathVariable("defPath") String definitionPath, @RequestBody String definition) throws Exception {

        IResource resource = new DefaultResource(resourceRoot + "/" + definitionPath);

        if(definitionPath.endsWith(".process")) {

            //TODO [severe] BPMNUtil.importAdapt(InputStream) must be available. using temp file will arise a multi-thread problem.
            ByteArrayInputStream bai = new ByteArrayInputStream(definition.getBytes("UTF-8"));

            UEngineUtil.copyStream(bai, new FileOutputStream("test.bpmn"));
            ProcessDefinition processDefinition = BPMNUtil.importAdapt(new File("test.bpmn"));

            resourceManager.save(resource, processDefinition);
        }else if(definitionPath.endsWith(".class")){

            ObjectMapper mapper = new ObjectMapper();
            ClassDefinition classDefinition = mapper.readValue(definition, ClassDefinition.class);

            resourceManager.save(resource, classDefinition);
        }else if(definitionPath.endsWith(".json")){

            ObjectMapper objectMapper = createObjectMapper();
            DefinitionWrapper definitionWrapper = objectMapper.readValue(definition, DefinitionWrapper.class);

            if(definitionWrapper.getDefinition()==null) throw new Exception("Definition is corrupt.");

            resourceManager.save(resource, definitionWrapper.getDefinition());

        }else
            throw new Exception("unknown resource type: " + definitionPath);
    }

    @RequestMapping(value = "/definition/{defPath:.+}", method = RequestMethod.DELETE)
    public void deleteRootDefinition(@PathVariable("defPath") String definitionPath) throws Exception {
        deleteDefinition("", definitionPath);
    }

    @RequestMapping(value = "/definition/{packagePath:.+}/{filePath:.+}", method = RequestMethod.DELETE)
    public void deleteDefinition(@PathVariable("packagePath") String packagePath,
                                 @PathVariable("filePath") String filePath) throws Exception {

        if(!packagePath.equals("")) packagePath += "/";

        IResource resource = new DefaultResource(resourceRoot + "/" + packagePath + filePath);
        List<String> deleteDefinitionList = new ArrayList<String>();
        deleteDefinitionList.add(packagePath + filePath);
        resourceManager.delete(resource);

        callActivityDelete(deleteDefinitionList);
    }

    @RequestMapping(value = "/definition/{defPath}/instance", method = RequestMethod.POST)
    @Transactional
    @ProcessTransactional

    public String runRootDefinition(@PathVariable("defPath") String definitionPath, @RequestBody String arguments) throws Exception {

        return runDefinition("", definitionPath, arguments);

    }

    // ----------------- execution services -------------------- //
    @RequestMapping(value = "/definition/{packagePath}/{filePath}/instance", method = RequestMethod.POST)
    @Transactional
    @ProcessTransactional
    public String runDefinition(@PathVariable("packagePath") String packagePath, @PathVariable("filePath") String filePath, @RequestBody String arguments) throws Exception {

        if(!packagePath.equals("")) packagePath += "/";

        IResource resource = new DefaultResource(resourceRoot + "/" + packagePath + filePath);
        Object definition = getDefinitionLocal(packagePath + filePath);

        if(definition instanceof ProcessDefinition){
            ProcessDefinition processDefinition = (ProcessDefinition) definition;

            org.uengine.kernel.ProcessInstance instance = applicationContext.getBean(
                    org.uengine.kernel.ProcessInstance.class,
                    //new Object[]{
                            processDefinition,
                            null,
                            null
                    //}
            );

            instance.execute();

            return instance.getInstanceId(); //TODO: returns HATEOAS _self link instead.
        }
        return null;

    }

    public Object getDefinitionLocal(String definitionPath) throws Exception {

        if(definitionPath.indexOf(".")==-1) definitionPath = definitionPath + ".json"; //TODO: check definition id convention

        IResource resource = new DefaultResource((definitionPath.startsWith(resourceRoot) ? definitionPath : resourceRoot + "/" + definitionPath));
        Object definition = resourceManager.getObject(resource);

        //TODO: move to framework
        if(definition instanceof NeedArrangementToSerialize){
            ((NeedArrangementToSerialize) definition).afterDeserialization();
        }

        if(definition instanceof ProcessDefinition) {
            ProcessDefinition processDefinition = (ProcessDefinition) definition;
            { //TODO: will be moved to afterDeserialize of ProcessDefinition
                processDefinition.setId(resource.getPath());
                if (processDefinition.getName() == null)
                    processDefinition.setName(resource.getPath());
            }
        }

        return definition;
    }


//    //TODO: must moved to InstanceService later.
//    @TransactionalEventListener(fallbackExecution=true, phase = TransactionPhase.BEFORE_COMMIT)
//    public void beforeProcessInstanceCommit(ChangeEvent<ProcessInstance> changeEvent) throws Exception {
//
//        ProcessInstance instance = changeEvent.getObject();
//
//        //TODO: check the instance is dirty
//        IResource resource = new DefaultResource("instances/" + instance.getInstanceId());
//        resourceManager.save(resource, ((DefaultProcessInstance)instance).getVariables());
//    }
//

    @Autowired
    ApplicationContext applicationContext;


}
