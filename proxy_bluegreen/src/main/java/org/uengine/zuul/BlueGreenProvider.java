package org.uengine.zuul;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uengine on 2017. 10. 9..
 */
@Component
public class BlueGreenProvider {

    private List<BlueGreenServiceState> serviceStatusList = new ArrayList<>();

    private BlueGreenServiceState getDefault(String service) {
        BlueGreenServiceState serviceState = new BlueGreenServiceState();
        serviceState.setService(service);
        serviceState.setProd(BlueGreen.BLUE);
        serviceState.setDev(BlueGreen.GREEN);
        return serviceState;
    }

    public BlueGreenServiceState getService(String service) {
        BlueGreenServiceState selected = null;
        if(serviceStatusList != null){
            for (BlueGreenServiceState blueGreenServiceState : serviceStatusList) {
                if (blueGreenServiceState.getService().equals(service)) {
                    selected = blueGreenServiceState;
                }
            }
        }
        if (selected == null) {
            selected = getDefault(service);
        }
        return selected;
    }

    public void setService(BlueGreenServiceState serviceState) {
        boolean hasService = false;
        for (int i = 0; i < serviceStatusList.size(); i++) {
            if (serviceStatusList.get(i).getService().equals(serviceState.getService())) {
                serviceStatusList.set(i, serviceState);
                hasService = true;
            }
        }
        if (!hasService) {
            serviceStatusList.add(serviceState);
        }
    }
}
