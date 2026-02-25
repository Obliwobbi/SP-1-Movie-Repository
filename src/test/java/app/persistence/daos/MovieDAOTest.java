package app.persistence.daos;

import app.config.HibernateTestConfig;
import app.entities.*;
import app.persistence.testutils.TestPopulator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MovieDAOTest
{
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private MovieDAO movieDAO;
    private Map<String, Movie> seeded;

    @BeforeEach
    void setUp()
    {
        seeded = TestPopulator.populateMovies(emf);
        movieDAO = new MovieDAO(emf);
    }

    @AfterAll
    void tearDown()
    {
        emf.close();
    }

    // ─── createAndMerge ────────────────────────────────────────────────────────

    @Test
    @DisplayName("CreateAndMerge - should persist movie, director, genres and actors in one call")
    void createAndMerge_persistsAllRelatedEntities()
    {
        Genre existingAction = fetchGenreByApiId(28L);   // already in DB from seeded data

        Director newDirector = new Director(99L, "New Director");
        Movie newMovie = new Movie(
                77777L,
                "New Film", newDirector,
                7.0,
                LocalDate.of(2023, 6, 1), 60.0,
                new HashSet<>(Collections.singletonList(existingAction)),
                new HashSet<>());

        Actor newActor = Actor.builder().apiId(9900L).name("New Actor").build();
        MovieActor movieActor = MovieActor.builder().movie(newMovie).actor(newActor).character("Hero").build();

        Movie saved = movieDAO.createAndMerge(newMovie, List.of(movieActor));

        assertThat(saved.getDbId(), notNullValue());
        assertThat(saved.getTitle(), is("New Film"));
        assertThat(saved.getDirector().getName(), is("New Director"));
    }

    @Test
    @DisplayName("CreateAndMerge - should reuse an existing genre instead of creating a duplicate")
    void createAndMerge_reusesExistingGenreByApiId()
    {
        long genreCountBefore = countTable(Genre.class);
        Genre existingAction = fetchGenreByApiId(28L);

        Director director = new Director(
                1000L,
                "Some Director");

        Movie movie = new Movie(
                88888L,
                "Genre Reuse Film",
                director, 6.0,
                LocalDate.of(2023, 1, 1), 40.0,
                new HashSet<>(Collections.singletonList(existingAction)),
                new HashSet<>());

        movieDAO.createAndMerge(movie, List.of());

        long genreCountAfter = countTable(Genre.class);
        assertThat("No new genre row should have been created", genreCountAfter, is(genreCountBefore));
    }

    @Test
    @DisplayName("CreateAndMerge - should reuse an existing director instead of creating a duplicate")
    void createAndMerge_reusesExistingDirectorByApiId()
    {
        long directorCountBefore = countTable(Director.class);
        Director existingNolan = new Director(525L, "Christopher Nolan"); // apiId already seeded

        Movie movie = new Movie(
                88889L,
                "Director Reuse Film",
                existingNolan,
                7.0,
                LocalDate.of(2021, 5, 1), 50.0,
                new HashSet<>(), new HashSet<>());

        movieDAO.createAndMerge(movie, List.of());

        long directorCountAfter = countTable(Director.class);
        assertThat("No new director row should have been created", directorCountAfter, is(directorCountBefore));
    }

    @Test
    @DisplayName("CreateAndMerge - should reuse existing actor instead of creating a duplicate")
    void createAndMerge_reusesExistingActorByApiId()
    {
        long actorCountBefore = countTable(Actor.class);

        Director director = new Director(2000L, "Another Director");
        Movie movie = new Movie(88890L,
                "Actor Reuse Film",
                director,
                5.5,
                LocalDate.of(2020, 3, 10), 33.0,
                new HashSet<>(), new HashSet<>());

        Actor existingDiCaprio = Actor.builder().apiId(6193L).name("Leonardo DiCaprio").build(); // already seeded

        MovieActor ma = MovieActor.builder()
                .movie(movie)
                .actor(existingDiCaprio)
                .character("Some role")
                .build();

        movieDAO.createAndMerge(movie, List.of(ma));

        long actorCountAfter = countTable(Actor.class);
        assertThat("No new actor row should have been created", actorCountAfter, is(actorCountBefore));
    }

    // ─── getByGenre ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GetByGenre - should return all movies that belong to the given genre")
    void getByGenre_returnsMoviesMatchingGenre()
    {
        List<Movie> actionMovies = movieDAO.getByGenre("Action");

        assertThat(actionMovies, notNullValue());
        assertThat(actionMovies.size(), is(2)); // inception + darkKnight

        List<String> titles = actionMovies.stream().map(Movie::getTitle).toList();
        assertThat(titles, hasItem("Inception"));
        assertThat(titles, hasItem("The Dark Knight"));
        assertThat(titles, not(hasItem("Fight Club")));
    }

    @Test
    @DisplayName("GetByGenre - should return empty list when no movies match the genre")
    void getByGenre_returnsEmptyListForUnknownGenre()
    {
        List<Movie> result = movieDAO.getByGenre("NonExistentGenre");

        assertThat(result, notNullValue());
        assertThat(result, empty());
    }

    @Test
    @DisplayName("GetByGenre - should do a partial/case-insensitive match")
    void getByGenre_isCaseInsensitiveAndPartial()
    {
        List<Movie> result = movieDAO.getByGenre("action");  // lowercase

        assertThat(result, not(empty()));
        result.forEach(m ->
                assertThat(m.getGenres()
                        .stream()
                        .anyMatch(g -> g.getName()
                                .equalsIgnoreCase("Action")), is(true))
        );
    }

    // ─── getByTitle ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GetByTitle - should return movies whose title contains the search string")
    void getByTitle_returnsMoviesWithMatchingTitle()
    {
        List<Movie> result = movieDAO.getByTitle("Dark");

        assertThat(result, notNullValue());
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getTitle(), is("The Dark Knight"));
    }

    @Test
    @DisplayName("GetByTitle - should be case-insensitive")
    void getByTitle_isCaseInsensitive()
    {
        List<Movie> result = movieDAO.getByTitle("inception");

        assertThat(result, not(empty()));
        assertThat(result.get(0).getTitle(), is("Inception"));
    }

    @Test
    @DisplayName("GetByTitle - should return empty list when no movie matches")
    void getByTitle_returnsEmptyListForNoMatch()
    {
        List<Movie> result = movieDAO.getByTitle("ZZZNoSuchFilm");

        assertThat(result, notNullValue());
        assertThat(result, empty());
    }

    // ─── getAverageRating ──────────────────────────────────────────────────────

    @Test
    @DisplayName("GetAverageRating - should return the mean rating across all movies")
    void getAverageRating_returnsCorrectMean()
    {
        // seeded: 8.8, 9.0, 8.8, 2.5  →  avg = 29.1 / 4 = 7.275
        double expected = (8.8 + 9.0 + 8.8 + 2.5) / 4.0;

        double avg = movieDAO.getAverageRating();

        assertThat(avg, closeTo(expected, 0.01));
    }

    @Test
    @DisplayName("GetAverageRating - result should always be between 0 and 10")
    void getAverageRating_isBetweenZeroAndTen()
    {
        double avg = movieDAO.getAverageRating();

        assertThat(avg, greaterThanOrEqualTo(0.0));
        assertThat(avg, lessThanOrEqualTo(10.0));
    }

    // ─── getTopRated ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("GetTopRated - should return the N highest-rated movies in descending order")
    void getTopRated_returnsCorrectNumberInDescendingOrder()
    {
        List<Movie> top = movieDAO.getTopRated(2);

        assertThat(top, hasSize(2));
        assertThat(top.get(0).getRating(), greaterThanOrEqualTo(top.get(1).getRating()));
        assertThat(top.get(0).getTitle(), is("The Dark Knight")); // 9.0
    }

    @Test
    @DisplayName("GetTopRated - should throw IllegalArgumentException for limit <= 0")
    void getTopRated_throwsOnInvalidLimit()
    {
        assertThrows(IllegalArgumentException.class, () -> movieDAO.getTopRated(0));
        assertThrows(IllegalArgumentException.class, () -> movieDAO.getTopRated(-5));
    }

    @Test
    @DisplayName("GetTopRated - result should not exceed the requested limit")
    void getTopRated_doesNotExceedLimit()
    {
        List<Movie> top = movieDAO.getTopRated(1);

        assertThat(top, hasSize(1));
    }

    // ─── getLowestRated ────────────────────────────────────────────────────────

    @Test
    @DisplayName("GetLowestRated - should return the N lowest-rated movies in ascending order")
    void getLowestRated_returnsCorrectNumberInAscendingOrder()
    {
        List<Movie> lowest = movieDAO.getLowestRated(2);

        assertThat(lowest, hasSize(2));
        assertThat(lowest.get(0).getRating(), lessThanOrEqualTo(lowest.get(1).getRating()));
        assertThat(lowest.get(0).getTitle(), is("Flop Movie")); // 2.5
    }

    @Test
    @DisplayName("GetLowestRated - should throw IllegalArgumentException for limit <= 0")
    void getLowestRated_throwsOnInvalidLimit()
    {
        assertThrows(IllegalArgumentException.class, () -> movieDAO.getLowestRated(0));
        assertThrows(IllegalArgumentException.class, () -> movieDAO.getLowestRated(-1));
    }

    @Test
    @DisplayName("GetLowestRated - result should not exceed the requested limit")
    void getLowestRated_doesNotExceedLimit()
    {
        List<Movie> lowest = movieDAO.getLowestRated(1);

        assertThat(lowest, hasSize(1));
    }

    // ─── getMostPopular ────────────────────────────────────────────────────────

    @Test
    @DisplayName("GetMostPopular - should return the N most popular movies in descending popularity order")
    void getMostPopular_returnsCorrectNumberInDescendingOrder()
    {
        List<Movie> popular = movieDAO.getMostPopular(2);

        assertThat(popular, hasSize(2));
        assertThat(popular.get(0).getPopularity(), greaterThanOrEqualTo(popular.get(1).getPopularity()));
        assertThat(popular.get(0).getTitle(), is("The Dark Knight")); // 95.0
    }

    @Test
    @DisplayName("GetMostPopular - should throw IllegalArgumentException for limit <= 0")
    void getMostPopular_throwsOnInvalidLimit()
    {
        assertThrows(IllegalArgumentException.class, () -> movieDAO.getMostPopular(0));
        assertThrows(IllegalArgumentException.class, () -> movieDAO.getMostPopular(-3));
    }

    @Test
    @DisplayName("GetMostPopular - result should not exceed the requested limit")
    void getMostPopular_doesNotExceedLimit()
    {
        List<Movie> popular = movieDAO.getMostPopular(1);

        assertThat(popular, hasSize(1));
    }

    // ─── helpers ───────────────────────────────────────────────────────────────

    private Genre fetchGenreByApiId(long apiId)
    {
        try (var em = emf.createEntityManager())
        {
            return em.createQuery("SELECT g FROM Genre g WHERE g.apiId = :id", Genre.class)
                    .setParameter("id", apiId)
                    .getSingleResult();
        }
    }

    private <T> long countTable(Class<T> entityClass)
    {
        try (var em = emf.createEntityManager())
        {
            return em.createQuery("SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class)
                    .getSingleResult();
        }
    }
}