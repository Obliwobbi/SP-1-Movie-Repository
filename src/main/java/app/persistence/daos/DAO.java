package app.persistence.daos;

import app.persistence.daos.interfaces.IDAO;
import app.exceptions.DatabaseErrorType;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;

import java.util.List;

public class DAO<T> implements IDAO<T>
{
    protected final EntityManagerFactory emf;
    private final Class<T> entityClass;

    public DAO(EntityManagerFactory emf, Class<T> entityClass)
    {
        this.emf = emf;
        this.entityClass = entityClass;
    }

    @Override
    public T create(T entity)
    {
        if (entity == null)
        {
            throw new IllegalArgumentException(entityClass.getSimpleName() + " cannot be null");
        }

        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            try
            {
                em.persist(entity);
                em.getTransaction().commit();
                return entity;
            }
            catch (PersistenceException e)
            {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new DatabaseException("Create " + entityClass.getSimpleName() + " failed", DatabaseErrorType.TRANSACTION_FAILURE, e);
            }
            catch (RuntimeException e)
            {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new DatabaseException("Create " + entityClass.getSimpleName() + " failed", DatabaseErrorType.UNKNOWN, e);
            }
        }
    }

    @Override
    public T get(Integer id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException(entityClass.getSimpleName() + " id is required");
        }

        try (EntityManager em = emf.createEntityManager())
        {
            T entity = em.find(entityClass, id);
            if (entity != null)
            {
                return entity;
            }
            throw new DatabaseException(entityClass.getSimpleName() + " not found", DatabaseErrorType.NOT_FOUND);
        }
        catch (PersistenceException e)
        {
            throw new DatabaseException("Get " + entityClass.getSimpleName() + " failed", DatabaseErrorType.QUERY_FAILURE, e);
        }
    }

    @Override
    public List<T> getAll()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                     .getResultList();
        }
        catch (PersistenceException e)
        {
            throw new DatabaseException("GetAll " + entityClass.getSimpleName() + " failed", DatabaseErrorType.QUERY_FAILURE, e);
        }
    }

    @Override
    public T update(T entity)
    {
        if (entity == null)
        {
            throw new IllegalArgumentException(entityClass.getSimpleName() + " cannot be null");
        }

        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            try
            {
                T merged = em.merge(entity);
                em.getTransaction().commit();
                return merged;
            }
            catch (PersistenceException e)
            {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new DatabaseException("Update " + entityClass.getSimpleName() + " failed", DatabaseErrorType.TRANSACTION_FAILURE, e);
            }
            catch (RuntimeException e)
            {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new DatabaseException("Update " + entityClass.getSimpleName() + " failed", DatabaseErrorType.UNKNOWN, e);
            }
        }
    }

    @Override
    public void delete(Integer id)
    {
        T entity = get(id);
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            try
            {
                em.remove(em.merge(entity));
                em.getTransaction().commit();
            }
            catch (PersistenceException e)
            {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new DatabaseException("Delete " + entityClass.getSimpleName() + " failed", DatabaseErrorType.TRANSACTION_FAILURE, e);
            }
            catch (RuntimeException e)
            {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new DatabaseException("Delete " + entityClass.getSimpleName() + " failed", DatabaseErrorType.UNKNOWN, e);
            }
        }
    }
}
