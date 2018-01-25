package org.uengine.five.repository;

import org.metaworks.multitenancy.persistence.MultitenantRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.uengine.five.entity.Definition;

@RepositoryRestResource(collectionResourceRel = "definition", path = "definition")
public interface DefinitionRepository extends MultitenantRepository<Definition, String> {

}
