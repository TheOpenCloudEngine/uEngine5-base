package org.uengine.social.common.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Map;


@Transactional
public class PersistentRepositoryImpl<E, PK extends Serializable> extends
        SimpleJpaRepository<E, PK> implements PersistentRepository<E, PK> {

    @PersistenceContext
    private EntityManager em;

    public PersistentRepositoryImpl(JpaEntityInformation<E, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public PersistentRepositoryImpl(Class<E> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    @Override
    public void setRepositoryMethodMetadata(CrudMethodMetadata crudMethodMetadata) {
        super.setRepositoryMethodMetadata(crudMethodMetadata);
    }

    @Override
    protected CrudMethodMetadata getRepositoryMethodMetadata() {
        return super.getRepositoryMethodMetadata();
    }

    @Override
    protected Class<E> getDomainClass() {
        return super.getDomainClass();
    }

    @Override
    public <S extends E> S save(S entity) {

        if(entity instanceof BeforeSave){
            ((BeforeSave)entity).beforeSave();
        }
        return super.save(entity);
    }

    @Override
    public void delete(PK pk) {
        super.delete(pk);
    }

    @Override
    public void delete(E entity) {
        super.delete(entity);
    }

    @Override
    public void delete(Iterable<? extends E> entities) {
        super.delete(entities);
    }

    @Override
    public void deleteInBatch(Iterable<E> entities) {
        super.deleteInBatch(entities);
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
    }

    @Override
    public void deleteAllInBatch() {
        super.deleteAllInBatch();
    }

    @Override
    public E findOne(PK pk) {
        return super.findOne(pk);
    }

    @Override
    protected Map<String, Object> getQueryHints() {
        return super.getQueryHints();
    }

    @Override
    public E getOne(PK pk) {
        return super.getOne(pk);
    }

    @Override
    public boolean exists(PK pk) {
        return super.exists(pk);
    }

    @Override
    public List<E> findAll() {
        return super.findAll();
    }

    @Override
    public List<E> findAll(Iterable<PK> pks) {
        return super.findAll(pks);
    }

    @Override
    public List<E> findAll(Sort sort) {
        return super.findAll(sort);
    }

    @Override
    public Page<E> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @Override
    public E findOne(Specification<E> spec) {
        return super.findOne(spec);
    }

    @Override
    public List<E> findAll(Specification<E> spec) {
        return super.findAll(spec);
    }

    @Override
    public Page<E> findAll(Specification<E> spec, Pageable pageable) {
        return super.findAll(spec, pageable);
    }

    @Override
    public List<E> findAll(Specification<E> spec, Sort sort) {
        return super.findAll(spec, sort);
    }

    @Override
    public long count() {
        return super.count();
    }

    @Override
    public long count(Specification<E> spec) {
        return super.count(spec);
    }

    @Override
    public void flush() {
        super.flush();
    }

    @Override
    protected Page<E> readPage(TypedQuery<E> query, Pageable pageable, Specification<E> spec) {
        return super.readPage(query, pageable, spec);
    }

    @Override
    protected TypedQuery<E> getQuery(Specification<E> spec, Pageable pageable) {
        return super.getQuery(spec, pageable);
    }


    @Override
    protected TypedQuery<E> getQuery(Specification<E> spec, Sort sort) {
        return super.getQuery(spec, sort);
    }

    @Override
    protected TypedQuery<Long> getCountQuery(Specification<E> spec) {
        return super.getCountQuery(spec);
    }
}