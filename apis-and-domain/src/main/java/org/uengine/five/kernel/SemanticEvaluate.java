package org.uengine.five.kernel;

import org.metaworks.dwr.MetaworksRemoteService;
import org.uengine.five.service.SemanticEntityService;
import org.uengine.kernel.Evaluate;
import org.uengine.kernel.ProcessInstance;
import org.uengine.util.UEngineUtil;

/**
 * Created by uengine on 2018. 1. 11..
 */
public class SemanticEvaluate extends Evaluate {

    @Override
    public boolean isMet(ProcessInstance instance, String scope) throws Exception {

        if("means".equals(getCondition())){

            SemanticEntityService semanticEntityService = MetaworksRemoteService.getComponent(SemanticEntityService.class);

            Object text = null;

            if(getKey()==null && pv!=null){
                setKey(pv.getName());
            }

            if(!UEngineUtil.isNotEmpty(getKey())){
                return true; //ignore the condition
            }

            if( getKey().startsWith("[instance]")  ){
                text = instance.getBeanProperty(getKey());
            }else{
                text = instance.getProcessDefinition().getProcessVariable(getKey()).get(instance, "");
            }

            if(text == null){
                if (getValue()==null) return true;

                if (getValue() instanceof String){
                    return (((String) getValue()).length() == 0);
                }
            }

            if(getValue()==null){
                if(text == null) return true;

                if(text instanceof  String){
                    return (((String)text).length() == 0);
                }
            }

            int probability = semanticEntityService.mean(text.toString(), getValue().toString());

            if(probability > 1){
                return true;
            }else
                return false;

        }else

            return super.isMet(instance, scope);
    }
}
