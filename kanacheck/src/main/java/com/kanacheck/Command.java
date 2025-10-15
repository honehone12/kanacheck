package com.kanacheck;

import picocli.CommandLine.Option;

public class Command implements Runnable {

    @Option(names = { "-p", "--path" })
    private String _path;

    @Option(names = { "-r", "--recursive" })
    private boolean _recursive;

    @Option(names = "--config")
    private boolean _config;

    public void run() {
        if (_config) {
            final var kanacheck = new Kanacheck();
            kanacheck.config();
        } else {
            if (_recursive) {
                final var kanacheck = new KanacheckMultiThread();
                kanacheck.checkDir(_path);
                KanacheckMultiThread.shutdown();
            } else {
                final var kanacheck = new Kanacheck();
                kanacheck.checkFile(_path);
            }
        }
    }
}
