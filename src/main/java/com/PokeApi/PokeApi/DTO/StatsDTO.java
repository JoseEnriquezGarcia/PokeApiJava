package com.PokeApi.PokeApi.DTO;

import com.PokeApi.PokeApi.ML.Stat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatsDTO {
    private int base_stat;
    private int effort;
    public Stat stat;
}
