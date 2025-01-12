package mephi.exercise.repository.Impl;

import mephi.exercise.entity.Link;
import mephi.exercise.repository.DataSource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Хранилище ссылок
 */
public class LinkDataSource implements DataSource<Link> {

    private Map<String, Link> map = new ConcurrentHashMap<>();

    /**
     * Конструктор, принимает начальный список ссылок
     *
     * @param links список ссылок
     */
    public LinkDataSource(List<Link> links) {
        links.forEach(link -> map.put(link.getKey(), link));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(String key, Link value) {
        map.computeIfAbsent(key, s -> value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Link get(String key) {
        return map.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Link> getAll() {
        return map.values().stream().toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void remove(String key) {
        map.remove(key);
    }
}
