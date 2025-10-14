package com.kanacheck;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.databind.ObjectMapper;
import com.kanacheck.config.Config;

public class Kanacheck {
    private final Path CONFIG_PATH = Paths.get("kanacheck.json");
    private final Logger LOG = LogManager.getLogger(Kanacheck.class); 

    public void config() {
        try {
            final var config = Config.getDefault();
            final var ser = new ObjectMapper();
            final var json = ser.writerWithDefaultPrettyPrinter()
                .writeValueAsString(config);
            Files.writeString(CONFIG_PATH, json);
            LOG.info("Config file has been generated as " + CONFIG_PATH);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    public void check(boolean recursive, String path) {
        if (path == null) {
            LOG.error("Please provide path");
            return;
        }

        try {
            if (!Files.exists(CONFIG_PATH)) {
                LOG.error("Please config first (--config)");
                return;
            }

            final var config = readConfig();
            if (recursive) {
                searchDir(config);
            } else {
                searchFile(config);
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    private Config readConfig() throws IOException {
        final var json = Files.readString(CONFIG_PATH);
        final var de = new ObjectMapper();
        final var config = de.readValue(json, Config.class);
        return config;
    }

    private void searchFile(Config config) {

    }

    private void searchDir(Config config) {

    }
}
