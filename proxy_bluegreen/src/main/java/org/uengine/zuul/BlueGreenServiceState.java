package org.uengine.zuul;

/**
 * Created by uengine on 2017. 10. 9..
 */
public class BlueGreenServiceState {
    private String service;
    private BlueGreen prod;
    private BlueGreen dev;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public BlueGreen getProd() {
        return prod;
    }

    public void setProd(BlueGreen prod) {
        this.prod = prod;
    }

    public BlueGreen getDev() {
        return dev;
    }

    public void setDev(BlueGreen dev) {
        this.dev = dev;
    }
}
