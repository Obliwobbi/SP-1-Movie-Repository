package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenreDTO
{
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;
}


// "genres": [
//    {
//      "id": 35,
//      "name": "Comedy"
//    },
//    {