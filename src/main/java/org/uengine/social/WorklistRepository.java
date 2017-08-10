package org.uengine.social;

import org.metaworks.multitenancy.persistence.MultitenantRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by uengine on 2017. 6. 19..
 */
@RepositoryRestResource(collectionResourceRel = "worklist", path = "worklist")
public interface WorklistRepository extends MultitenantRepository<WorklistEntity, Long> {

    public List<WorklistEntity> findByInstId(@Param("instId") Long instId);

}

