package org.uengine.five.activity;

import org.uengine.kernel.*;

/**
 * Created by uengine on 2017. 12. 4..
 */
public class RestCallActivity extends DefaultActivity {

    @Override
    protected void executeActivity(ProcessInstance instance) throws Exception {
        super.executeActivity(instance);
    }

    Role role;
        public Role getRole() {
            return role;
        }
        public void setRole(Role role) {
            this.role = role;
        }


    String uriTemplate;
        public String getUriTemplate() {
            return uriTemplate;
        }

        public void setUriTemplate(String uriTemplate) {
            this.uriTemplate = uriTemplate;
        }

    String inputPayloadTemplate;
        public String getInputPayloadTemplate() {
            return inputPayloadTemplate;
        }
        public void setInputPayloadTemplate(String inputPayloadTemplate) {
            this.inputPayloadTemplate = inputPayloadTemplate;
        }

    String method;
        public String getMethod() {
            return method;
        }
        public void setMethod(String method) {
            this.method = method;
        }

    ParameterContext[] outputMapping;
        public ParameterContext[] getOutputMapping() {
            return outputMapping;
        }
        public void setOutputMapping(ParameterContext[] outputMapping) {
            this.outputMapping = outputMapping;
        }

}
