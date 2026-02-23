package app;

import app.dto.MovieDTO;
import app.services.MovieService;
import app.utils.APIReader;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        String movieList5years = "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&primary_release_date.gte=2020-01-08&primary_release_date.lte=2025-01-09&sort_by=primary_release_date.asc&with_origin_country=DK&api_key=%s";

        String movieDetailsWithCredits = "https://api.themoviedb.org/3/movie/%d?append_to_response=credits&language=en-US&api_key=%s"; // FORMAT id AND API-KEY

        String endpoint = "https://api.themoviedb.org/3/movie/%d?append_to_response=credits&language=en-US&api_key=";


        ObjectMapper objectMapper = new ObjectMapper();

        APIReader apiReader = new APIReader(objectMapper);

        MovieService ms = new MovieService(apiReader);

        List<MovieDTO> results = ms.getMovieIds();

        System.out.println(results.size());

        System.out.println(ms.getMovieDetails(results.get(1).getId()));

//        public MovieDTO getMovieById(int id)
//        {
//            String endpoint = String.format(Locale.US, ENDPOINT, id);
//            return apiReader.getAndConvertData(endpoint, MovieDTO.class);
//        }

    }
}