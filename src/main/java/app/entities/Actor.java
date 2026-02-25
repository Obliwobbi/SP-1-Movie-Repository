package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor()
@AllArgsConstructor
@Builder
@Entity
@Table(name = "actors")
public class Actor
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbId;

    @Column(nullable = false)
    private Long apiId;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "actor")
    Set<MovieActor> movieActors;
}