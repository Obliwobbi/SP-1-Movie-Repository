package app.services;

import app.MovieListDTO;
import app.daos.MovieDAO;
import app.dto.*;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import app.utils.APIReader;

import java.time.LocalDate;
import java.util.*;

public class MovieService
{
    private final APIReader apiReader;
    private final MovieDAO movieDAO;
    private final static String API_KEY = System.getenv("API_KEY");

    public MovieService(APIReader apiReader, MovieDAO movieDAO)
    {
        this.apiReader = apiReader;
        this.movieDAO = movieDAO;
    }

    public MovieDetailsDTO getMovieDetails(Long id)
    {
        String endpointDetails = "https://api.themoviedb.org/3/movie/%d?append_to_response=credits&language=en-US&api_key=%s";
        String endpoint = String.format(Locale.US, endpointDetails, id, API_KEY);
        return apiReader.getAndConvertData(endpoint, MovieDetailsDTO.class);
    }

    public List<MovieDTO> getMovieIds()
    {
        String endpoint = "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&primary_release_date.gte=2020-01-08&primary_release_date.lte=2025-01-09&sort_by=primary_release_date.asc&with_origin_country=DK&with_original_language=da&api_key=%s";
        String formatted = String.format(Locale.US, endpoint, API_KEY);
        MovieListDTO listSize = apiReader.getAndConvertData(formatted, MovieListDTO.class);

        int pages = listSize.getTotalPages();
        List<MovieDTO> results = new ArrayList<>();

        for (int i = 1; i <= pages; i++)
        {
            String pageEndpoint = formatted + "&page=" + i;
            results.addAll(apiReader.getAndConvertDataList(pageEndpoint, MovieDTO.class));
        }
        return results;
    }

}