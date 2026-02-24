package app.persistence.daos.interfaces;

import app.entities.Movie;

import java.util.List;

public interface IMovieDAO
{
    List<Movie> getByGenre(String genre);
    List<Movie> getByTitle(String query);
    double getAverageRating();
    List<Movie> getTopRated(int limit);
    List<Movie> getLowestRated(int limit);
    List<Movie> getMostPopular(int limit);
}
