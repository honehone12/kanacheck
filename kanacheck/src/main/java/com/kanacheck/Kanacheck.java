package com.kanacheck;

import com.kanacheck.config.Config;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.databind.ObjectMapper;

public class Kanacheck {

    protected static final Path CONFIG_PATH = Paths.get("kanacheck.json");
    protected final Logger _log = LogManager.getLogger(Kanacheck.class);

    public void config() {
        try {
            final var config = Config.getDefault();
            final var ser = new ObjectMapper();
            final var json = ser
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(config);
            Files.writeString(CONFIG_PATH, json);
            _log.info("config file has been generated as '{}'", CONFIG_PATH);
        } catch (Exception e) {
            _log.error(e);
        }
    }

    public void checkFile(String path) {
        try {
            searchFile(validatePath(path), readConfig());
        } catch (Exception e) {
            _log.error(e);
        }
    }

    protected Path validatePath(String path) throws IOException {
        if (path == null || path.length() == 0) {
            throw new IOException("please provide path");
        }

        return Paths.get(path);
    }

    protected Config readConfig() throws IOException {
        if (!Files.exists(CONFIG_PATH)) {
            throw new IOException("please config first (use --config)");
        }

        final var json = Files.readString(CONFIG_PATH);
        final var de = new ObjectMapper();
        final var config = de.readValue(json, Config.class);
        return config;
    }

    protected void searchFile(Path path, Config config) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IOException(
                String.format("'%s' is not a file (use --recursive)", path)
            );
        }

        try (final BufferedReader r = Files.newBufferedReader(path)) {
            long n = 1;
            while (true) {
                final var line = r.readLine();
                if (line == null) {
                    break;
                }

                for (final var s : config.search()) {
                    if (line.contains(s)) {
                        _log.info(
                            "found: '{}', line: {}, file: {}",
                            s,
                            n,
                            path
                        );
                    }
                }

                n++;
            }
        }
    }
}
