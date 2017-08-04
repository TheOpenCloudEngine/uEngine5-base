package org.uengine.social.service.worklist;


import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.uengine.social.common.repository.PersistentRepository;

import java.util.List;

/**
 * Created by uengine on 2017. 6. 19..
 */
@RepositoryRestResource(collectionResourceRel = "worklist", path = "worklist")
public interface WorkListRepository extends PersistentRepository<WorkList, Long> {

    public List<WorkList> findByInstId(@Param("instId") Long instId);


}

