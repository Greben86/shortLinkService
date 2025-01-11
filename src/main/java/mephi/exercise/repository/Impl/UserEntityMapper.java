package mephi.exercise.repository.Impl;

import lombok.RequiredArgsConstructor;
import mephi.exercise.entity.User;
import mephi.exercise.repository.EntityMapper;
import mephi.exercise.service.Printer;
import org.apache.commons.lang3.StringUtils;

import java.io.ObjectStreamClass;

import static mephi.exercise.Main.DELIMITER;

/**
 * Маппер пользователей
 */
@RequiredArgsConstructor
public class UserEntityMapper implements EntityMapper<User> {

    private static final String CLASS_NAME = User.class.getName();
    private static final int FIELD_COUNT = User.class.getDeclaredFields().length;
    private static final String USER_UID = String.valueOf(ObjectStreamClass.lookup(User.class).getSerialVersionUID());

    private final Printer printer;

    /**
     * {@inheritDoc}
     */
    @Override
    public User map(String line) {
        if (StringUtils.isBlank(line)) {
            return null;
        }

        var splittedLine = line.split(DELIMITER);
        if (!USER_UID.equals(splittedLine[0])) {
            printer.error("Не совпадает версия объекта " + User.class.getName());
            return null;
        }

        if (splittedLine.length != FIELD_COUNT) {
            printer.error("Не совпадает количество атрибутов объекта");
            return null;
        }

        return User.builder()
                .setUuid(splittedLine[1])
                .setLogin(splittedLine[2])
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String map(User user) {
        return new StringBuilder()
                .append(USER_UID)
                .append(DELIMITER)
                .append(user.getUuid())
                .append(DELIMITER)
                .append(user.getLogin())
                .toString();
    }
}
