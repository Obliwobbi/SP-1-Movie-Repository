package app;

import app.persistence.config.HibernateConfig;
import app.persistence.daos.DAO;
import app.persistence.daos.MovieDAO;
import app.entities.Movie;
import app.persistence.daos.interfaces.IMovieDAO;
import app.services.MovieService;
import app.services.MovieServiceImpl;
import app.utils.APIReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class Main
{
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public static void main(String[] args)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        APIReader apiReader = new APIReader(objectMapper);

        IMovieDAO movieDAO = new MovieDAO(emf);
        MovieService ms = new MovieServiceImpl(apiReader, movieDAO);

//        ms.fetchAndSaveToDB();

        System.out.println("Top Ten");
        List<Movie> topTenRated = ms.getTopRated();
        topTenRated.forEach(movie -> System.out.println(movie.getTitle()));
        System.out.println();

        System.out.println("Lowest ten");
        List<Movie> lowestTenRated = ms.getLowestRated();
        lowestTenRated.forEach(movie -> System.out.println(movie.getTitle()));
        System.out.println();

        System.out.println("Most popular");
        List<Movie> mostPopular = ms.getMostPopular();
        mostPopular.forEach(movie -> System.out.println(movie.getTitle()));
        System.out.println();

        System.out.println("Average rating: "+ms.getAvgRating());
        System.out.println();

        System.out.println("Get by genre: action");
        List<Movie> byGenreAction = ms.getByGenre("action");
        byGenreAction.forEach(movie -> System.out.println(movie.getTitle()));
        System.out.println();

        System.out.println("Get by title: thomas");
        List<Movie> byTitleThomas = ms.getByTitle("thomas");
        byTitleThomas.forEach(movie -> System.out.println(movie.getTitle()));
        System.out.println();

    }
}