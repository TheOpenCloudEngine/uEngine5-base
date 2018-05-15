package org.uengine.five.service;

import org.metaworks.WebFieldDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.uengine.cloud.app.AppCreate;
import org.uengine.cloud.app.TemplateSpecific;
import org.uengine.cloud.catalog.FileMapping;
import org.uengine.cloud.services.AppService;
import org.uengine.modeling.Relation;
import org.uengine.uml.ClassDiagram;
import org.uengine.uml.model.Aggregation;
import org.uengine.uml.model.Association;
import org.uengine.uml.model.Attribute;
import org.uengine.uml.model.ClassDefinition;
import org.uengine.util.UEngineUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by uengine on 2018. 2. 23..
 */
@RestController
public class AppGenerationService {

    @Autowired
    AppService appService;

//    @Autowired
//    JavaReverseService javaReverseService;

    @Autowired
    DefinitionServiceImpl definitionService;

    @RequestMapping(value="/create-app/{classDefinitionPath:.+}", method = RequestMethod.POST)
    public void createApp(@PathVariable("classDefinitionPath") String classDefinitionPath) throws Exception {

//        Map appInfo = null;
//        try {
//            appInfo = getApp(classDefinitionPath);
//        }catch(Exception e){
//        }
//
//        if(appInfo!=null){ //if existing app, update the model.
//
//
//        }

        ClassDiagram classDiagram = (ClassDiagram) definitionService.getDefinitionLocal(classDefinitionPath + ".xml");

        String appNameSeed = createAppName(classDiagram.getName());

        AppCreate appCreate = new AppCreate();
        appCreate.setAppName(appNameSeed);
        appCreate.setCategoryItemId("metaworks4");
        appCreate.setExternalDevDomain(appCreate.getAppName() + "-dev.pas-mini.io");
        appCreate.setExternalProdDomain(appCreate.getAppName() + ".pas-mini.io");
        appCreate.setExternalStgDomain(appCreate.getAppName() + "-stg.pas-mini.io");
        appCreate.setCpu(0.4);
        appCreate.setMem(512);
        appCreate.setInstances(1);
        appCreate.setAppNumber(30);

        TemplateSpecific templateSpecific = new TemplateSpecific();
        templateSpecific.setFileMappings(new ArrayList<FileMapping>());

        Map<String, ClassDefinition> classDefinitionByName = new HashMap<String, ClassDefinition>();

        for(ClassDefinition classDefinition : classDiagram.getClassDefinitions()) {
            classDefinitionByName.put(classDefinition.getName(), classDefinition);

            FileMapping fileMapping = new FileMapping();
            fileMapping.setFile("Entity.java");
            fileMapping.setPath("src/main/java/hello/"+ classDefinition.getName() +".java");

            WebFieldDescriptor keyField = null;
            for(WebFieldDescriptor attribute : classDefinition.getFieldDescriptors()){
                if("true".equals(attribute.getAttribute("isKey"))) keyField = attribute;

                if(attribute.getAttributes()==null)
                    attribute.setAttributes(new Properties());

                attribute.getAttributes().setProperty("nameForSetterGetter", attribute.getName().substring(0, 1).toUpperCase() + attribute.getName().substring(1));

                String relationAnnotation = attribute.getAttribute("relationAnnotation");
                if(relationAnnotation!=null){

                    String mappedBy = attribute.getAttribute("relationAnnotation.mappedBy");
                    attribute.setAttribute("multiplicity", "@" + relationAnnotation + (mappedBy!=null ? "(mappedBy=\"" + mappedBy + "\")" : ""));

                }
            }

            if(keyField==null){
                keyField = classDefinition.getFieldDescriptors()[0];
                keyField.getAttributes().setProperty("isKey", "true");
            }
            classDefinition.setKeyFieldDescriptor(keyField);

            fileMapping.setData(classDefinition);
            templateSpecific.getFileMappings().add(fileMapping);


            fileMapping = new FileMapping();
            fileMapping.setFile("Repository.java");
            fileMapping.setPath("src/main/java/hello/"+ classDefinition.getName() +"Repository.java");

            fileMapping.setData(classDefinition);

            templateSpecific.getFileMappings().add(fileMapping);
        }


        ///// server side attribute generation is not used anymore.
//        for(Relation classRelation : classDiagram.getRelations()) {
//
//            if(classRelation instanceof Association){
//
//                String sourceAnnotationPart = null;{
//                    if (classRelation instanceof Aggregation) {
//                        Aggregation aggregation = (Aggregation) classRelation;
//                        sourceAnnotationPart = getAnnotationPart(aggregation.getSourceMultiplicity());
//                    }
//
//                    if (sourceAnnotationPart == null) sourceAnnotationPart = "One";
//                }
//
//                String targetAnnotationPart = null;{
//                    if(classRelation instanceof Aggregation){
//                        Aggregation aggregation = (Aggregation) classRelation;
//                        targetAnnotationPart = getAnnotationPart(aggregation.getTargetMultiplicity());
//                    }
//
//                    if(targetAnnotationPart==null) targetAnnotationPart = "One";
//
//                }
//
//                Association association = (Association) classRelation;
//                String sourceName = association.getSourceClassName();
//                String targetName = association.getTargetClassName();
//
//                //validation
//                if(!UEngineUtil.isNotEmpty(sourceName) || !UEngineUtil.isNotEmpty(targetName)) continue;
//
//                ClassDefinition sourceClassDefinition = (ClassDefinition) classDefinitionByName.get(association.getSourceClassName());
//                ClassDefinition targetClassDefinition = (ClassDefinition) classDefinitionByName.get(association.getTargetClassName());
//
//
//
//
//

//                Attribute attributeAtTarget = new Attribute();{
//                    String className = "hello." + sourceClassDefinition.getName();
//
//                    if("Many".equals(sourceAnnotationPart)) {
//                        className = "List<" + className + ">";
//
//                        sourceName = association.getSourceClassName().substring(0, 1).toLowerCase() + association.getSourceClassName().substring(1) + "List";
//                    }
//
//                    if(sourceName == null)
//                        sourceName = association.getSourceClassName().substring(0, 1).toLowerCase() + association.getSourceClassName().substring(1);
//
//
//                    attributeAtTarget.setName(sourceName);
//                    attributeAtTarget.setAttribute("nameForSetterGetter", sourceName.substring(0, 1).toUpperCase() + sourceName.substring(1));
//                    attributeAtTarget.setClassName(className);
//                }
//
//                Attribute attributeAtSource = new Attribute();{
//                    String className = "hello." + targetClassDefinition.getName();
//                    if("Many".equals(targetAnnotationPart)){
//                        className = "List<" + className + ">";
//
//                        targetName = association.getTargetClassName().substring(0, 1).toLowerCase() + association.getTargetClassName().substring(1) + "List";
//
//                    }
//
//                    if(targetName == null)
//                        targetName = association.getTargetClassName().substring(0, 1).toLowerCase() + association.getTargetClassName().substring(1);
//
//                    attributeAtSource.setName(targetName);
//                    attributeAtSource.setAttribute("nameForSetterGetter", targetName.substring(0, 1).toUpperCase() + targetName.substring(1));
//
//                    attributeAtSource.setClassName(className);
//                }
//
//
//
//
//                attributeAtSource.setAttribute("multiplicity", "@" + sourceAnnotationPart + "To" + targetAnnotationPart);
//                attributeAtTarget.setAttribute("multiplicity", "@" + targetAnnotationPart + "To" + sourceAnnotationPart);
//
//                WebFieldDescriptor existingAttribute = sourceClassDefinition.getFieldDescriptor(attributeAtSource.getName());
//                if(existingAttribute!=null){
//                    sourceClassDefinition.setFieldDescriptor(attributeAtSource.getName(), attributeAtSource);
//                }else
//                    sourceClassDefinition.addFieldDescriptor(attributeAtSource);
//
//                existingAttribute = targetClassDefinition.getFieldDescriptor(attributeAtTarget.getName());
//                if(existingAttribute!=null){
//                    targetClassDefinition.setFieldDescriptor(attributeAtTarget.getName(), attributeAtTarget);
//                }else
//                    targetClassDefinition.addFieldDescriptor(attributeAtTarget);

//            }
//
//        }



        appCreate.setTemplateSpecific(templateSpecific);



        appService.createApp(appCreate, "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ1ZW5naW5lLWNsb3VkLWlhbSIsImNvbnRleHQiOnsiY2xpZW50S2V5IjoibXktY2xpZW50LWtleSIsInNjb3BlcyI6WyJjbG91ZC1zZXJ2ZXIiXSwidHlwZSI6InVzZXIiLCJ1c2VyTmFtZSI6Imp5amFuZ0B1ZW5naW5lLm9yZyIsInVzZXIiOnsidXNlck5hbWUiOiJqeWphbmdAdWVuZ2luZS5vcmciLCJtZXRhRGF0YSI6eyJhY2wiOiJhZG1pbiIsImVtYWlsIjoianlqYW5nQHVlbmdpbmUub3JnIiwibmFtZSI6IuyepeynhOyYgSIsImdpdGxhYi1pZCI6NX0sInJlZ0RhdGUiOjE1MTUzOTgzMDQxMjMsInVwZERhdGUiOjE1MTU2NDkyNTk1NTl9LCJyZWZyZXNoVG9rZW4iOiJhYTVlZmVkZC01YzUyLTQ5YTctOWY2Yy04ZWIxZGViM2EzYWUifSwiY2xhaW0iOnt9LCJleHAiOjE1MTc5MzM4NDQsImlhdCI6MTUxNzkyNjY0NH0.J4jhwvnR1O6nvFdjeoV-WrljgX2zdhPbwLjd085Jt8s");

    }

    public static String createAppName(String classDiagramName) {

        int dotPos = classDiagramName.indexOf(".");
        if(dotPos > -1){
            classDiagramName = classDiagramName.substring(0, dotPos + 1);
        }
        return "backend-" + classDiagramName.toLowerCase().replace(' ', '-');
    }


    private String getAnnotationPart(String multiplicity){

        String annotationPart = null;

        if("1..n".equals(multiplicity) || "0..n".equals(multiplicity)){
            annotationPart = "Many";
        }else if("1".equals(multiplicity) || "0".equals(multiplicity)){
            annotationPart = "One";
        }

        return annotationPart;
    }


    @RequestMapping(value="/create-app/{classDefinitionPath}", method = RequestMethod.GET)
    public Map getApp(@PathVariable("classDefinitionPath") String classDefinitionPath) throws Exception {

        String appNameSeed = createAppName(classDefinitionPath);

        return appService.getApp(appNameSeed,"eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ1ZW5naW5lLWNsb3VkLWlhbSIsImNvbnRleHQiOnsiY2xpZW50S2V5IjoibXktY2xpZW50LWtleSIsInNjb3BlcyI6WyJjbG91ZC1zZXJ2ZXIiXSwidHlwZSI6InVzZXIiLCJ1c2VyTmFtZSI6Imp5amFuZ0B1ZW5naW5lLm9yZyIsInVzZXIiOnsidXNlck5hbWUiOiJqeWphbmdAdWVuZ2luZS5vcmciLCJtZXRhRGF0YSI6eyJhY2wiOiJhZG1pbiIsImVtYWlsIjoianlqYW5nQHVlbmdpbmUub3JnIiwibmFtZSI6IuyepeynhOyYgSIsImdpdGxhYi1pZCI6NX0sInJlZ0RhdGUiOjE1MTUzOTgzMDQxMjMsInVwZERhdGUiOjE1MTU2NDkyNTk1NTl9LCJyZWZyZXNoVG9rZW4iOiJhYTVlZmVkZC01YzUyLTQ5YTctOWY2Yy04ZWIxZGViM2EzYWUifSwiY2xhaW0iOnt9LCJleHAiOjE1MTc5MzM4NDQsImlhdCI6MTUxNzkyNjY0NH0.J4jhwvnR1O6nvFdjeoV-WrljgX2zdhPbwLjd085Jt8s");
    }

//    @Bean
//    public ObjectMapper objectMapper(){
//        return createTypedJsonObjectMapper();
//    }
//
}
