package org.uengine.social.service.definition;

import org.directwebremoting.impl.DefaultContainer;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.uengine.five.JPAProcessInstance;
import org.uengine.kernel.AbstractProcessInstance;
import org.uengine.kernel.ProcessDefinition;
import org.uengine.modeling.resource.*;
import org.uengine.processpublisher.BPMNUtil;
import org.uengine.util.UEngineUtil;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by uengine on 2017. 8. 9..
 *
 * Implementation Principals:
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



    String processInstanceClass;
        public String getProcessInstanceClass() {
            return processInstanceClass;
        }

        public void setProcessInstanceClass(String processInstanceClass) {
            this.processInstanceClass = processInstanceClass;
        }


    @PostConstruct
    public void init(){
//        try {
//            //AbstractProcessInstance.USE_CLASS = Class.forName(getProcessInstanceClass());
//        } catch (ClassNotFoundException e) {
//
//            e.printStackTrace(System.err); // For the cloud applications, system out and err is the standard logging framework
//
//            AbstractProcessInstance.USE_CLASS = JPAProcessInstance.class;
//        }

    }

    // ---------------- CRUD mappings -------------------- //

    @RequestMapping(value = "/definitions", method = RequestMethod.GET)
    public List<String> listDefinition() throws Exception {

        IContainer resource = new ContainerResource();
        resource.setPath(resourceRoot + "/");
        List<IResource> resources = resourceManager.listFiles(resource);

        List<String> resourcesList = new ArrayList<String>();

        for(IResource resource1 : resources){
            resourcesList.add(resource1.getPath().substring(resourceRoot.length()));
        }

        return resourcesList;  //TODO: Need to be changed to HATEOAS _self link instead
    }

    @RequestMapping(value = "/definition/{defPath}", method = RequestMethod.GET)
    public Object getDefinition(@PathVariable("defPath") String definitionPath) throws Exception {

        IResource resource = new DefaultResource(resourceRoot + "/" + definitionPath);
        Object object = resourceManager.getObject(resource);

        TDefinitions tDefinitions = (TDefinitions) BPMNUtil.exportAdapt(object);

        org.omg.spec.bpmn._20100524.model.ObjectFactory objectFactory = new org.omg.spec.bpmn._20100524.model.ObjectFactory();

        JAXBElement<TDefinitions> element = objectFactory.createDefinitions(tDefinitions);
        JAXBContext jaxbContext = JAXBContext.newInstance(TDefinitions.class);

        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        marshaller.marshal(element, bao);
        String bpmn = new String(bao.toByteArray(), "UTF-8");

        return bpmn;
    }

    @RequestMapping(value = "/definition/{defPath}", method = RequestMethod.POST)
    public void putDefinition(@PathVariable("defPath") String definitionPath, @RequestBody String definition) throws Exception {

        IResource resource = new DefaultResource(resourceRoot + "/" + definitionPath);

        ByteArrayInputStream bai = new ByteArrayInputStream(definition.getBytes("UTF-8"));

        UEngineUtil.copyStream(bai, new FileOutputStream("test.bpmn"));
        ProcessDefinition processDefinition = BPMNUtil.importAdapt(new File("test.bpmn"));

        resourceManager.save(resource, processDefinition);
    }

    @RequestMapping(value = "/definition/{defPath}", method = RequestMethod.DELETE)
    public void deleteDefinition(@PathVariable("defPath") String definitionPath) throws Exception {

        IResource resource = new DefaultResource(resourceRoot + "/" + definitionPath);
        resourceManager.delete(resource);
    }


    // ----------------- execution services -------------------- //

    @RequestMapping(value = "/definition/{defPath}/instance", method = RequestMethod.POST)
    public String runDefinition(@PathVariable("defPath") String definitionPath, @RequestBody Object arguments) throws Exception {

        Object definition = getDefinition(definitionPath);

        if(definition instanceof ProcessDefinition){
            ProcessDefinition processDefinition = (ProcessDefinition) definition;

            //org.uengine.kernel.ProcessInstance instance = processDefinition.createInstance();

            org.uengine.kernel.ProcessInstance instance = applicationContext.getBean(
                    org.uengine.kernel.ProcessInstance.class,
                    new Object[]{
                        processDefinition,
                            null,
                            null
                    }
            );

            instance.execute();

            return instance.getInstanceId(); //TODO: returns HATEOAS _self link instead.
        }

        return null;

    }

    @Autowired
    ApplicationContext applicationContext;


}
