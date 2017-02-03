package com.github.invader.agent.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.rolling.*;
import ch.qos.logback.core.util.FileSize;
import com.github.invader.agent.config.AgentConfiguration;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


public class LoggingConfigurator {

    private final Logger rootLogger;

    public LoggingConfigurator() {
        rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    }

    public void configure(AgentConfiguration agentConfiguration) throws IOException {
        rootLogger.setLevel(Level.valueOf(agentConfiguration.getLog().getLevel()));

        addConsoleAppender();
        addFileAppender(agentConfiguration.getLog());
    }

    private void addConsoleAppender() {
        if (rootLogger.getAppender("Console") != null) {
            return;
        }
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender();
        consoleAppender.setName("Console");
        consoleAppender.setTarget("System.out");
        consoleAppender.setEncoder(getEncoder(rootLogger.getLoggerContext()));
        consoleAppender.setContext(rootLogger.getLoggerContext());
        consoleAppender.start();
        rootLogger.addAppender(consoleAppender);

    }

    public void addFileAppender(AgentConfiguration.Log logConfiguration) throws IOException {
        if (rootLogger.getAppender("File") != null) {
            return;
        }

        String fileName = getLogFileName(logConfiguration);

        FileAppender<ILoggingEvent> fileAppender = createFileAppender(logConfiguration.getFileCount(), logConfiguration.getFileCount(), fileName, logConfiguration.isDaily());
        fileAppender.setEncoder(getEncoder(rootLogger.getLoggerContext()));
        fileAppender.start();
        rootLogger.addAppender(fileAppender);
    }

    private String getLogFileName(AgentConfiguration.Log logConfiguration) {
        return new File(logConfiguration.getFilePath(), logConfiguration.getFileName()).getPath();
    }

    private FileAppender<ILoggingEvent> createDailyAppender(Logger rootLogger, int fileCount, String fileName)
    {
        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender();

        fileAppender.setContext(rootLogger.getLoggerContext());
        fileAppender.setName("File");
        fileAppender.setFile(fileName);
        fileAppender.setAppend(true);
        fileAppender.setPrudent(false);

        TimeBasedRollingPolicy<ILoggingEvent> timePolicy = new TimeBasedRollingPolicy();
        timePolicy.setFileNamePattern(fileName + ".%d{yyyy-MM-dd}");
        timePolicy.setContext(rootLogger.getLoggerContext());
        timePolicy.setMaxHistory(fileCount);
        timePolicy.setParent(fileAppender);
        fileAppender.setRollingPolicy(timePolicy);
        timePolicy.start();

        return fileAppender;
    }

    private FileAppender<ILoggingEvent> createFileAppender(int fileCount, long sizeLimit, String fileName, boolean isDaily)
    {
        if (isDaily) {
            return createDailyAppender(rootLogger, fileCount, fileName);
        }
        if (fileCount <= 1)
        {
            FileAppender<ILoggingEvent> fileAppender = new FileAppender();
            fileAppender.setName("File");
            fileAppender.setFile(fileName);
            fileAppender.setAppend(true);
            fileAppender.setPrudent(false);
            fileAppender.setContext(rootLogger.getLoggerContext());
            return fileAppender;
        }

        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender();

        fileAppender.setContext(rootLogger.getLoggerContext());
        fileAppender.setName("File");
        fileAppender.setFile(fileName);
        fileAppender.setAppend(true);
        fileAppender.setPrudent(false);

        FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
        rollingPolicy.setContext(rootLogger.getLoggerContext());
        rollingPolicy.setParent(fileAppender);
        rollingPolicy.setMinIndex(1);
        rollingPolicy.setMaxIndex(fileCount - 1);
        rollingPolicy.setFileNamePattern(fileName + ".%i");
        fileAppender.setRollingPolicy(rollingPolicy);

        if (sizeLimit > 0) {
            SizeBasedTriggeringPolicy triggerPolicy = new SizeBasedTriggeringPolicy();
            triggerPolicy.setMaxFileSize(new FileSize(sizeLimit));
            fileAppender.setTriggeringPolicy(triggerPolicy);
            triggerPolicy.start();
        }

        rollingPolicy.start();

        return fileAppender;
    }

    private static Encoder<ILoggingEvent> getEncoder(LoggerContext loggerContext) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();

        encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        encoder.setContext(loggerContext);
        encoder.start();

        return encoder;
    }
}
