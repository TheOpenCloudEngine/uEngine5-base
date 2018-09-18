package org.uengine.five;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaServer
@RestController
public class EurekaApplication {

    private static Log logger = LogFactory.getLog(EurekaApplication.class);

    @RequestMapping("/health")
    public String health() {
        return "";
    }

    public static void main(String[] args) {


        new SpringApplicationBuilder(EurekaApplication.class).web(true).run(args);
    }

}
