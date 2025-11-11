package kanacheck.kanacheck;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.OutputStreamAppender;
import kanacheck.config.Config;
import org.slf4j.LoggerFactory;

class KanacheckTest {

    private Kanacheck kanacheck;
    private final Path tempFile = Path.of("temp-test-file.txt");
    private final Path configFile = Path.of("kanacheck.json");
    private ByteArrayOutputStream logOutput;
    private Appender<ILoggingEvent> appender;
    private Logger rootLogger;

    @BeforeEach
    void setUp() throws IOException {
        kanacheck = new Kanacheck();
        // Create a dummy file to be checked
        Files.writeString(tempFile, "This is a test file with a　sample string.");

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
        Files.deleteIfExists(tempFile);
        Files.deleteIfExists(configFile);
        rootLogger.detachAppender(appender);
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
