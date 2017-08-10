package org.uengine.social.common.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

/**
 * Created by uengine on 2017. 8. 3..
 */
@Configuration
public class GlobalRepositoryRestConfigurer extends RepositoryRestConfigurerAdapter {
    /**
     * Jpa 레파지토리의
     *
     * @param config
     */
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.getCorsRegistry()
                .addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("POST", "GET", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("access_token", "Content-Type");
    }
}
