package app.services;

import app.dto.*;
import app.entities.*;
import app.persistence.daos.interfaces.IMovieDAO;
import app.utils.APIReader;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MovieServiceImpl implements MovieService
{
    private final APIReader apiReader;
    private final IMovieDAO movieDAO;
    private final static String API_KEY = System.getenv("API_KEY");

    public MovieServiceImpl(APIReader apiReader, IMovieDAO movieDAO)
    {
        this.apiReader = apiReader;
        this.movieDAO = movieDAO;
    }

    @Override
    public List<Movie> getByGenre(String genre)
    {
        return movieDAO.getByGenre(genre);
    }

    @Override
    public List<Movie> getByTitle(String query)
    {
        return movieDAO.getByTitle(query);
    }

    @Override
    public List<Movie> getTopRated()
    {
        return movieDAO.getTopRated(10);
    }

    @Override
    public List<Movie> getLowestRated()
    {
        return movieDAO.getLowestRated(10);
    }

    @Override
    public List<Movie> getMostPopular()
    {
        return movieDAO.getMostPopular(10);
    }

    @Override
    public Double getAvgRating()
    {
        return movieDAO.getAverageRating();
    }

    @Override
    public void fetchAndSaveToDB()
    {
        List<MovieDTO> movieIds = getMovieIds();
        int i = 0;
        for (MovieDTO m : movieIds)
        {
            {
                MovieDetailsDTO details = getMovieDetails(m.getId());

                Movie movie = movieDTOToEntity(details);
                List<MovieActor> movieActors = actorDTOToEntity(details, movie);

                movieDAO.createAndMerge(movie, movieActors);
                i++;
                System.out.println(i);
            }
        }
    }

    private List<MovieActor> actorDTOToEntity(MovieDetailsDTO details, Movie movie)
    {
        return details.getCredits()
                .getCastDTOList()
                .stream()
                .map(actorDTO ->
                {
                    Actor actor = new Actor();
                    actor.setApiId(actorDTO.getId());
                    actor.setName(actorDTO.getName());

                    MovieActor movieActor = new MovieActor();
                    movieActor.setMovie(movie);
                    movieActor.setActor(actor);
                    movieActor.setCharacter(actorDTO.getCharacter());
                    return movieActor;
                }).toList();
    }

    @Override
    public MovieDetailsDTO getMovieDetails(Long id)
    {
        String endpointDetails = "https://api.themoviedb.org/3/movie/%d?append_to_response=credits&language=en-US&api_key=%s";
        String endpoint = String.format(Locale.US, endpointDetails, id, API_KEY);
        return apiReader.getAndConvertData(endpoint, MovieDetailsDTO.class);
    }

    @Override
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

    private Movie movieDTOToEntity(MovieDetailsDTO details)
    {
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

        Set<Genre> genres = createGenreSet(details.getGenres());

        Movie movie = new Movie();
        movie.setApiId(details.getId());
        movie.setTitle(details.getTitle());
        movie.setRating(details.getRating());
        movie.setPopularity(details.getPopularity());
        movie.setReleaseDate(details.getReleaseDate());
        movie.setDirector(director);
        movie.setGenres(genres);

        return movie;
    }

    public Set<Genre> createGenreSet(List<GenreDTO> genres)
    {
        Set<Genre> genreSet = new HashSet<>();
        for (GenreDTO g : genres)
        {
            Genre g1 = new Genre(g.getId(), g.getName());
            genreSet.add(g1);
        }
        return genreSet;
    }

}