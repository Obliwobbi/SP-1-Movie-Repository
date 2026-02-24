package app.daos;

import app.daos.interfaces.IMovieDAO;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
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

            Set<Genre> resolvedGenres = new HashSet<>();
            for (Genre genre : movie.getGenres())
            {
                resolvedGenres.add(findOrPersist(em, Genre.class, "apiId", genre.getApiId(), genre));
            }

            Set<Actor> resolvedActors = new HashSet<>();
            for (Actor actor : movie.getActors())
            {
                resolvedActors.add(findOrPersist(em, Actor.class, "apiId", actor.getApiId(), actor));
            }

            if (movie.getDirector() != null)
            {
                Director d = movie.getDirector();
                Director resolved = findOrPersist(em, Director.class, "apiId", d.getApiId(), d);
                movie.setDirector(resolved);
            }

            movie.setActors(resolvedActors);
            movie.setGenres(resolvedGenres);

            em.persist(movie);
            em.getTransaction().commit();
            return movie;
        }
    }

    private <T> T findOrPersist(EntityManager em, Class<T> clazz, String field, Object value, T newEntity)
    {
        return em.createQuery("SELECT e FROM " + clazz.getSimpleName() + " e WHERE e." + field + " = :val", clazz)
                .setParameter("val", value)
                .getResultStream()
                .findFirst()
                .orElseGet(() ->
                {
                    em.persist(newEntity);
                    return newEntity;
                });
    }
}