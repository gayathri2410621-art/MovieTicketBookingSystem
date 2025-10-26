package com.example.movieticketbookingsystem.dao;

import com.example.movieticketbookingsystem.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class GenericDaoImpl<T, ID> implements GenericDao<T, ID> {

    private Class<T> entityClass;

    public GenericDaoImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected void executeInsideTransaction(Consumer<EntityManager> action) {
        EntityManager entityManager = null;
        EntityTransaction tx = null;
        try {
            entityManager = HibernateUtil.getEntityManager();
            tx = entityManager.getTransaction();
            tx.begin();
            action.accept(entityManager);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void save(T entity) {
        executeInsideTransaction(entityManager -> entityManager.persist(entity));
    }

    @Override
    public void update(T entity) {
        executeInsideTransaction(entityManager -> entityManager.merge(entity));
    }

    @Override
    public void delete(T entity) {
        executeInsideTransaction(entityManager ->
                entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity))
        );
    }

    @Override
    public Optional<T> findById(ID id) {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManager();
            T entity = entityManager.find(entityClass, id);
            return Optional.ofNullable(entity);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public List<T> findAll() {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManager();
            return entityManager.createQuery("FROM " + entityClass.getName(), entityClass).getResultList();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
