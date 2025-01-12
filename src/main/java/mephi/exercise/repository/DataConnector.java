package mephi.exercise.repository;

import java.util.List;

/**
 * Интерфейс для чтения данных из файла и сохранения данных в файл
 */
public interface DataConnector {

    /**
     * Чтение данных из файла
     *
     * @param mapper маппер для преобразования строки файла в объект
     * @param clazz класс читаемых объектов
     * @return список прочитанных объектов
     * @param <T> тип объекта
     */
    <T> List<T> readEntities(Class<T> clazz, EntityMapper<T> mapper);

    /**
     * Сохранение данных в файл
     *
     * @param mapper маппер для преобразования объекта в строку файла
     * @param clazz класс сохраняемых объектов
     * @param entities список объектов
     * @param <T> тип объекта
     */
    <T> void saveEntities(Class<T> clazz, EntityMapper<T> mapper, List<T> entities);
}
