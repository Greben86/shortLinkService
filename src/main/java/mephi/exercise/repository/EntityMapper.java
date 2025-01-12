package mephi.exercise.repository;

/**
 * Маппер строки из файла в объект и наоборот
 *
 * @param <T> тип объекта
 */
public interface EntityMapper<T> {

    /**
     * Маппинг строки в объект
     *
     * @param line строка
     * @return полученный объект
     */
    T map(String line);

    /**
     * Маппинг объекта в строку
     *
     * @param entity объект
     * @return строка
     */
    String map(T entity);
}
