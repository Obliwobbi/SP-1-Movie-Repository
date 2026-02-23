package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrewDTO
{
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("job")
    private String job;

    @JsonProperty("department")
    private String department;

}

//{
//        "adult": false,
//        "gender": 0,
//        "id": 2745359,
//        "known_for_department": "Camera",
//        "name": "Thomas Holm Deleurang",
//        "original_name": "Thomas Holm Deleurang",
//        "popularity": 0.3616,
//        "profile_path": null,
//        "credit_id": "6884660522c579e4f038f43d",
//        "department": "Camera",
//        "job": "\"B\" Camera Operator"
//      },