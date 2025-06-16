package com.PokeApi.PokeApi.Service;

import com.PokeApi.PokeApi.DTO.UrlDTO;
import com.PokeApi.PokeApi.ML.Pokemon;
import com.PokeApi.PokeApi.ML.Result;
import com.PokeApi.PokeApi.ML.ResultUrlPokemon;
import com.PokeApi.PokeApi.ML.Species;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServicePokemon {

    private RestTemplate restTemplate = new RestTemplate();
    private String urlBase = "https://pokeapi.co/api/v2/pokemon";
    
    @Async
    public CompletableFuture<ResultUrlPokemon> getUrlPokemon(){
        return CompletableFuture.completedFuture(restTemplate.getForObject(urlBase + "?offset=0&limit=80", ResultUrlPokemon.class));
    }

    @Async
    public CompletableFuture<List<Pokemon>> getAllPokemon(ResultUrlPokemon resultUrlPokemon) throws InterruptedException {
        List<Pokemon> pokemones = resultUrlPokemon.results.parallelStream()
                .map(urlPokemon -> {
                    Pokemon response = restTemplate.getForObject(urlPokemon.getUrl(), Pokemon.class);
                    return response;
                })
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(pokemones);
    }

    public Pokemon getPokemonByNombre(String nombre) {
        return restTemplate.getForObject(urlBase + "/" + nombre, Pokemon.class);
    }
    
    @Async
    public CompletableFuture<Result> getTipoPokemon() throws InterruptedException {
        return CompletableFuture.completedFuture(restTemplate.getForObject("https://pokeapi.co/api/v2/type?offset=0&limit=18", Result.class));
    }

    public Species getSpecies(Pokemon pokemon) {
        return restTemplate.getForObject(pokemon.species.getUrl(), Species.class);
    }
    
    @Async
    public CompletableFuture<List<Pokemon>> getAllPokemonByType(List<UrlDTO> resultUrlDTO) throws InterruptedException {
        List<Pokemon> pokemones = resultUrlDTO.parallelStream()
                .map(urlPokemon -> {
                    Pokemon response = restTemplate.getForObject(urlPokemon.pokemon.getUrl(), Pokemon.class);
                    return response;
                })
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(pokemones);
    }
}
