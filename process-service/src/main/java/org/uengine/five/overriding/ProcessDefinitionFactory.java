package org.uengine.five.overriding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uengine.processmanager.ProcessTransactionContext;
import org.uengine.processmanager.SimulatorTransactionContext;
import org.uengine.five.service.DefinitionServiceUtil;

import java.util.Map;

/**
 * Created by uengine on 2017. 10. 2..
 */
@Component
public class ProcessDefinitionFactory extends org.uengine.kernel.ProcessDefinitionFactory {
    public static final String unstructuredProcessDefinitionLocation = "Unstructured.process";
    public static final String codiProcessDefinitionFolder = "codi";

    public ProcessDefinitionFactory() {
        super(new SimulatorTransactionContext());
    }

    public ProcessDefinitionFactory(ProcessTransactionContext tc) {
        super(tc);
    }


    @Autowired
    DefinitionServiceUtil definitionService;


    protected Object getDefinitionSourceImpl(String location, boolean fromCompilationVersion, boolean shouldBeObjectResult) throws Exception {

        return definitionService.getDefinition(location + ".json"); //TODO: definition reference problematic someday
    }

    public void removeDefinition(String pdvid) throws Exception {
        throw new Exception("Not implemented.");
    }

    public String[] addDefinitionImpl(String belongingPdid, String defId, int version, String name, String description, boolean isAdhoc, Object definition, String folder, boolean overwrite, Map options) throws Exception {
        throw new Exception("Dont\' use this method.");
    }

}
