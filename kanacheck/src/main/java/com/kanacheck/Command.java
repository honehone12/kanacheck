package com.kanacheck;

import picocli.CommandLine.Option;

public class Command implements Runnable {
    @Option(names = {"-p", "--path"})
    private String _path;
    @Option(names = {"-r", "--recursive"})
    private boolean _recursive;
    @Option(names = "--config")
    private boolean _config;

    public void run() {
        final var kanacheck = new Kanacheck();

        if (_config) {
            kanacheck.config();
        } else {
            kanacheck.check(_recursive, _path);
        }
    }
}
