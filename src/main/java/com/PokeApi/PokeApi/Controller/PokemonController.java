package com.PokeApi.PokeApi.Controller;

import com.PokeApi.PokeApi.DTO.ChainDTO;
import com.PokeApi.PokeApi.DTO.EvolvesDTO;
import com.PokeApi.PokeApi.DTO.TypeDTO;
import com.PokeApi.PokeApi.DTO.UrlDTO;
import com.PokeApi.PokeApi.ML.FlavorText;
import com.PokeApi.PokeApi.DTO.UrlPokemonDTO;
import com.PokeApi.PokeApi.ML.Chain;
import com.PokeApi.PokeApi.ML.Pokemon;
import com.PokeApi.PokeApi.ML.Result;
import com.PokeApi.PokeApi.ML.ResultUrlPokemon;
import com.PokeApi.PokeApi.ML.Species;
import com.PokeApi.PokeApi.ML.Type;
import com.PokeApi.PokeApi.Service.ServicePokemon;
import com.PokeApi.PokeApi.Service.ServiceUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;

@Controller
@RequestMapping("/home")
public class PokemonController {

    @Autowired
    ServicePokemon servicePokemon;
    @Autowired
    ServiceUtility serviceUtility;

    @GetMapping
    public String GetAll(Model model) throws InterruptedException, ExecutionException {
        try {
            Pokemon pokemonBusqueda = new Pokemon();
            CompletableFuture<ResultUrlPokemon> resultFuture = servicePokemon.getUrlPokemon();
            ResultUrlPokemon resultUrlPokemon = resultFuture.get();
            CompletableFuture<Result> tipoFuture = servicePokemon.getTipoPokemon();
            Result tipoPokemon = tipoFuture.get();
            CompletableFuture<List<Pokemon>> future = servicePokemon.getAllPokemon(resultUrlPokemon);
            List<Pokemon> pokemones = future.get();

            model.addAttribute("tipos", tipoPokemon);
            model.addAttribute("pokemonBusqueda", pokemonBusqueda);
            model.addAttribute("listaPokemones", pokemones);
            model.addAttribute("results", resultUrlPokemon);
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
            Pokemon pokemon = servicePokemon.getPokemonByNombre(name);
            Species species = servicePokemon.getSpecies(pokemon);
            ChainDTO chains = servicePokemon.getEvolution(species.evolution_chain.getUrl());
            
            List<EvolvesDTO> evolve = chains.evolves_to;
            
            List<FlavorText> descripcion = species.flavor_text_entries
                    .stream()
                    .map(t -> (FlavorText) t)
                    .filter(t -> t.language.getName().equals("es"))
                    .collect(Collectors.toList());

            model.addAttribute("lenguaje", descripcion);
            model.addAttribute("pokemon", pokemon);

        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "ErrorPage";
        }
        return "Pokemon";
    }

    @GetMapping("/pageable")
    public String Pageable(@RequestParam String urlPage, Model model) throws InterruptedException, ExecutionException {
        try {
            Pokemon pokemonBusqueda = new Pokemon();
            ResultUrlPokemon resultUrlPokemon = serviceUtility.paginacion(urlPage);
            CompletableFuture<Result> tipoFuture = servicePokemon.getTipoPokemon();
            Result tipoPokemon = tipoFuture.get();

            //Lista pokemones
            CompletableFuture<List<Pokemon>> future = servicePokemon.getAllPokemon(resultUrlPokemon);
            List<Pokemon> pokemones = future.get();

            model.addAttribute("tipos", tipoPokemon);
            model.addAttribute("pokemonBusqueda", pokemonBusqueda);
            model.addAttribute("listaPokemones", pokemones);
            model.addAttribute("results", resultUrlPokemon);

        } catch (HttpStatusCodeException ex) {
            model.addAttribute("status", ex.getStatusCode());
            model.addAttribute("message", ex.getMessage());
            return "Error";
        }
        return "Index";
    }

    @PostMapping("/busqueda")
    public String Buscar(@ModelAttribute Pokemon pokemonBuscar, Model model) throws InterruptedException, ExecutionException {
        Pokemon pokemonBusqueda = new Pokemon();
        pokemonBusqueda.tiposPokemon = new Type();
        Result result = new Result();
        ResultUrlPokemon resultUrlPokemonFiltro = new ResultUrlPokemon();
        resultUrlPokemonFiltro.results = new ArrayList<>();

        try {
            if (!pokemonBuscar.getName().isEmpty() && "opcion1".equals(pokemonBuscar.tiposPokemon.getName())) {

                CompletableFuture<ResultUrlPokemon> resultUrlFuturo = serviceUtility.busquedaGetAll();
                ResultUrlPokemon resultUrlPokemon = resultUrlFuturo.get();

                CompletableFuture<Result> tipoFuture = servicePokemon.getTipoPokemon();
                Result tipoPokemon = tipoFuture.get();

                resultUrlPokemonFiltro.results = resultUrlPokemon.results.stream()
                        .map(u -> (UrlPokemonDTO) u)
                        .filter(e -> e.getName().contains(pokemonBuscar.getName()))
                        .collect(Collectors.toList());

                CompletableFuture<List<Pokemon>> future = servicePokemon.getAllPokemon(resultUrlPokemonFiltro);
                List<Pokemon> pokemones = future.get();

                model.addAttribute("tipos", tipoPokemon);
                model.addAttribute("pokemonBusqueda", pokemonBusqueda);
                model.addAttribute("listaPokemones", pokemones);
                model.addAttribute("results", result);
                
                //Por tipo y nombre
            } else if (!pokemonBuscar.getName().isEmpty() && !"opcion1".equals(pokemonBuscar.tiposPokemon.getName())) {
                CompletableFuture<Result> tiposFuture = servicePokemon.getTipoPokemon();
                Result tipos = tiposFuture.get();

                Type tiposPokemones = serviceUtility.busquedaByTipo(pokemonBuscar.tiposPokemon.getName());

                Type tiposPokemon = new Type();
                tiposPokemon.pokemon = new ArrayList<>();

                tiposPokemon.pokemon = tiposPokemones.pokemon.stream()
                        .map(u -> (UrlDTO) u)
                        .filter(u -> u.pokemon.getName().contains(pokemonBuscar.getName()))
                        .collect(Collectors.toList());

                CompletableFuture<List<Pokemon>> futurePokemonTipo = servicePokemon.getAllPokemonByType(tiposPokemon.pokemon);
                List<Pokemon> pokemonTipoByName = futurePokemonTipo.get();

                model.addAttribute("tipos", tipos);
                model.addAttribute("pokemonBusqueda", pokemonBusqueda);
                model.addAttribute("listaPokemones", pokemonTipoByName);
                model.addAttribute("results", result);
            } else if (pokemonBuscar.getName().isEmpty() && !"opcion1".equals(pokemonBuscar.tiposPokemon.getName())) {
                CompletableFuture<Result> tiposFuture = servicePokemon.getTipoPokemon();
                Result tiposPokemon = tiposFuture.get();

                Type tiposPokemones = serviceUtility.busquedaByTipo(pokemonBuscar.tiposPokemon.getName());

                CompletableFuture<List<Pokemon>> futurePokemonTipo = servicePokemon.getAllPokemonByType(tiposPokemones.pokemon);
                List<Pokemon> pokemonesTipo = futurePokemonTipo.get();

                model.addAttribute("tipos", tiposPokemon);
                model.addAttribute("pokemonBusqueda", pokemonBusqueda);
                model.addAttribute("listaPokemones", pokemonesTipo);
                model.addAttribute("results", result);
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
