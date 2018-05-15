package org.uengine.five.repository;

import org.metaworks.multitenancy.persistence.MultitenantRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.uengine.five.entity.AuditEntity;

/**
 * Created by uengine on 2017. 12. 21..
 */

@RepositoryRestResource(collectionResourceRel = "audits", path = "audit")
public interface AuditEntityRepository extends MultitenantRepository<AuditEntity, Long> {
}
