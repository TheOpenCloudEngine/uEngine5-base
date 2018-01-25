package org.uengine.five.repository;

import org.metaworks.multitenancy.persistence.MultitenantRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.uengine.five.entity.DefinitionVersion;

@RepositoryRestResource(collectionResourceRel = "version", path = "version")
public interface DefinitionVersionRepository extends MultitenantRepository<DefinitionVersion, Long> {

}
