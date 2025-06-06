package com.PokeApi.PokeApi.Controller;

import com.PokeApi.PokeApi.DTO.UrlPokemonDTO;
import com.PokeApi.PokeApi.ML.Pokemon;
import com.PokeApi.PokeApi.ML.Result;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/home")
public class PokemonController {

    private RestTemplate restTemplate = new RestTemplate();
    private String urlBase = "https://pokeapi.co/api/v2/pokemon";

    @GetMapping
    public String GetAll(Model model) {
        try {

            ResponseEntity<Result<UrlPokemonDTO>> getPokemon = restTemplate.exchange(urlBase,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<UrlPokemonDTO>>() {
            });

            if (getPokemon.getStatusCode().is2xxSuccessful()) {
                Result result = new Result();

                result = getPokemon.getBody();

                List<UrlPokemonDTO> url = new ArrayList<>();

                url = result.results;

                //Lista pokemones
                List<Pokemon> pokemons = new ArrayList<>();

                for (UrlPokemonDTO urls : url) {
                    ResponseEntity<Pokemon> getUniquePokemon = restTemplate.exchange(urls.getUrl(),
                            HttpMethod.GET,
                            HttpEntity.EMPTY,
                            new ParameterizedTypeReference<Pokemon>() {
                    });

                    if (getUniquePokemon.getStatusCode().is2xxSuccessful()) {
                        pokemons.add(getUniquePokemon.getBody());
                    }
                }
                
                model.addAttribute("listaPokemons", pokemons);
            }
        } catch (HttpStatusCodeException ex) {
            System.out.println("dsdsd");
        }
        return "Index";
    }
}
