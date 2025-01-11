package mephi.exercise.commands;

import lombok.RequiredArgsConstructor;
import mephi.exercise.entity.Context;
import mephi.exercise.entity.User;
import mephi.exercise.repository.DataSource;
import mephi.exercise.service.Printer;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

import java.util.Date;
import java.util.UUID;

import static mephi.exercise.Main.DELIMITER;

/**
 * Команда регистрации нового пользователя
 * Например: {@code new Viktor} - будет зарегистрирован пользователь с логином Viktor
 * Новому пользователю будет присвоен и выведен уникальный UUID, по нему будет выполняться авторизация пользователя
 */
@RequiredArgsConstructor
@CommandLine.Command(name = "new", description = "Регистрация нового пользователя")
public class New implements Runnable {

    private final DataSource<User> dataSource;
    private final Context context;
    private final Printer printer;

    @CommandLine.Parameters(description = "Логин пользователя")
    private String login;

    @Override
    public void run() {
        if (StringUtils.isBlank(login)) {
            printer.error("Логин не может быть пустым");
            return;
        }

        // Если не соблюдать это требование, то такой логин сломает сохранение
        // и при чтении такого пользователя из файла его логин будет обрезан по этому символу
        if (StringUtils.contains(login, DELIMITER)) {
            printer.error("Логин не может содержать символ " + DELIMITER);
            return;
        }

        // Если не соблюдать это требование, то такой логин тоже сломает сохранение и последующее чтение
        if (StringUtils.contains(login, StringUtils.SPACE)) {
            printer.error("Логин не может содержать символ пробела");
            return;
        }

        var foundUser = dataSource.getAll().stream()
                .filter(u -> u.getLogin().equals(login))
                .findAny();
        if (foundUser.isPresent()) {
            throw new IllegalStateException("Пользователь с таким логином уже существует");
        }

        var user = new User();
        user.setUuid(UUID.randomUUID().toString());
        user.setLogin(login);
        dataSource.add(user.getUuid(), user);

        context.setUser(user);
        context.setSignUpTime(new Date());
        context.setSignUp(true);

        printer.info("Пользователь " + user.getLogin() + " зарегистрирован, присвоен UUID " + user.getUuid());
        printer.info();
    }
}
