package org.uengine.five;

import org.springframework.context.ApplicationEvent;
import org.uengine.kernel.ProcessInstance;

/**
 * Created by uengine on 2017. 8. 23..
 */
public class ProcessInstanceChangeEvent extends ChangeEvent<ProcessInstance> {

    public ProcessInstanceChangeEvent(ProcessInstance processInstance) {
        super(processInstance);
    }
}
