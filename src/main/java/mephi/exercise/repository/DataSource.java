package mephi.exercise.repository;

import java.util.List;

/**
 * Источник данных
 *
 * @param <V> тип хранимых данных
 */
public interface DataSource<V> {

    /**
     * Метод проверяет наличие значения по ключу
     *
     * @param key ключ
     * @return если есть объект - true, иначе false
     */
    boolean containsKey(String key);

    /**
     * Метод добавляет объект в хранилище
     *
     * @param key ключ
     * @param value хранимый объект
     */
    void add(String key, V value);

    /**
     * Метод возвращает объект по ключу
     *
     * @param key ключ
     * @return объект, если он есть в хранилище, если нет - null
     */
    V get(String key);

    /**
     * Метод возвращает все объекты в хранилище
     *
     * @return коллекция объектов
     */
    List<V> getAll();

    /**
     * Метод удаляет объект по ключу
     *
     * @param key ключ
     */
    void remove(String key);
}
