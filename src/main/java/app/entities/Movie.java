package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "movies")
public class Movie
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbId;

    @Column(nullable = false)
    private Long apiId;

    @Column(nullable = false)
    private String title;

    //Mange film til én direktør
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "director_dbId")
    private Director director;

    //Mange til mange (join table)
    @ManyToMany(mappedBy = "movies")
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_dbId"),
            inverseJoinColumns = @JoinColumn(name = "actor_dbId")
    )
    private Set<Actor> actors;

    //Mange til mange (join table)
    @ManyToMany(mappedBy = "movies")
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_dbId"),
            inverseJoinColumns = @JoinColumn(name = "genre_dbId")
    )
    private Set<Genre> genres;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private LocalDate releaseDate;

    public void addActor(Actor actor)
    {
        actors.add(actor);
        actor.getMovies().add(this);
    }

    public void removeActor(Actor actor)
    {
        actors.remove(actor);
        actor.getMovies().remove(this);
    }

    public void addGenre(Genre genre)
    {
        genres.add(genre);
        genre.getMovies().add(this);
    }

    public void removeGenre(Genre genre)
    {
        genres.remove(genre);
        genre.getMovies().remove(this);
    }

    public void setDirector(Director director)
    {
        this.director = director;
        director.getMovies().add(this);
    }

}
