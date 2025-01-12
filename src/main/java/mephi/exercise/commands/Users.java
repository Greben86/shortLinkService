package mephi.exercise.commands;

import lombok.RequiredArgsConstructor;
import mephi.exercise.entity.Context;
import mephi.exercise.entity.User;
import mephi.exercise.repository.DataSource;
import mephi.exercise.service.Printer;
import picocli.CommandLine;

/**
 * Команда вывода в консоль всех пользователей
 * Например: {@code users} - будет выведен список всех пользователей
 */
@RequiredArgsConstructor
@CommandLine.Command(name = "users", description = "Показать всех пользователей")
public class Users implements Runnable {

    private final DataSource<User> dataSource;
    private final Context context;
    private final Printer printer;

    @Override
    public void run() {
        var users = dataSource.getAll().stream()
                .toList();
        if (users.isEmpty()) {
            printer.info("Список пуст");
        }

        users.forEach(user -> printer.info(user.equals(context.getUser()) ? "* " + user : user.toString()));
        printer.info();
    }
}
