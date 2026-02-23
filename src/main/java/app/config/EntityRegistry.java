package app.config;

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
        //configuration.addAnnotatedClass(User.class);
    }
}