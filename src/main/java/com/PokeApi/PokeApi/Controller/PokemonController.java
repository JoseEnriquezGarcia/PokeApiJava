package com.PokeApi.PokeApi.Controller;

import com.PokeApi.PokeApi.DTO.TypeDTO;
import com.PokeApi.PokeApi.ML.UrlPokemon;
import com.PokeApi.PokeApi.ML.Pokemon;
import com.PokeApi.PokeApi.ML.Result;
import com.PokeApi.PokeApi.ML.Type;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            ResponseEntity<Result<UrlPokemon>> getPokemon = restTemplate.exchange(urlBase + "?offset=0&limit=40",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<UrlPokemon>>() {
            });

            ResponseEntity<Result> getType = restTemplate.exchange("https://pokeapi.co/api/v2/type",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result>() {
            });

            if (getPokemon.getStatusCode().is2xxSuccessful()) {
                Result result = new Result();
                Pokemon pokemonSearch = new Pokemon();
                result = getPokemon.getBody();
                List<UrlPokemon> url = new ArrayList<>();
                url = result.results;

                //Lista pokemones
                List<Pokemon> pokemons = new ArrayList<>();

                for (UrlPokemon urls : url) {
                    ResponseEntity<Pokemon> getUniquePokemon = restTemplate.exchange(urls.getUrl(),
                            HttpMethod.GET,
                            HttpEntity.EMPTY,
                            new ParameterizedTypeReference<Pokemon>() {
                    });

                    if (getUniquePokemon.getStatusCode().is2xxSuccessful()) {
                        pokemons.add(getUniquePokemon.getBody());
                    }
                }

                model.addAttribute("types", getType.getBody());
                model.addAttribute("pokemonSearch", pokemonSearch);
                model.addAttribute("listaPokemons", pokemons);
                model.addAttribute("results", getPokemon.getBody());
            }
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "ErrorPage";
        }
        return "Index";
    }

    @GetMapping("/byname/{name}")
    public String getByName(@PathVariable String name, Model model) {
        try {
            ResponseEntity<Pokemon> response = restTemplate.exchange(urlBase + "/" + name,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Pokemon>() {
            });

            if (response.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("pokemon", response.getBody());
            }
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "ErrorPage";
        }
        return "Pokemon";
    }

    @GetMapping("/pageable")
    public String Pageable(@RequestParam String urlPage, Model model) {
        try {
            ResponseEntity<Result<UrlPokemon>> response = restTemplate.exchange(urlPage,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<UrlPokemon>>() {
            });
            
            ResponseEntity<Result> getType = restTemplate.exchange("https://pokeapi.co/api/v2/type",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result>() {
            });

            if (response.getStatusCode().is2xxSuccessful()) {
                Result result = new Result();
                result = response.getBody();
                List<UrlPokemon> url = new ArrayList<>();
                url = result.results;

                //Lista pokemones
                List<Pokemon> pokemons = new ArrayList<>();

                for (UrlPokemon urls : url) {
                    ResponseEntity<Pokemon> getUniquePokemon = restTemplate.exchange(urls.getUrl(),
                            HttpMethod.GET,
                            HttpEntity.EMPTY,
                            new ParameterizedTypeReference<Pokemon>() {
                    });

                    if (getUniquePokemon.getStatusCode().is2xxSuccessful()) {
                        pokemons.add(getUniquePokemon.getBody());
                    }
                }
                Pokemon pokemonSearch = new Pokemon();

                model.addAttribute("types", getType.getBody());
                model.addAttribute("pokemonSearch", pokemonSearch);
                model.addAttribute("listaPokemons", pokemons);
                model.addAttribute("results", response.getBody());
            }

        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "Error";
        }
        return "Index";
    }

    @PostMapping("/busqueda")
    public String Buscar(@ModelAttribute Pokemon pokemon, Model model) {
        try {
            ResponseEntity<Result<UrlPokemon>> getPokemon = restTemplate.exchange(urlBase,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<UrlPokemon>>() {
            });

            ResponseEntity<Pokemon> response = restTemplate.exchange(urlBase + "/" + pokemon.getName(),
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Pokemon>() {
            });
            
            ResponseEntity<Result> getType = restTemplate.exchange("https://pokeapi.co/api/v2/type",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result>() {
            });
            
            if (response.getStatusCode().is2xxSuccessful()) {
                Pokemon pokemonSearch = new Pokemon();
                
                model.addAttribute("types", getType.getBody());
                model.addAttribute("pokemonSearch", pokemonSearch);
                model.addAttribute("listaPokemons", response.getBody());
                model.addAttribute("results", getPokemon.getBody());
            }
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "Error";
        }
        return "Index";
    }
}
