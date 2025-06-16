package com.PokeApi.PokeApi.ML;

import com.PokeApi.PokeApi.DTO.StatsDTO;
import com.PokeApi.PokeApi.DTO.TypeDTO;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Pokemon {
    private int id;
    private String name;
    private double weight;
    private int order;
    private double height;
    private int base_experience;
    public Sprites sprites;
    public Cries cries;
    public Species species;
    public List<Ability> abilities;
    public List<StatsDTO> stats;
    public List<TypeDTO> types;
    public Type tiposPokemon;
}
