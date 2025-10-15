package com.kanacheck;

import com.kanacheck.config.Config;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
            _log.error(e);
        }
    }

    private Optional<String> getFileExtension(Path path) {
        final var fileName = path.getFileName().toString();
        final var idx = fileName.lastIndexOf('.');
        // ignore hidden files
        if (idx > 0) {
            return Optional.of(fileName.substring(idx + 1));
        } else {
            return Optional.empty();
        }
    }

    private void searchDir(Path path, Config config) throws IOException {
        if (!Files.isDirectory(path)) {
            throw new IOException(
                String.format(
                    "'%s' is not a directory (remove --recursive)",
                    path
                )
            );
        }

        var extensions = List.of(config.extensions());
        var futures = new ArrayList<CompletableFuture<Void>>();
        try (final var dirStream = Files.walk(path)) {
            final var iter = dirStream.iterator();
            while (iter.hasNext()) {
                final var file = iter.next();
                if (!Files.isRegularFile(file)) {
                    continue;
                }

                final var ext = getFileExtension(file);
                // ignore files without extension
                if (ext.isEmpty()) {
                    continue;
                }

                if (!extensions.contains(ext.get())) {
                    continue;
                }

                final var fut = CompletableFuture.runAsync(
                    () -> {
                        try {
                            searchFile(file, config);
                        } catch (Exception e) {
                            _log.warn(
                                "skipping '{}' which is not a utf-8 encoded file",
                                file
                            );
                        }
                    },
                    GLOBAL_EXECUTER
                );
                futures.add(fut);
            }
        }

        final var all = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        all.join();
    }
}
