package com.kanacheck;

import com.kanacheck.config.Config;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KanacheckMultiThread extends Kanacheck {

    private static final ExecutorService GLOBAL_EXECUTER =
        Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        );

    public static void shutdown() {
        GLOBAL_EXECUTER.shutdown();
    }

    public void checkDir(String path) {
        try {
            searchDir(validatePath(path), readConfig());
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void searchDir(Path path, Config config) throws IOException {}
}
