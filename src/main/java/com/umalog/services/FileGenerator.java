package com.umalog.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Создает файлы тестовых данных.
 * Работает через postConstruct, чтобы не создавать еще один запускатор.
 */
@Component
public class FileGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileGenerator.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Размер создаваемого файла.
     */
    @Value("${plannedFileSize}")
    public int plannedFileSize;

    /**
     * Величина единичной генерируемой порции данных.
     */
    @Value("${batchSize}")
    public int batchSize;

    /**
     * Файлы, которые необходимо создать.
     */
    @Value("${fileNames}")
    public String[] fileNames;


    @PostConstruct
    public void postConstruct() {
        LOGGER.debug("plannedFileSize = {}, batchSize = {}, fileNames = {}",
                plannedFileSize, batchSize, fileNames == null ? 0 : Arrays.asList(fileNames));

        Arrays.stream(fileNames).forEach(file -> createFile(Paths.get(file)));
    }

    private void createFile(Path path) {
        if (Files.exists(path)) {
            LOGGER.info("Файл {} уже существует", path);
            return;
        }

        try (BufferedOutputStream bos =
                     new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.CREATE))) {
            int fileSize = 0;
            while (fileSize < plannedFileSize) {
                byte[] sequense = getRandomIntSequence();
                fileSize = fileSize + sequense.length;
                bos.write(sequense);
            }
            bos.flush();

            LOGGER.info("Создан файл {} размером {} mb", path, Files.size(path) / (1024 * 1024));
        } catch (IOException e) {
            LOGGER.error("Поймано исключение при генерации файла " + path, e);
        }
    }

    private  byte[] getRandomIntSequence() {
        StringBuilder partOfSequense = new StringBuilder();
        while (partOfSequense.length() < batchSize) {
            partOfSequense.append(RANDOM.nextInt(Short.MAX_VALUE)).append(",");
        }
        return partOfSequense.toString().getBytes();
    }
}
