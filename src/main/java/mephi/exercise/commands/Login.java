package mephi.exercise.commands;

import lombok.RequiredArgsConstructor;
import mephi.exercise.entity.Context;
import mephi.exercise.entity.User;
import mephi.exercise.repository.DataSource;
import mephi.exercise.service.Printer;
import picocli.CommandLine;

import java.util.Date;

/**
 * Команда авторизации пользователя
 * Например: {@code login fbd14e52-14e7-4e95-9cc3-ff14a70d638e} - будет авторизован пользователь
 * с идентификатором fbd14e52-14e7-4e95-9cc3-ff14a70d638e
 */
@RequiredArgsConstructor
@CommandLine.Command(name = "login", description = "Авторизация пользователя по UUID")
public class Login implements Runnable {

    private final DataSource<User> dataSource;
    private final Context context;
    private final Printer printer;

    @CommandLine.Parameters(description = "UUID пользователя")
    private String uuid;

    @Override
    public void run() {
        var user = dataSource.get(uuid);
        if (user == null) {
            throw new IllegalStateException("Пользователь с таким UUID не найден");
        }

        context.setUser(user);
        context.setSignUpTime(new Date());
        context.setSignUp(true);
        printer.info("Пользователь " + user.getLogin() + " авторизован");
    }
}
