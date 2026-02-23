package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActorDTO
{
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("character")
    private String character;
}

// "adult": false,
//        "gender": 0,
//        "id": 2507250,
//        "known_for_department": "Acting",
//        "name": "Ståle Tørring",
//        "original_name": "Ståle Tørring",
//        "popularity": 0.2113,
//        "profile_path": null,
//        "cast_id": 89,
//        "character": "Norsk præst",
//        "credit_id": "688464fc2f591ad8d18a685a",
//        "order": 53