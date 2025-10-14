package com.kanacheck;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        final var cmd = new CommandLine(new Command());
        cmd.execute(args);
    }
}
