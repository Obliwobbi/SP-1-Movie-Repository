package app;

import app.config.HibernateConfig;
import app.daos.MovieDAO;
import app.dto.*;
import app.services.MovieService;
import app.utils.APIReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;

public class Main
{
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public static void main(String[] args)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        APIReader apiReader = new APIReader(objectMapper);

        MovieDAO movieDAO = new MovieDAO(emf);
        MovieService ms = new MovieService(apiReader, movieDAO);

//        ms.saveMovieData();


    }
}