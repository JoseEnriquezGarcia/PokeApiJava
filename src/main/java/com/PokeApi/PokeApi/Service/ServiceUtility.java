package com.PokeApi.PokeApi.Service;

import com.PokeApi.PokeApi.ML.ResultUrlPokemon;
import com.PokeApi.PokeApi.ML.Type;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServiceUtility {
    private RestTemplate restTemplate = new RestTemplate();
    private String urlBase = "https://pokeapi.co/api/v2/pokemon";
    
    public ResultUrlPokemon paginacion(String urlPage){
        return restTemplate.getForObject(urlPage, ResultUrlPokemon.class);
    }
    
    @Async
    public CompletableFuture<ResultUrlPokemon> busquedaGetAll() throws InterruptedException{
//        Thread.sleep(100L);
        return CompletableFuture.completedFuture(restTemplate.getForObject(urlBase + "?limit=1172&offset=0", ResultUrlPokemon.class));
    }
    
    public Type busquedaByTipo(String tipoNombre) throws  InterruptedException{
        return restTemplate.getForObject("https://pokeapi.co/api/v2/type/" + tipoNombre, Type.class);
    }
}
