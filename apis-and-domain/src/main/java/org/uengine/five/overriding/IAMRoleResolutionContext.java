package org.uengine.five.overriding;

import org.uengine.kernel.ProcessDefinition;
import org.uengine.kernel.ProcessInstance;
import org.uengine.kernel.RoleMapping;
import org.uengine.kernel.RoleResolutionContext;

import java.util.Map;

/**
 * Created by uengine on 2018. 4. 5..
 */
public class IAMRoleResolutionContext extends RoleResolutionContext {
    @Override
    public RoleMapping getActualMapping(ProcessDefinition pd, ProcessInstance instance, String tracingTag, Map options) throws Exception {
        RoleMapping roleMapping = RoleMapping.create();
        roleMapping.setEndpoint(getScope());
        return roleMapping;
    }

    @Override
    public String getDisplayName() {
        return "Who has the scope '" + getScope() + "'";
    }

    String scope;
        public String getScope() {
            return scope;
        }
        public void setScope(String scope) {
            this.scope = scope;
        }

}
