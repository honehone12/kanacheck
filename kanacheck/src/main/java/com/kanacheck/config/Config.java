package com.kanacheck.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Config {
    @JsonProperty("search")
    private String[] _search;
    
    public Config() {
        _search = new String[]{};
    }

    public Config(String[] search) {
        _search = search;
    }

    public static Config getDefault() {
        return new Config(new String[]{"　"});
    }

    public void setSearch(String[] search) {
        _search = search;
    }

    public String[] getSearch() {
        return _search;
    }
}
