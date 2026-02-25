package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor()
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

    //En til mange (join table) - Movie owns this side
    @OneToMany(mappedBy = "movie")
    private Set<MovieActor> actors;

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

    public Movie(Long apiId, String title, Director director, Double rating, LocalDate releaseDate, double popularity, Set<Genre> genres, Set<MovieActor> actors)
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
