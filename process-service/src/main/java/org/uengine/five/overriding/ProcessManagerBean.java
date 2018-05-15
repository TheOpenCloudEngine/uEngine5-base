package org.uengine.five.overriding;

import org.springframework.stereotype.Component;
import org.uengine.five.framework.ProcessTransactional;
import org.uengine.kernel.Activity;
import org.uengine.kernel.ProcessDefinition;
import org.uengine.kernel.ProcessInstance;
import org.uengine.kernel.bpmn.TimerEvent;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * Created by uengine on 2018. 3. 4..
 */
@Component
public class ProcessManagerBean extends org.uengine.processmanager.ProcessManagerBean{

    @ProcessTransactional
    public Serializable sendMessage(String instanceId, String message, Serializable payload) throws RemoteException {

        try{
            ProcessInstance instance = getInstance(instanceId);
            ProcessDefinition definition = instance.getProcessDefinition();

            if("onTime".equals(message) && payload instanceof String) {
                TimerEvent timerActivity = (TimerEvent) definition.getActivity((String)payload);

                return timerActivity.onMessage(instance, null);
            }else
                return (Serializable)definition.fireMessage(message, instance, payload); //send message to the whole subscribers regardless of given instance id. it means even if there're instance which didn't subscribe, the instance will be ignored.

        }catch(Exception e){
            e.printStackTrace();
            throw new RemoteException("ProcessManagerError:"+e.getMessage(), e);
        }

    }
}
