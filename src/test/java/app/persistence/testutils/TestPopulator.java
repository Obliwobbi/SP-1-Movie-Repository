package app.persistence.testutils;

import app.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;

import java.time.LocalDate;
import java.util.*;

public class TestPopulator
{
    public static Map<String, Movie> populateMovies(EntityManagerFactory emf)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();

            try
            {
                em.createNativeQuery("TRUNCATE TABLE movie_actors RESTART IDENTITY CASCADE").executeUpdate();
                em.createNativeQuery("TRUNCATE TABLE movie_genre RESTART IDENTITY CASCADE").executeUpdate();
                em.createNativeQuery("TRUNCATE TABLE movies RESTART IDENTITY CASCADE").executeUpdate();
                em.createNativeQuery("TRUNCATE TABLE actors RESTART IDENTITY CASCADE").executeUpdate();
                em.createNativeQuery("TRUNCATE TABLE directors RESTART IDENTITY CASCADE").executeUpdate();
                em.createNativeQuery("TRUNCATE TABLE genres RESTART IDENTITY CASCADE").executeUpdate();

                Genre action = new Genre(28L, "Action");
                Genre drama = new Genre(18L, "Drama");
                Genre sciFi = new Genre(878L, "Science Fiction");
                Genre thriller = new Genre(53L, "Thriller");

                em.persist(action);
                em.persist(drama);
                em.persist(sciFi);
                em.persist(thriller);

                Director nolan = new Director(525L, "Christopher Nolan");
                Director fincher = new Director(7467L, "David Fincher");

                em.persist(nolan);
                em.persist(fincher);

                Actor diCaprio = Actor.builder().apiId(6193L).name("Leonardo DiCaprio").build();
                Actor pitt = Actor.builder().apiId(287L).name("Brad Pitt").build();
                Actor murphy = Actor.builder().apiId(1176032L).name("Cillian Murphy").build();

                em.persist(diCaprio);
                em.persist(pitt);
                em.persist(murphy);

                em.flush();

                Movie inception = new Movie(27205L, "Inception", nolan, 8.8, LocalDate.of(2010, 7, 16), 87.5, new HashSet<>(Arrays.asList(action, sciFi)), new HashSet<>());
                Movie darkKnight = new Movie(155L, "The Dark Knight", nolan, 9.0, LocalDate.of(2008, 7, 18), 95.0, new HashSet<>(Arrays.asList(action, thriller)), new HashSet<>());
                Movie fightClub = new Movie(550L, "Fight Club", fincher, 8.8, LocalDate.of(1999, 10, 15), 76.3, new HashSet<>(Arrays.asList(drama, thriller)), new HashSet<>());
                Movie lowRated = new Movie(99999L, "Flop Movie", fincher, 2.5, LocalDate.of(2022, 1, 1), 5.0, new HashSet<>(Collections.singletonList(drama)), new HashSet<>());

                em.persist(inception);
                em.persist(darkKnight);
                em.persist(fightClub);
                em.persist(lowRated);

                MovieActor inceptionDiCaprio = MovieActor.builder().movie(inception).actor(diCaprio).character("Dom Cobb").build();
                MovieActor inceptionMurphy = MovieActor.builder().movie(inception).actor(murphy).character("Robert Fischer").build();
                MovieActor darkKnightMurphy = MovieActor.builder().movie(darkKnight).actor(murphy).character("Jonathan Crane").build();
                MovieActor fightClubPitt = MovieActor.builder().movie(fightClub).actor(pitt).character("Tyler Durden").build();

                em.persist(inceptionDiCaprio);
                em.persist(inceptionMurphy);
                em.persist(darkKnightMurphy);
                em.persist(fightClubPitt);

                em.flush();
            }
            catch (PersistenceException e)
            {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            }

            em.getTransaction().commit();

            // Re-fetch managed instances with IDs assigned
            Map<String, Movie> seeded = new LinkedHashMap<>();
            try (EntityManager em2 = emf.createEntityManager())
            {
                seeded.put("inception",   em2.createQuery("SELECT m FROM Movie m WHERE m.apiId = 27205", Movie.class).getSingleResult());
                seeded.put("darkKnight",  em2.createQuery("SELECT m FROM Movie m WHERE m.apiId = 155",   Movie.class).getSingleResult());
                seeded.put("fightClub",   em2.createQuery("SELECT m FROM Movie m WHERE m.apiId = 550",   Movie.class).getSingleResult());
                seeded.put("lowRated",    em2.createQuery("SELECT m FROM Movie m WHERE m.apiId = 99999", Movie.class).getSingleResult());
            }

            return seeded;
        }
    }
}