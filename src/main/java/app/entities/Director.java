package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "directors")
public class Director
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbId;

    @Column()
    private Long apiId;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "director")
    private Set<Movie> movies;

    public Director(Long apiId, String name)
    {
        this.apiId = apiId;
        this.name = name;
    }

    public Director(String name)
    {
        this.name = name;
    }
}
