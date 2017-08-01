package org.uengine.social;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface ProcessInstanceRepositoryBase<E, PK extends Serializable> extends
        JpaRepository<E, PK>, JpaSpecificationExecutor<E> {


}
