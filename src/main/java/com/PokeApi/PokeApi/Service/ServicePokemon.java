package com.PokeApi.PokeApi.Service;

import com.PokeApi.PokeApi.ML.Result;
import org.springframework.web.client.RestTemplate;

public class ServicePokemon {
    private RestTemplate restTemplate = new RestTemplate();
    
    public Result getAllPokemon(){
        return restTemplate.getForEntity("https://pokeapi.co/api/v2/pokemon", Result);
    }
}
