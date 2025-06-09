package com.PokeApi.PokeApi.ML;

import com.PokeApi.PokeApi.DTO.StatsDTO;
import java.util.List;
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
    public Cries cries;
    public List<Ability> abilities;
    public List<StatsDTO> stats;
}
