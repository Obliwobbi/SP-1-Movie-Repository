package app.daos;

import app.entities.Director;
import jakarta.persistence.EntityManagerFactory;

public class DirectorDAO extends DAO<Director>
{
    public DirectorDAO(EntityManagerFactory emf)
    {
        super(emf, Director.class);
    }


}

