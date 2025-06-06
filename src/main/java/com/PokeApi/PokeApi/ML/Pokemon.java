package com.PokeApi.PokeApi.ML;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Pokemon {
    private int id;
    private String name;
    private int weight;
    private int order;
    private int height;
    private int base_experience;
    public Sprites sprites;
}
