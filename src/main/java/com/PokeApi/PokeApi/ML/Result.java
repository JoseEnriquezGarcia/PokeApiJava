package com.PokeApi.PokeApi.ML;

import java.util.List;

public class Result<T> {
    public int count;
    public String next;
    public String previous;
    public List<T> results;
}
