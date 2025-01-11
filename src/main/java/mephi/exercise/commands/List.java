package mephi.exercise.commands;

import lombok.RequiredArgsConstructor;
import mephi.exercise.entity.Context;
import mephi.exercise.entity.Link;
import mephi.exercise.repository.DataSource;
import mephi.exercise.service.Printer;
import picocli.CommandLine;

import java.util.Objects;

/**
 * Команда вывода в консоль всех ссылок пользователя
 * Например: {@code list} - будет выведен список всех коротких ссылок пользователя
 */
@RequiredArgsConstructor
@CommandLine.Command(name = "list", description = "Показать все ссылки пользователя")
public class List implements Runnable {

    private final DataSource<Link> dataSource;
    private final Context context;
    private final Printer printer;

    @Override
    public void run() {
        var links = dataSource.getAll().stream()
                .filter(link -> Objects.equals(context.getUser().getUuid(), link.getUserKey()))
                .toList();
        if (links.isEmpty()) {
            printer.info("Список пуст");
        }
        links.forEach(printer::link);
    }
}
