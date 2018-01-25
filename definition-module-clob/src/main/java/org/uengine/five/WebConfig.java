package org.uengine.five;

import org.metaworks.springboot.configuration.Metaworks4WebConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.uengine.modeling.resource.LocalFileStorage;
import org.uengine.modeling.resource.Storage;

/**
 * Created by uengine on 2018. 1. 11..
 */
@EnableJpaRepositories(basePackageClasses = {WebConfig.class})
@EnableWebMvc
@Configuration
public class WebConfig extends Metaworks4WebConfig{
    @Override
    protected Storage storage() {
        return new LocalFileStorage();
    }
}
