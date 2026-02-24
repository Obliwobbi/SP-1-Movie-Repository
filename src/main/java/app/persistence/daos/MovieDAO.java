package app.persistence.daos;

import app.persistence.daos.interfaces.IMovieDAO;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import app.exceptions.DatabaseErrorType;
import app.exceptions.DatabaseException;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieDAO extends DAO<Movie> implements IMovieDAO
{
    public MovieDAO(EntityManagerFactory emf)
    {
        super(emf, Movie.class);
    }

    public Movie createAndMerge(Movie movie)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            try
            {
                Set<Genre> resolvedGenres = new HashSet<>();
                for (Genre genre : movie.getGenres())
                {
                    resolvedGenres.add(findOrPersistOnApiId(em, Genre.class, genre.getApiId(), genre));
                }

                Set<Actor> resolvedActors = new HashSet<>();
                for (Actor actor : movie.getActors())
                {
                    resolvedActors.add(findOrPersistOnApiId(em, Actor.class, actor.getApiId(), actor));
                }

                if (movie.getDirector() != null)
                {
                    Director d = movie.getDirector();
                    Director resolved = findOrPersistOnApiId(em, Director.class, d.getApiId(), d);
                    movie.setDirector(resolved);
                }

                movie.setActors(resolvedActors);
                movie.setGenres(resolvedGenres);

                em.persist(movie);
                em.getTransaction().commit();
                return movie;
            }
            catch (PersistenceException e)
            {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new DatabaseException("Create Movie failed", DatabaseErrorType.TRANSACTION_FAILURE, e);
            }
            catch (RuntimeException e)
            {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new DatabaseException("Create Movie failed", DatabaseErrorType.UNKNOWN, e);
            }
        }
    }

    private <T> T findOrPersistOnApiId(EntityManager em, Class<T> clazz, Long id, T newEntity)
    {
        try
        {
            return em.createQuery("SELECT e FROM " + clazz.getSimpleName() + " e WHERE e." + "apiId" + " = :id", clazz)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElseGet(() ->
                    {
                        em.persist(newEntity);
                        return newEntity;
                    });
        }
        catch (PersistenceException e)
        {
            throw new DatabaseException("FindOrPersist " + clazz.getSimpleName() + " failed", DatabaseErrorType.TRANSACTION_FAILURE, e);
        }
        catch (RuntimeException e)
        {
            throw new DatabaseException("FindOrPersist " + clazz.getSimpleName() + " failed", DatabaseErrorType.UNKNOWN, e);
        }
    }

    @Override
    public List<Movie> getByGenre(String genre)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m JOIN m.genres g WHERE g.name ILIKE :genre", Movie.class)
                    .setParameter("genre", "%" + genre + "%");
            return query.getResultList();
        }
        catch (PersistenceException e)
        {
            throw new DatabaseException("Get movies by genre failed", DatabaseErrorType.QUERY_FAILURE, e);
        }
    }

    @Override
    public List<Movie> getByTitle(String stringQuery)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE m.title ILIKE :stringQuery", Movie.class)
                    .setParameter("stringQuery", "%" + stringQuery + "%");
            return query.getResultList();
        }
        catch (PersistenceException e)
        {
            throw new DatabaseException("Get movies by genre failed", DatabaseErrorType.QUERY_FAILURE, e);
        }
    }

    @Override
    public double getAverageRating()
    {
        try (EntityManager em = emf.createEntityManager())
        {
            TypedQuery<Double> query = em.createQuery("SELECT AVG(m.rating) FROM Movie m", Double.class);
            return query.getSingleResult();
        }
    }

    @Override
    public List<Movie> getTopRated(int limit)
    {
        if (limit <= 0)
        {
            throw new IllegalArgumentException("Input needs to be bigger than 0");
        }
        try (EntityManager em = emf.createEntityManager())
        {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m ORDER BY m.rating DESC", Movie.class)
                    .setMaxResults(limit);
            return query.getResultList();
        }
    }

    @Override
    public List<Movie> getLowestRated(int limit)
    {
        if (limit <= 0)
        {
            throw new IllegalArgumentException("Input needs to be bigger than 0");
        }
        try (EntityManager em = emf.createEntityManager())
        {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m ORDER BY m.rating ASC", Movie.class)
                    .setMaxResults(limit);
            return query.getResultList();
        }
    }

    @Override
    public List<Movie> getMostPopular(int limit)
    {
        if (limit <= 0)
        {
            throw new IllegalArgumentException("Input needs to be bigger than 0");
        }
        try (EntityManager em = emf.createEntityManager())
        {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m ORDER BY m.popularity DESC", Movie.class)
                    .setMaxResults(limit);
            return query.getResultList();
        }
    }
}