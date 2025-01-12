package mephi.exercise.repository.Impl;

import mephi.exercise.entity.User;
import mephi.exercise.repository.DataSource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Хранилище пользователей
 */
public class UserDataSource implements DataSource<User> {

    private Map<String, User> map = new ConcurrentHashMap<>();

    public UserDataSource(List<User> users) {
        users.forEach(user -> map.put(user.getUuid(), user));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(String uuid) {
        return map.containsKey(uuid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(String uuid, User value) {
        map.computeIfAbsent(uuid, s -> value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User get(String uuid) {
        return map.get(uuid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getAll() {
        return map.values().stream().toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void remove(String uuid) {
        map.remove(uuid);
    }
}
