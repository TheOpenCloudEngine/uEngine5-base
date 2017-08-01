package org.uengine.social;

import org.metaworks.multitenancy.persistence.MultitenantRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by uengine on 2017. 6. 19..
 */
@RepositoryRestResource(collectionResourceRel = "instances", path = "instances")
public interface ProcessInstanceRepository extends MultitenantRepository<ProcessInstance, Long> {

    @Query("select pi from ProcessInstance pi where exists (select 1 from RoleMapping rm where rm.endpoint = ?#{loggedUserId})")
    List<ProcessInstance> findAllICanSee();

}

