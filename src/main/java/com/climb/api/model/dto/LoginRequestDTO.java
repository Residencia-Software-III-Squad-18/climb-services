package com.climb.api.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("senha")
    private String senha;
}
