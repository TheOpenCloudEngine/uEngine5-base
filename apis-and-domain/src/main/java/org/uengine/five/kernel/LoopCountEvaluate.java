package org.uengine.five.kernel;

import org.uengine.kernel.Activity;
import org.uengine.kernel.Evaluate;
import org.uengine.kernel.ProcessInstance;
import org.uengine.util.UEngineUtil;

/**
 * Created by uengine on 2018. 2. 21..
 */
public class LoopCountEvaluate extends Evaluate {

    @Override
    public boolean isMet(ProcessInstance instance, String scope) throws Exception {

        Activity act = null;
        try {
            act = instance.getProcessDefinition().getActivity(getKey());
        } catch(Exception e) {
            act = instance.getProcessDefinition().getActivity(scope);
        }
        int actLoopBackCount = act.getLoopBackCount(instance);
        int compVal = 0;
        if (getValue().getClass() == String.class)
            compVal = Integer.parseInt((String) getValue());
        else if (getValue().getClass() == Integer.class)
            compVal = (Integer)getValue();
        if ("==".equals(getCondition()))
            return (actLoopBackCount == compVal);
        else if (">".equals(getCondition()))
            return (actLoopBackCount > compVal);
        else if ("<".equals(getCondition()))
            return (actLoopBackCount < compVal);
        else
            return false;

    }
}
