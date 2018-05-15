package org.uengine.five.overriding;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.JsonPath;
import net.sf.json.JSONObject;
import org.metaworks.dwr.MetaworksRemoteService;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.uengine.five.ProcessServiceApplication;
import org.uengine.five.service.DefinitionServiceUtil;
import org.uengine.kernel.ProcessInstance;
import org.uengine.kernel.ProcessVariable;
import org.uengine.uml.ClassDiagram;
import org.uengine.uml.model.Attribute;
import org.uengine.uml.model.ClassDefinition;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by uengine on 2018. 2. 15..
 */
public class RestResourceProcessVariableValue implements Serializable{

    String uri;
        public String getUri() {
            return uri;
        }
        public void setUri(String uri) {
            this.uri = uri;
        }

//    String externalUri;
//        public String getExternalUri() {
//            return externalUri;
//        }
//        public void setExternalUri(String externalUri) {
//            this.externalUri = externalUri;
//        }


    transient Object value;
        public Object getValue() {
            return value;
        }
        public void setValue(Object value) {
            this.value = value;
        }



    transient private String serviceName;
    transient private ClassDefinition classDefinition;

    private String findProperServer(ProcessVariable variable, ProcessInstance instance){
        String className = variable.getTypeClassName();
        if (className.indexOf("#") == -1) throw new RuntimeException("This variable type cannot persistable with REST:" + variable.getTypeClassName());

        String[] definitionAndClassName = className.split("#");

        DefinitionServiceUtil definitionServiceUtil = MetaworksRemoteService.getComponent(DefinitionServiceUtil.class);
        try {
            ClassDiagram classDiagram = (ClassDiagram) definitionServiceUtil.getDefinition(definitionAndClassName[0]);

            classDefinition = classDiagram.getClassDefinition(definitionAndClassName[1]);
        } catch (Exception e) {
        }

        serviceName = createAppName(definitionAndClassName[0]);
        String path = definitionAndClassName[1].toLowerCase() + "s";

        // select proper server

        LoadBalancerClient loadBalancerClient = MetaworksRemoteService.getInstance().getComponent(LoadBalancerClient.class);

        DiscoveryClient discoveryClient = MetaworksRemoteService.getInstance().getComponent(DiscoveryClient.class);
        List<String> services = discoveryClient.getServices();

        if (loadBalancerClient == null) throw new RuntimeException("There's no load balancer to find proper backend server for " + serviceName);

        ServiceInstance serviceInstance = loadBalancerClient.choose(serviceName);

        if (serviceInstance == null) throw new RuntimeException("There's no backend server for " + serviceName);

        return serviceInstance.getUri().toString() +"/" + path;
    }

    /**
     * Storing data in server-side approach is not good if the data graph is complicated. - since it has transaction issues
     * In case of the data is transferred with full body, this approach will be used.
     * @param data
     * @param variable
     * @param instance
     */
    public RestResourceProcessVariableValue lightweight(Serializable data, ProcessVariable variable, ProcessInstance instance) {

        // check if the data is already a lightweight object:

        if(data instanceof Map){
            Map dataMap = (Map)data;
            if(dataMap.get("uri")!=null && getClass().getName().equals(dataMap.get("_type"))){
                RestResourceProcessVariableValue restResourceProcessVariableValue = new RestResourceProcessVariableValue();
                restResourceProcessVariableValue.setUri((String) dataMap.get("uri"));

                return restResourceProcessVariableValue;
            }
        }

        // create uri

        String baseUrl = (findProperServer(variable, instance));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll((Map) data);

        JSONObject associationFields = new JSONObject();

        // remove association fields from the aggregation root object and stores them so that they can be processed later.
        if(classDefinition!=null) {
            for (Attribute attribute : classDefinition.getFieldDescriptors()){
                if(attribute.getAttribute("relationAnnotation")!=null){

                    Object value = jsonObject.get(attribute.getName());
                    if(value instanceof List){
                        List listValue = (List) value;

                        associationFields.put(attribute.getName(), value);

                        jsonObject.remove(attribute.getName());
                    }
                }
            }
        }

        HttpEntity<String> body = new HttpEntity<String>(jsonObject.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        // send request and parse result
        ResponseEntity<String> response = restTemplate
                .exchange(baseUrl, HttpMethod.POST, body, String.class);

        setUri((String) JsonPath.read(response.getBody(), "$._links.self.href"));

        // change the actual host with service name
        try {
            URL url = new URL(getUri());

            url.getPath();

            setUri(url.getProtocol() + "://" + serviceName + "/" + url.getPath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // POST the association resources
//        for (Object key : associationFields.keySet()){
//            List value = (List) associationFields.get(key);
//
//            Attribute attribute = (Attribute) classDefinition.getFieldDescriptor((String) key);
//
//            HttpEntity<String> body = new HttpEntity<String>(jsonObject.toString(), headers);
//
//            RestTemplate restTemplate = new RestTemplate();
//
//            restTemplate.getMessageConverters()
//                    .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
//
//            // send request and parse result
//            ResponseEntity<String> response = restTemplate
//                    .exchange(baseUrl, HttpMethod.POST, body, String.class);
//
//            associationFields.put(attribute.getName(), value);
//
//        }

        return this;

    }

    public void fillValue(ProcessVariable variable, ProcessInstance instance){

        // get the id part of uri
        String id = getUri().substring(getUri().lastIndexOf('/'), getUri().length());

        String baseUrl = findProperServer(variable, instance);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = null;
        response = restTemplate.exchange(baseUrl + "/" + id,
                HttpMethod.GET, null, String.class);

        String result = response.getBody();
        JsonNode jsonNode;

        try {
            jsonNode = ProcessServiceApplication.objectMapper.readTree(result);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load variable from REST: " + getUri(), e);
        }

        setValue(jsonNode);

    }


    public static String createAppName(String classDiagramName) {
        return "backend-" + classDiagramName.toLowerCase().replace(' ', '-');
    }




}
