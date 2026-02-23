package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditsDTO
{
    @JsonProperty("cast")
    private List<ActorDTO> castDTOList;

    @JsonProperty("crew")
    private List<CrewDTO> crewDTOList;
}
