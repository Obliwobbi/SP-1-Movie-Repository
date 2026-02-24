package app.persistence.config;

import app.entities.*;
import org.hibernate.cfg.Configuration;

final class EntityRegistry
{

    private EntityRegistry()
    {
    }

    static void registerEntities(Configuration configuration)
    {
        // TODO: Add entities here...
        configuration.addAnnotatedClass(Movie.class);
        configuration.addAnnotatedClass(Actor.class);
        configuration.addAnnotatedClass(Genre.class);
        configuration.addAnnotatedClass(Director.class);
        //configuration.addAnnotatedClass(User.class);
    }
}