package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    //Mange til mange (join table) - Movie owns this side
    @ManyToMany()
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_dbId"),
            inverseJoinColumns = @JoinColumn(name = "actor_dbId")
    )
    private Set<Actor> actors;

    //Mange til mange (join table) - Movie owns this side
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_dbId"),
            inverseJoinColumns = @JoinColumn(name = "genre_dbId")
    )
    private Set<Genre> genres;

    @Column(nullable = false)
    private double popularity;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private LocalDate releaseDate;


    public Movie(Long apiId, String title, Director director, Double rating, LocalDate releaseDate, double popularity, Set<Genre> genres, Set<Actor> actors)
    {
        this.apiId = apiId;
        this.title = title;
        this.director = director;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.genres = genres;
        this.actors = actors;
        this.popularity = popularity;
    }
}
