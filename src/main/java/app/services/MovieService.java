package app.services;

import app.dto.MovieDTO;
import app.dto.MovieDetailsDTO;
import app.entities.Movie;

import java.util.List;

public interface MovieService
{
    List<Movie> getByGenre(String genre);

    List<Movie> getByTitle(String query);

    List<Movie> getTopRated();

    List<Movie> getLowestRated();

    List<Movie> getMostPopular();

    Double getAvgRating();

    MovieDetailsDTO getMovieDetails(Long id);

    List<MovieDTO> getMovieIds();

    void fetchAndSaveToDB();
}