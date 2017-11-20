package org.uengine.five.repository;


import org.metaworks.multitenancy.persistence.MultitenantRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.uengine.five.entity.RoleMappingEntity;

/**
 * Created by uengine on 2017. 6. 19..
 */
@RepositoryRestResource(collectionResourceRel = "rolemapping", path = "rolemapping")
public interface RoleMappingRepository extends MultitenantRepository<RoleMappingEntity, Long> {

}

