package com.PokeApi.PokeApi.ML;

import com.PokeApi.PokeApi.DTO.EvolutionChainDTO;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Species {
    private String name;
    private String url;
    public List<FlavorText> flavor_text_entries;
    public EvolutionChainDTO evolution_chain;
}
