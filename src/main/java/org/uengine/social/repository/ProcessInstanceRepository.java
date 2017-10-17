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

    @Query("select pi from ProcessInstanceEntity pi where pi.mainInstId is null")
    List<ProcessInstanceEntity> findMainInstICanSee();

    @Query("select pi from ProcessInstanceEntity pi " +
            "where 1=1 " +
            "and (:instId is null or pi.instId = :instId )" +
            "and (:defId is null or pi.defId like CONCAT('%',:defId,'%')) " +
            "and (:status is null or pi.status = :status )" +
            "and (:eventHandler is null or pi.eventHandler = :eventHandler )" +
            "and (:name is null or pi.name like CONCAT('%',:name,'%') )" +
            "and (:startedDate is null or pi.startedDate >= :startedDate )" +
            "and (:finishedDate is null or pi.finishedDate <= :finishedDate )")
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

