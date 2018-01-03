package org.uengine.five.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;
import org.uengine.modeling.resource.Version;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by uengine on 2018. 1. 2..
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Relation(value = "version", collectionRelation = "versions")
public class VersionResource extends ResourceSupport{



    Version version;

    public VersionResource(Version version) throws Exception {
        setVersion(version);

        add(
                linkTo(
                        methodOn(DefinitionServiceImpl.class)
                                .getVersion(
                                        version.toString()
                                )
                ).withSelfRel()
        );

        add(
                linkTo(
                        methodOn(DefinitionServiceImpl.class)
                                .listVersionDefinitions(
                                        version.toString(), ""
                                )
                ).withRel("definitions")
        );

        add(
            linkTo(
                methodOn(DefinitionServiceImpl.class)
                    .makeProduction(
                            version.toString()
                    )
            ).withRel("makeProduction")
        );

    }

    public Version getVersion() {
            return version;
        }
    public void setVersion(Version version) {
            this.version = version;
        }


}
