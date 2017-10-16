package org.uengine.social.repository;

import org.metaworks.multitenancy.persistence.MultitenantRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.uengine.social.entity.ProcessInstanceEntity;

import java.util.List;

/**
 * Created by uengine on 2017. 6. 19..
 */
@RepositoryRestResource(collectionResourceRel = "instances", path = "instances")
public interface ProcessInstanceRepository extends MultitenantRepository<ProcessInstanceEntity, Long> {

    @Query("select pi from ProcessInstanceEntity pi where exists (select 1 from WorklistEntity wl where wl.endpoint = ?#{loggedUserId})")
    List<ProcessInstanceEntity> findAllICanSee();

    @Query("select pi from ProcessInstanceEntity pi where pi.defId like CONCAT('%',:defId,'%') or pi.instId = :instId or pi.status = :status or pi.eventHandler = :eventHandler or pi.name like :name or pi.startedDate = :startedDate or pi.finishedDate = :finishedDate")
    List<ProcessInstanceEntity> findFilterICanSee(
                                                  @Param("defId") String defId,
                                                  @Param("instId") Long instId,
                                                  @Param("status") String status,
                                                  @Param("eventHandler") String eventHandler,
                                                  @Param("name") String name,
                                                  @Param("startedDate") String startedDate,
                                                  @Param("finishedDate") String finishedDate
                                                  );
}

