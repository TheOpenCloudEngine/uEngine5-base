package org.uengine.social;

import org.metaworks.ObjectInstance;
import org.metaworks.WebObjectType;
import org.metaworks.dwr.MetaworksRemoteService;
import org.metaworks.multitenancy.persistence.AfterLoad;
import org.metaworks.multitenancy.persistence.BeforeSave;
import org.metaworks.multitenancy.persistence.MultitenantEntity;
import org.oce.garuda.multitenancy.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;


@Transactional
public class ProcessInstanceRepositoryBaseImpl<ProcessInstance, PK extends Serializable> extends
        SimpleJpaRepository<ProcessInstance, PK> implements ProcessInstanceRepositoryBase<ProcessInstance, PK> {

    private final EntityManager entityManager;
    private final JpaEntityInformation<ProcessInstance, ?> entityInformation;

    public ProcessInstanceRepositoryBaseImpl(final JpaEntityInformation<ProcessInstance, ?> entityInformation,
                                     final EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.entityInformation = entityInformation;
    }

    @Autowired
    private ApplicationContext context;

    @Override
    public <S extends ProcessInstance> S save(S entity) {

        S returnVal = super.save(entity);



        return returnVal;
    }


}