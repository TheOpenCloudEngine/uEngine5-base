package org.uengine.five.overriding;

import org.uengine.kernel.ProcessDefinition;
import org.uengine.modeling.resource.DefaultResource;
import org.uengine.modeling.resource.IResource;
import org.uengine.modeling.resource.Serializer;

import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * Created by uengine on 2018. 1. 24..
 *
 * JPAProcessInstance variation which stores data in the CLOB field (varLob) not into the file.
 *
 */
public class CLOBProcessInstance extends JPAProcessInstance {
    public CLOBProcessInstance(ProcessDefinition procDefinition, String instanceId, Map options) throws Exception {
        super(procDefinition, instanceId, options);
    }

    @Override
    protected Map loadVariables() throws Exception {

        if(getProcessInstanceEntity()==null) return null;

        byte[] bytes = getProcessInstanceEntity().getVarLob();
        ByteArrayInputStream bai = new ByteArrayInputStream(bytes);

        return (Map) Serializer.deserialize(bai);

    }

    @Override
    protected void saveVariables() throws Exception {
        if(getProcessInstanceEntity()==null) return;

        String serializedVars = Serializer.serialize(getVariables());
        getProcessInstanceEntity().setVarLob(serializedVars.getBytes());
    }

}
