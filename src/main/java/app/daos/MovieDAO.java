package app.daos;

import app.daos.interfaces.IDAO;
import app.daos.interfaces.IMovieDAO;
import app.entities.Movie;
import app.exceptions.DatabaseErrorType;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class MovieDAO implements IDAO<Movie>, IMovieDAO
{
    private final EntityManagerFactory emf;

    public MovieDAO(EntityManagerFactory emf){this.emf = emf;}
    @Override
    public Movie create(Movie user)
    {
        if (user == null)
        {
            throw new IllegalArgumentException("Movie cant be null");
        }

        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();

            try
            {
                em.persist(user);
                em.getTransaction().commit();
                return user;
            }
            catch (PersistenceException e)
            {
                if (em.getTransaction().isActive())
                {
                    em.getTransaction().rollback();
                }
                throw new DatabaseException("Create Movie failed", DatabaseErrorType.TRANSACTION_FAILURE, e);
            }
            catch (RuntimeException e)
            {
                if (em.getTransaction().isActive())
                {
                    em.getTransaction().rollback();
                }
                throw new DatabaseException("Create Movie failed", DatabaseErrorType.UNKNOWN, e);
            }
        }
    }

    @Override
    public Movie get(Integer id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("Movie id is required");
        }

        try (EntityManager em = emf.createEntityManager())
        {
            Movie user = em.find(Movie.class, id);
            if (user != null)
            {
                return user;
            }
            throw new DatabaseException("Movie not found", DatabaseErrorType.NOT_FOUND);
        }
        catch (PersistenceException e)
        {
            throw new DatabaseException("Get Movie failed", DatabaseErrorType.QUERY_FAILURE, e);
        }
    }

    @Override
    public List<Movie> getAll()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m", Movie.class);
            return query.getResultList();
        }
        catch (PersistenceException e)
        {
            throw new DatabaseException("Get movies failed", DatabaseErrorType.QUERY_FAILURE, e);
        }
    }


    @Override
    public Movie update(Movie m)
    {
        if (m == null || m.getDbId() == null)
        {
            throw new IllegalArgumentException("Movie and movie id are required");
        }

        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();

            try
            {
                Movie managed = em.find(Movie.class, m.getDbId());
                if (managed == null)
                {
                    if (em.getTransaction().isActive())
                    {
                        em.getTransaction().rollback();
                    }
                    throw new DatabaseException("User not found or invalid", DatabaseErrorType.NOT_FOUND);
                }

                managed = em.merge(m);
                em.getTransaction().commit();
                return managed;
            }
            catch (DatabaseException e)
            {
                throw e;
            }
            catch (PersistenceException e)
            {
                if (em.getTransaction().isActive())
                {
                    em.getTransaction().rollback();
                }
                throw new DatabaseException("Update User failed", DatabaseErrorType.TRANSACTION_FAILURE, e);
            }
            catch (RuntimeException e)
            {
                if (em.getTransaction().isActive())
                {
                    em.getTransaction().rollback();
                }
                throw new DatabaseException("Update User failed", DatabaseErrorType.UNKNOWN, e);
            }
        }
    }
}
