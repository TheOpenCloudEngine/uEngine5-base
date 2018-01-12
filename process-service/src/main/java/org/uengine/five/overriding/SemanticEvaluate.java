package org.uengine.five.overriding;

import org.metaworks.dwr.MetaworksRemoteService;
import org.uengine.five.service.SemanticEntityService;
import org.uengine.five.service.SemanticValue;
import org.uengine.kernel.Evaluate;
import org.uengine.kernel.ProcessInstance;
import org.uengine.util.UEngineUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

            String[] texts = text.toString().split(" ");
            Map<String, AtomicInteger> weightForValues = new HashMap<String, AtomicInteger>();

            for(String theText : texts){
                List<SemanticValue> semanticValues = semanticEntityService.findBySynonym(theText);

                String theValue = null;
                if(semanticValues!=null && semanticValues.size()>0){
                    theValue = semanticValues.get(0).getValue();

                    AtomicInteger curr = weightForValues.get(theValue);

                    if(curr==null) curr = new AtomicInteger(0);
                    curr.incrementAndGet();

                    weightForValues.put(theValue, curr);
                }

            }

            int total = 0;
            int max = 0, latterMax = 0;
            String maxAnswer = null;

            for(String answer: weightForValues.keySet()){

                AtomicInteger weight = weightForValues.get(answer);

                total = total + weight.get();

                if(weight.get() > max){
                    latterMax = max;
                    max = weight.get();

                }
            }

            if(maxAnswer!=null && max/total > 60){
                return (maxAnswer.equals(getValue()));
            }

            throw new Exception("Can't understand the expression : " + text);

        }else

            return super.isMet(instance, scope);
    }
}
