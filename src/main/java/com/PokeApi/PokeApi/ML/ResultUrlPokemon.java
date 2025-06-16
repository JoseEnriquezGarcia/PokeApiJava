package com.PokeApi.PokeApi.ML;

import com.PokeApi.PokeApi.DTO.UrlPokemonDTO;
import java.util.List;

public class ResultUrlPokemon {
    public int count;
    public String next;
    public String previous;
    public List<UrlPokemonDTO> results;
}
