package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDetailsDTO
{
    @JsonProperty("id")
    private Long id;

    @JsonProperty("original_title")
    private String title;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

    @JsonProperty("vote_average")
    private double rating;

    @JsonProperty("popularity")
    private double popularity;

    @JsonProperty("genres")
    private List<GenreDTO> genres;

    @JsonProperty("credits")
    private CreditsDTO credits;

    public String getReleaseYear()
    {
        if (releaseDate != null)
        {
            return String.valueOf(releaseDate.getYear());
        }
        return null;
    }
}