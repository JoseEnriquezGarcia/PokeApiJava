package com.PokeApi.PokeApi.Service;

import com.PokeApi.PokeApi.ML.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServicePokemon {
    
    private RestTemplate restTemplate = new RestTemplate();
    
    public Result getAllPokemon(){
        return restTemplate.getForObject("https://pokeapi.co/api/v2/pokemon", Result.class);
    }
}
