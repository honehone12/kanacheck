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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KanacheckMultiThreadTest {

    private KanacheckMultiThread kanacheckMultiThread;
    private final Path tempDir = Path.of("temp-test-dir");
    private final Path configFile = Path.of("kanacheck.json");
    private StringWriter logOutput;
    private Appender appender;
    private Logger rootLogger;

    @BeforeEach
    void setUp() throws IOException {
        kanacheckMultiThread = new KanacheckMultiThread();
        kanacheckMultiThread.config();
        Files.createDirectories(tempDir);

        // Create some dummy files
        Files.writeString(tempDir.resolve("file1.md"), "This is a test file with a　sample string.");
        Files.writeString(tempDir.resolve("file2.log"), "This is a log file.");
        Files.writeString(tempDir.resolve(".hidden.md"), "This is a hidden test file with a　sample string.");

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
        Files.walk(tempDir)
                .sorted(java.util.Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(java.io.File::delete);
        Files.deleteIfExists(configFile);
        rootLogger.removeAppender(appender);
        appender.stop();
    }

    @AfterAll
    static void tearDownAll() {
        KanacheckMultiThread.shutdown();
    }

    @Test
    void testCheckDir_emptyDir() throws IOException {
        Path emptyDir = Files.createDirectory(tempDir.resolve("empty"));
        Assertions.assertDoesNotThrow(() -> kanacheckMultiThread.checkDir(emptyDir.toString()));
    }

    @Test
    void testCheckDir() throws IOException {
        kanacheckMultiThread.checkDir(tempDir.toString());
        String consoleOutput = logOutput.toString();
        Assertions.assertTrue(consoleOutput.contains("found: '　', line: 1, file: " + tempDir.resolve("file1.md")));
        Assertions.assertTrue(consoleOutput.contains("found: '　', line: 1, file: " + tempDir.resolve(".hidden.md")));
    }
}
