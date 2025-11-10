package kanacheck.kanacheck;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kanacheck.config.Config;

class KanacheckTest {

    private Kanacheck kanacheck;
    private final Path tempFile = Path.of("temp-test-file.txt");
    private final Path configFile = Path.of("kanacheck.json");
    private StringWriter logOutput;
    private Appender appender;
    private Logger rootLogger;

    @BeforeEach
    void setUp() throws IOException {
        kanacheck = new Kanacheck();
        // Create a dummy file to be checked
        Files.writeString(tempFile, "This is a test file with a　sample string.");

        // Capture log output
        logOutput = new StringWriter();
        appender = WriterAppender.createAppender(
                PatternLayout.createDefaultLayout(),
                null,
                logOutput,
                "StringAppender",
                false,
                true);
        appender.start();
        rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.addAppender(appender);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
        Files.deleteIfExists(configFile);
        rootLogger.removeAppender(appender);
        appender.stop();
    }

    @Test
    void testCheckFile() throws IOException {
        kanacheck.config(); // Create config file
        kanacheck.checkFile(tempFile.toString());
        String consoleOutput = logOutput.toString();
        Assertions.assertTrue(consoleOutput.contains("found: '　', line: 1, file: temp-test-file.txt"));
    }

    @Test
    void testValidatePath_nullPath() {
        Assertions.assertThrows(IOException.class, () -> kanacheck.validatePath(null));
    }

    @Test
    void testValidatePath_emptyPath() {
        Assertions.assertThrows(IOException.class, () -> kanacheck.validatePath(""));
    }

    @Test
    void testReadConfig() throws IOException {
        kanacheck.config(); // Create config file
        Config config = kanacheck.readConfig();
        Assertions.assertNotNull(config);
        Assertions.assertArrayEquals(new String[] { "　" }, config.targets());
        Assertions.assertArrayEquals(new String[] { "md" }, config.extensions());
    }
}
