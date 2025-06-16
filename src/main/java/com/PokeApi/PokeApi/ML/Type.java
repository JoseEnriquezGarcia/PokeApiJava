package com.PokeApi.PokeApi.ML;

import com.PokeApi.PokeApi.DTO.UrlDTO;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Type {
    private String name;
    private int id;
    public List<UrlDTO> pokemon;
}
