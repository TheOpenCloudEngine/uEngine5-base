package org.uengine.social.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface PersistentRepository<E, PK extends Serializable> extends JpaRepository<E, PK>, JpaSpecificationExecutor<E> {
}
