package kanacheck.kanacheck;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.OutputStreamAppender;

class KanacheckMultiThreadTest {

    private KanacheckMultiThread kanacheckMultiThread;
    private final Path tempDir = Path.of("temp-test-dir");
    private final Path configFile = Path.of("kanacheck.json");
    private ByteArrayOutputStream logOutput;
    private Appender<ILoggingEvent> appender;
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
        logOutput = new ByteArrayOutputStream();
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%msg%n");
        encoder.start();

        OutputStreamAppender<ILoggingEvent> osAppender = new OutputStreamAppender<>();
        osAppender.setContext(context);
        osAppender.setName("OutputStream");
        osAppender.setEncoder(encoder);
        osAppender.setOutputStream(logOutput);
        osAppender.start();

        appender = osAppender;

        rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(appender);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
                .sorted(java.util.Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(java.io.File::delete);
        Files.deleteIfExists(configFile);
        rootLogger.detachAppender(appender);
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
