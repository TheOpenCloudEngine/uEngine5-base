package org.uengine.five.repository;

import org.metaworks.multitenancy.persistence.MultitenantRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.uengine.five.entity.Definition;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "definition", path = "definition")
public interface DefinitionRepository extends MultitenantRepository<Definition, String> {

    List<Definition> findByDefIdContaining(@Param("defId") String defId);

}
