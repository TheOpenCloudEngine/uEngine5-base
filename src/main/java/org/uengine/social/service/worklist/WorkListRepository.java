package org.uengine.social.service.worklist;


import org.metaworks.multitenancy.persistence.MultitenantRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by uengine on 2017. 6. 19..
 */
@RepositoryRestResource(collectionResourceRel = "worklist", path = "worklist")
public interface WorkListRepository extends MultitenantRepository<WorklistEntity, Long> {

    //@Query("select wl from WorkList wl where wl.instId = ?#{loggedUserId})")
//    @Query(value = "select * from BPM_WORKLIST where instId = ?1",
//            nativeQuery = true)
//    public List<WorklistEntity> findByInstId(@Param("instId") Long instId);

    public List<WorklistEntity> findByInstId(@Param("instId") Long instId);
}

