package org.uengine.social.service.rolemapping;


import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.uengine.social.common.repository.PersistentRepository;
import org.uengine.social.service.worklist.WorkList;

import java.util.List;

/**
 * Created by uengine on 2017. 6. 19..
 */
@RepositoryRestResource(collectionResourceRel = "rolemapping", path = "rolemapping")
public interface RoleMappingRepository extends PersistentRepository<RoleMapping, Long> {

}

