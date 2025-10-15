package com.kanacheck.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Config(@JsonProperty("search") String[] search) {
    public static Config getDefault() {
        return new Config(new String[] { "　" });
    }
}
