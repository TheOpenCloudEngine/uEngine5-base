package org.uengine.zuul;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by uengine on 2017. 10. 9..
 */
@RestController
public class BlueGreenController {

    @Autowired
    private BlueGreenProvider blueGreenProvider;

    @RequestMapping(value = "/service/{service}", method = GET)
    public BlueGreenServiceState getService(@PathVariable String service) {
        return blueGreenProvider.getService(service);
    }

    @RequestMapping(value = "/service", method = POST)
    public BlueGreenServiceState updateService(@RequestBody BlueGreenServiceState blueGreenServiceState) {
        blueGreenProvider.setService(blueGreenServiceState);
        return blueGreenServiceState;
    }
}
