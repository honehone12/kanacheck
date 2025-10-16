package com.kanacheck.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Config(
    @JsonProperty("targets") String[] targets,
    @JsonProperty("extensions") String[] extensions
) {
    public static Config getDefault() {
        return new Config(new String[] { "　" }, new String[] { "md" });
    }
}
