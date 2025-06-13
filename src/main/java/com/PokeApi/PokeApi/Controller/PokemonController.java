package com.PokeApi.PokeApi.Controller;

import com.PokeApi.PokeApi.DTO.TypeDTO;
import com.PokeApi.PokeApi.ML.FlavorText;
import com.PokeApi.PokeApi.ML.UrlPokemon;
import com.PokeApi.PokeApi.ML.Pokemon;
import com.PokeApi.PokeApi.ML.Result;
import com.PokeApi.PokeApi.ML.Species;
import com.PokeApi.PokeApi.ML.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
//            Result<UrlPokemon> resultPokemon = new Result<>();
//            
//            resultPokemon = restTemplate.getForObject(urlBase + "?offset=0&limit=40", Result.class);

            ResponseEntity<Result<UrlPokemon>> getPokemon = restTemplate.exchange(urlBase + "?offset=0&limit=150",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<UrlPokemon>>() {
            });

            Result results = new Result();
            results = restTemplate.getForObject("https://pokeapi.co/api/v2/type", Result.class);

            if (getPokemon.getStatusCode().is2xxSuccessful()) {
                Result result = new Result();
                Pokemon pokemonSearch = new Pokemon();
                result = getPokemon.getBody();
                List<UrlPokemon> url = new ArrayList<>();
                url = result.results;

                //Lista pokemones
                List<Pokemon> pokemons = new ArrayList<>();
                pokemons = url.parallelStream()
                        .map(urlPokemon -> {
                            Pokemon response = restTemplate.getForObject(urlPokemon.getUrl(), Pokemon.class);
                            return response;
                        })
                        .collect(Collectors.toList());

                Map<String, String> colors = new HashMap<>();
                colors.put("normal", "#9fa19f");
                colors.put("fighting", "#ff8000");
                colors.put("flying", "#BAAAFF");
                
                model.addAttribute("types", results);
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
            Pokemon pokemon = restTemplate.getForObject(urlBase + "/" + name, Pokemon.class);

            Species responseSpecies = restTemplate.getForObject(pokemon.species.getUrl(), Species.class);

            Species species = new Species();
            species = responseSpecies;

            List<FlavorText> texto = new ArrayList<>();

            texto = species.flavor_text_entries
                    .stream()
                    .map(t -> (FlavorText) t)
                    .filter(t -> t.language.getName().equals("es"))
                    .collect(Collectors.toList());

            if (pokemon != null) {
                model.addAttribute("language", texto);
                model.addAttribute("pokemon", pokemon);
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

            Result getType = new Result();
            getType = restTemplate.getForObject("https://pokeapi.co/api/v2/type", Result.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Result result = new Result();
                result = response.getBody();
                List<UrlPokemon> url = new ArrayList<>();
                url = result.results;

                //Lista pokemones
                List<Pokemon> pokemons = new ArrayList<>();

                pokemons = url.parallelStream()
                        .map(urlPokemon -> {
                            Pokemon pokemon = restTemplate.getForObject(urlPokemon.getUrl(), Pokemon.class);
                            return pokemon;
                        })
                        .collect(Collectors.toList());

                Pokemon pokemonSearch = new Pokemon();

                model.addAttribute("types", getType);
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
            if (!pokemon.getName().isEmpty()) {
                ResponseEntity<Result<UrlPokemon>> getPokemon = restTemplate.exchange(urlBase + "?limit=1172&offset=0",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<Result<UrlPokemon>>() {
                });
                
                List<UrlPokemon> url = new ArrayList<>();
                url = getPokemon.getBody().results;
                
                List<UrlPokemon> urlBusqueda = new ArrayList<>();
                
                urlBusqueda = url.stream()
                        .map(u ->(UrlPokemon) u)
                        .filter(e -> e.getName().contains(pokemon.getName()))
                        .collect(Collectors.toList());
                
                
                
                
                List<Pokemon> pokemons = new ArrayList<>();

                pokemons = urlBusqueda.parallelStream()
                        .map(urlPokemon -> {
                            Pokemon pokemo = restTemplate.getForObject(urlBase + "/" + urlPokemon.getName(), Pokemon.class);
                            return pokemo;
                        })
                        .collect(Collectors.toList());
                
                Result getType = new Result();
                getType = restTemplate.getForObject("https://pokeapi.co/api/v2/type", Result.class);

                if (pokemons != null) {
                    Pokemon pokemonSearch = new Pokemon();

                    model.addAttribute("types", getType);
                    model.addAttribute("pokemonSearch", pokemonSearch);
                    model.addAttribute("listaPokemons", pokemons);
                    model.addAttribute("results", getPokemon.getBody());
                }
            } else {
                GetAll(model);
            }
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "Error";
        }
        return "Index";
    }
}
