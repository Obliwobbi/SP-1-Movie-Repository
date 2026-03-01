package app.persistence.daos.interfaces;

import app.entities.Movie;
import app.entities.MovieActor;

import java.util.List;

public interface IMovieDAO
{
    Movie createAndMerge(Movie movie, List<MovieActor> movieActors);

    List<Movie> getByGenre(String genre);

    List<Movie> getByTitle(String query);

    double getAverageRating();

    List<Movie> getTopRated(int limit);

    List<Movie> getLowestRated(int limit);

    List<Movie> getMostPopular(int limit);

    List<Long> getAllApiIds();

    void deleteByApiId(Long apiId);
}
