package com.PokeApi.PokeApi.ML;

import com.PokeApi.PokeApi.DTO.OtherDTO;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Sprites {
    private String back_default;
    private String back_female;
    private String back_shiny;
    private String back_shiny_female;
    private String front_default;
    private String front_female;
    private String front_shiny;
    private String front_shiny_female;
    public OtherDTO other;
}
