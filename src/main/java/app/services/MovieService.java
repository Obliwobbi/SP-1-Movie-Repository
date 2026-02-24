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

    public void saveMovieData()
    {
        List<MovieDTO> results = getMovieIds();
        int i = 0;
        for (MovieDTO m : results)
        {
            {
                MovieDetailsDTO details = getMovieDetails(m.getId());

                Long apiId = details.getId();
                String title = details.getTitle();
                String description = details.getOverview();
                LocalDate releaseDate = details.getReleaseDate();
                double rating = details.getRating();
                double popularity = details.getPopularity();

                CrewDTO directorOne = details.getCredits().getCrewDTOList().stream().filter(c -> c.getJob().equals("Director")).findFirst().orElse(null);

                Director director;
                if (directorOne != null)
                {
                    director = new Director(directorOne.getId(), directorOne.getName());
                }
                else
                {
                    director = new Director((long) -1, "No Director Credited");
                }

                List<GenreDTO> genres = details.getGenres();
                Set<Genre> genreSet = new HashSet<>();

                for (GenreDTO g : genres)
                {
                    Genre g1 = new Genre(g.getId(), g.getName());
                    genreSet.add(g1);
                }

                List<ActorDTO> actors = details.getCredits().getCastDTOList();
                Set<Actor> actorSet = new HashSet<>();

                for (ActorDTO a : actors)
                {
                    actorSet.add(Actor.builder()
                            .apiId(a.getId())
                            .name(a.getName())
                            .build());
                }

                Movie movie = new Movie(apiId, title, director, rating, releaseDate, popularity, genreSet, actorSet);

                movieDAO.createAndMerge(movie);
                System.out.println(i);
                i++;
            }
        }
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