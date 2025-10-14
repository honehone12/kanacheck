package com.kanacheck;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.databind.ObjectMapper;
import com.kanacheck.config.Config;

public class Kanacheck { 
    private final Logger _log = LogManager.getLogger(Kanacheck.class); 

    public void config() {
        try {
            final var config = Config.getDefault();
            final var ser = new ObjectMapper();
            final var json = ser.writerWithDefaultPrettyPrinter()
                .writeValueAsString(config);
            final var path = Paths.get("kanacheck.json");
            Files.writeString(path, json);
            _log.info("config file has been generated as " + path);
        } catch (Exception e) {
            _log.error(e);
        }
    }

    public void check(boolean recursive, String path) {
        if (path == null) {
            _log.error("Please provide path");
            return;
        }
    }
}
