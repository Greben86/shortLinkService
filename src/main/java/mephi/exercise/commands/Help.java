package mephi.exercise.commands;

import lombok.RequiredArgsConstructor;
import mephi.exercise.entity.Context;
import mephi.exercise.service.Printer;
import picocli.CommandLine;

/**
 * Команда вывода в консоль всех доступных команд
 * Например: {@code help} - будут показаны все доступные команды в виде списка
 */
@RequiredArgsConstructor
@CommandLine.Command(name = "help", description = "Показать все доступные команды")
public class Help implements Runnable {

    private final Context context;
    private final Printer printer;

    @Override
    public void run() {
        if (context.isSignUp()) {
            printer.info("add - Добавить новую ссылку, например \"add ya.ru -C 10 -E 1d\" -> будет добавлена короткая " +
                    "ссылка для перехода на ya.ru, по ссылке можно будет перейти 10 раз, и ссылка будет доступна 1 день");
            printer.info("edit - Редактировать ссылку пользователя, например \"edit ltnrhhcmdd -C 10 -E 1d\" -> будет " +
                    "обновлена короткая ссылка с ключом ltnrhhcmdd, по ссылке можно будет перейти 10 раз, и ссылка будет " +
                    "доступна 1 день");
            printer.info("list - Показать все ссылки пользователя, например \"list\" -> будет выведен список всех " +
                    "коротких ссылок пользователя");
            printer.info("remove - Удалить ссылку пользователя, например \"remove ltnrhhcmdd\" -> будет удалена " +
                    "короткая ссылка с ключом ltnrhhcmdd");
        } else {
            printer.info("new - Регистрация нового пользователя, например \"new Viktor\" -> будет зарегистрирован " +
                    "пользователь с логином Viktor. Новому пользователю будет присвоен и выведен уникальный UUID, " +
                    "по нему будет выполняться авторизация пользователя");
            printer.info("login - Авторизация пользователя по UUID, например " +
                    "\"login fbd14e52-14e7-4e95-9cc3-ff14a70d638e\" -> будет авторизован пользователь с " +
                    "идентификатором fbd14e52-14e7-4e95-9cc3-ff14a70d638e");
            printer.info("users - Показать всех пользователей, например \"users\" -> будет выведен список всех пользователей");
        }
        printer.info("help - Показать все доступные команды, например \"help\" -> будут показаны все доступные " +
                "команды в виде списка");
        printer.info("exit - Завершить сессию пользователя или работу приложения, например \"exit\" -> будет " +
                "выполнен выход из сессии пользователя или завершено приложение, если нет активной сессии; " +
                "\"exit -F\" - будет выполнен выход из приложения");
    }
}
