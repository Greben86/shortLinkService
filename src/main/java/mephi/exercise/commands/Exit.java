package mephi.exercise.commands;

import lombok.RequiredArgsConstructor;
import mephi.exercise.entity.Context;
import mephi.exercise.service.Printer;
import picocli.CommandLine;

/**
 * Команда завершения сессии пользователя или работы приложения, если сессия уже закрыта
 * Например: {@code exit} - будет выполнен выход из сессии пользователя или из приложения, если нет активной сессии
 * {@code exit -F} - будет выполнен выход из приложения
 */
@RequiredArgsConstructor
@CommandLine.Command(name = "exit", description = "Завершить сессию пользователя или работу приложения")
public class Exit implements Runnable {

    private final Context context;
    private final Printer printer;

    @CommandLine.Option(
            names = {"-F", "--force"},
            description = "Быстрый выход из приложения"
    )
    private boolean force;

    @Override
    public void run() {
        if (force || !context.isSignUp()) {
            printer.info("Завершение работы приложения");
            context.setExitOnly(true);
        } else {
            printer.info("Завершение сессии пользователя " + context.getUser().getLogin());
            context.setUser(null);
            context.setSignUp(false);
        }
    }
}
