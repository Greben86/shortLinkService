package mephi.exercise.service;

import lombok.RequiredArgsConstructor;
import mephi.exercise.commands.Add;
import mephi.exercise.commands.Command;
import mephi.exercise.commands.Edit;
import mephi.exercise.commands.Exit;
import mephi.exercise.commands.Help;
import mephi.exercise.commands.List;
import mephi.exercise.commands.Login;
import mephi.exercise.commands.New;
import mephi.exercise.commands.Remove;
import mephi.exercise.commands.Users;
import mephi.exercise.entity.Context;
import mephi.exercise.entity.Link;
import mephi.exercise.entity.User;
import mephi.exercise.repository.DataSource;
import org.apache.commons.lang3.tuple.Pair;
import picocli.CommandLine;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Callable;

import static mephi.exercise.commands.Command.EMPTY;
import static mephi.exercise.commands.Command.UNKNOWN;

/**
 * Класс обработчик команд пользователя
 */
@CommandLine.Command
@RequiredArgsConstructor
public class CommandProcessor implements Callable<Integer> {

    private final InputStream input;
    private final DataSource<Link> linkDataSource;
    private final DataSource<User> userDataSource;
    private final Printer printer;

    /**
     * Метод реализует главное меню
     *
     * @param scanner чтение вводимых данных
     * @param context контекст выполнения
     */
    private void processWithoutUser(Scanner scanner, Context context) {
        if (context.isSignUp() || context.isExitOnly()) {
            return;
        }

        printer.info("_________________________________________________");
        printer.info("|                     МЕНЮ                      |");
        printer.info("_________________________________________________");
        printer.info("|new  | Регистрация нового пользователя         |");
        printer.info("|login| Войти по UUID пользователя              |");
        printer.info("|users| Показать всех пользователей             |");
        printer.info("|exit | Закрыть приложение                      |");
        printer.info("|help | Помощь                                  |");
        printer.info("_________________________________________________");
        while (!context.isSignUp() && !context.isExitOnly()) {
            try {
                printer.print("Введите команду: ");
                var line = scanner.nextLine();
                var command = parseCommand(line);
                switch (command.getKey()) {
                    case NEW -> new CommandLine(new New(userDataSource, context, printer)).execute(command.getValue());
                    case LOGIN -> new CommandLine(new Login(userDataSource, context, printer)).execute(command.getValue());
                    case USERS -> new CommandLine(new Users(userDataSource, context, printer)).execute(command.getValue());
                    case EXIT -> new CommandLine(new Exit(context, printer)).execute(command.getValue());
                    case HELP -> new CommandLine(new Help(context, printer)).execute(command.getValue());
                    case EMPTY -> printer.error("Пустая команда");
                    case UNKNOWN -> printer.error("Неизвестная команда " + line);
                    default -> printer.error("Команда \"" + command.getKey() + "\" не поддерживается в этом меню");
                }
            } catch (Exception ex) {
                printer.error(ex.getMessage());
            }
        }
    }

    /**
     * Метод реализует процесс обработки вводимых команд авторизованного пользователя
     *
     * @param scanner чтение вводимых данных
     * @param context контекст выполнения
     */
    private void processUser(Scanner scanner, Context context) {
        if (!context.isSignUp() || context.isExitOnly()) {
            return;
        }

        printer.info("_________________________________________________");
        printer.info("|                     МЕНЮ                      |");
        printer.info("_________________________________________________");
        printer.info("|add   | Добавить новую ссылку                  |");
        printer.info("|edit  | Редактировать ссылку                   |");
        printer.info("|list  | Показать все ссылки пользователя       |");
        printer.info("|remove| Удалить ссылку                         |");
        printer.info("|exit  | Выйти из сессии пользователя           |");
        printer.info("|help  | Помощь                                 |");
        printer.info("_________________________________________________");
        while (context.isSignUp() && !context.isExitOnly()) {
            try {
                printer.print("Введите команду (используйте help чтобы посмотреть список команд): ");
                var line = scanner.nextLine();
                var command = parseCommand(line);
                switch (command.getKey()) {
                    case ADD -> new CommandLine(new Add(linkDataSource, context, printer)).execute(command.getValue());
                    case EDIT -> new CommandLine(new Edit(linkDataSource, context, printer)).execute(command.getValue());
                    case LIST -> new CommandLine(new List(linkDataSource, context, printer)).execute(command.getValue());
                    case REMOVE -> new CommandLine(new Remove(linkDataSource, context, printer)).execute(command.getValue());
                    case EXIT -> new CommandLine(new Exit(context, printer)).execute(command.getValue());
                    case HELP -> new CommandLine(new Help(context, printer)).execute(command.getValue());
                    case EMPTY -> printer.error("Пустая команда");
                    case UNKNOWN -> printer.error("Неизвестная команда " + line);
                    default -> printer.error("Команда \"" + command.getKey() + "\" не поддерживается в этом меню");
                }
            } catch (Exception ex) {
                printer.error(ex.getMessage());
            }
        }
    }

    /**
     * Парсинг введенной команды, разрезаем ввод на массив по пробелам и первый элемент - это команда, а остальное аргументы
     *
     * @param line введенная команда
     * @return пара: команда и аргументы
     */
    private Pair<Command, String[]> parseCommand(String line) {
        if (line == null || line.isBlank()) {
            return Pair.of(EMPTY, null);
        }

        var lineAsArray = line.split(" ");
        var command = Command.getByName(lineAsArray[0]).orElse(UNKNOWN);
        var args = Arrays.copyOfRange(lineAsArray, 1, lineAsArray.length);
        return Pair.of(command, args);
    }

    /**
     * Старт обработчика
     *
     * @return 0 если все хорошо, или -1, если ошибка
     */
    @Override
    public Integer call() {
        var context = new Context();

        printer.info();
        try (var scanner = new Scanner(input)) {
            while (!context.isExitOnly()) {
                // Процесс без пользователя
                processWithoutUser(scanner, context);
                // Процесс с авторизованным пользователем
                processUser(scanner, context);
            }

            return 0;
        } catch (Exception e) {
            printer.error(e.getMessage());
            return -1;
        }
    }
}
