package org.uengine.social.repository;

import org.metaworks.multitenancy.persistence.MultitenantRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.uengine.social.entity.ProcessInstanceEntity;

import java.util.List;

/**
 * Created by uengine on 2017. 6. 19..
 */
@RepositoryRestResource(collectionResourceRel = "instances", path = "instances")
public interface ProcessInstanceRepository extends MultitenantRepository<ProcessInstanceEntity, Long> {

    @Query("select pi from ProcessInstanceEntity pi where exists (select 1 from RoleMappingEntity rm where rm.endpoint = ?#{loggedUserId})")
    List<ProcessInstanceEntity> findAllICanSee();

}

