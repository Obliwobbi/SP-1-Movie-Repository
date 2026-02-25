package app.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor()
@AllArgsConstructor
@Builder
@Entity
@Table(name = "movie_actors")
public class MovieActor
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Movie movie;

    @ManyToOne
    private Actor actor;

    private String character;
}
