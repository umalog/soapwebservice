package com.umalog.services;

import com.umalog.entity.Results;
import com.umalog.generateddata.Result;
import com.umalog.repository.ResultsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NumberSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberSearchService.class);
    @Autowired
    private final ResultsRepository resultsRepository;
    @Value("${fileNames}")
    private String[] fileNames;
    private List<Exception> errorList = new ArrayList<>();
    private int number;

    public NumberSearchService(ResultsRepository resultsRepository) {
        this.resultsRepository = resultsRepository;
    }

    public Result findResult(int number) {
        this.number = number;
        Result result = new Result();

        List<String> filesWhithNumber = Arrays.stream(fileNames).parallel()
                .filter(file -> {
                    try {
                        return parseFile(file);
                    } catch (IOException e) {
                        LOGGER.error(String.format("Получена ошибка ввода/вывада при работе с файлом %s.", file), e);
                        errorList.add(e);
                    }
                    return false;
                })
                .collect(Collectors.toList());
        if (filesWhithNumber.size() == 0) {
            result.setCode("01.Result.NotFound");
        } else {
            result.setCode("00.Result.OK");
            result.getFileNames().addAll(filesWhithNumber);
        }
        if (!errorList.isEmpty()){
            result.setCode("02.Result.Error");
            result.setError(errorList.toString());
        }
        LOGGER.info("При поиске числа {} получен результат: {}. Файлы содержащие число: {}. Пойманные ошибки: {}.",
                number, result.getCode(), result.getFileNames(), result.getError());
        saveResult(result);
        return result;
    }

    private boolean parseFile(String file) throws IOException {
        Path path = Paths.get(file);
        try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(path, StandardOpenOption.READ))) {
            StringBuffer buffer = new StringBuffer();
            int c;
            while ((c = bis.read()) != -1) {
                char ch = ((char) c);
                if (ch != ',') {
                    buffer.append(ch);
                } else {
                    try {
                        int x = Integer.parseInt(buffer.toString());
                        if (x != number) {
                            buffer = new StringBuffer();
                        } else {
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.info("\"{}\" - не число ;)", buffer.toString());
                        buffer = new StringBuffer();
                    }
                }
            }
        }
        return false;
    }

    public void saveResult(Result result){
        Results res = new Results();
        res.setCode(result.getCode());
        res.setNumber(number);
        res.setFileNames(result.getFileNames().toString());
        res.setError(result.getError());
        resultsRepository.save(res);
    }
}
