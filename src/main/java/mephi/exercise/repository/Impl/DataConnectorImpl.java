package mephi.exercise.repository.Impl;

import lombok.RequiredArgsConstructor;
import mephi.exercise.repository.DataConnector;
import mephi.exercise.repository.EntityMapper;
import mephi.exercise.service.Printer;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Класс с логикой чтения данных из файла и сохранения данных в файл
 */
@RequiredArgsConstructor
public class DataConnectorImpl implements DataConnector {

    private final Printer printer;
    private final File file;

    private final Map<String, List<String>> linesMap = new HashMap<>();

    /**
     * Загрузка строк файла в коллекцию
     */
    public void loadFile() {
        if (notValidFile()) {
            return;
        }

        try (var inputStream = new FileInputStream(file);
             var inputStreamReader = new InputStreamReader(inputStream);
             var reader = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                var lineAsArray = line.split(StringUtils.SPACE);
                linesMap.putIfAbsent(lineAsArray[0], new ArrayList<>());
                linesMap.computeIfPresent(lineAsArray[0], (clazz, list) -> {
                    list.add(lineAsArray[1]);
                    return list;
                });
            }
        } catch (IOException e) {
            printer.error(e.getMessage());
        }
    }

    /**
     * Очистка файла
     */
    public void clearFile() {
        if (notValidFile()) {
            return;
        }

        try(var fileWriter = new FileWriter(file, false);
            var bufferedWriter = new BufferedWriter(fileWriter);
            var writer = new PrintWriter(bufferedWriter)) {
            writer.print(StringUtils.EMPTY);
        } catch (IOException e) {
            printer.error(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> readEntities(Class<T> clazz, EntityMapper<T> mapper) {
        var className = clazz.getName();
        if (!linesMap.containsKey(className)) {
            return Collections.emptyList();
        }

        return linesMap.get(className).stream()
                .map(mapper::map)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void saveEntities(Class<T> clazz, EntityMapper<T> mapper, List<T> entities) {
        if (notValidFile()) {
            return;
        }

        var className = clazz.getName();
        try(var fileWriter = new FileWriter(file, true);
            var bufferedWriter = new BufferedWriter(fileWriter);
            var writer = new PrintWriter(bufferedWriter)) {
            entities.stream()
                    .map(entity -> className + StringUtils.SPACE + mapper.map(entity))
                    .forEach(writer::println);
        } catch (IOException e) {
            printer.error(e.getMessage());
        }
    }

    /**
     * Проверка существования файла
     *
     * @return если файл передан и существует - true, иначе - false
     */
    private boolean notValidFile() {
        return file == null || !file.exists() || !file.isFile();
    }
}
