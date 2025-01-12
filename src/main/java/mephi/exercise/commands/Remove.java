package mephi.exercise.commands;

import lombok.RequiredArgsConstructor;
import mephi.exercise.entity.Context;
import mephi.exercise.entity.Link;
import mephi.exercise.repository.DataSource;
import mephi.exercise.service.Printer;
import picocli.CommandLine;

import java.util.Objects;

/**
 * Команда редактирования ссылки пользователя
 * Например: {@code remove ltnrhhcmdd} - будет удалена короткая ссылка с ключом ltnrhhcmdd
 */
@RequiredArgsConstructor
@CommandLine.Command(name = "remove", description = "Удалить ссылку пользователя")
public class Remove implements Runnable {

    private final DataSource<Link> dataSource;
    private final Context context;
    private final Printer printer;

    @CommandLine.Parameters(description = "Ключ ссылки")
    private String key;

    @Override
    public void run() {
        if (key == null || key.isBlank()) {
            printer.error("Не передан ключ ссылки");
            return;
        }

        var link = dataSource.get(key);
        if (link == null || !Objects.equals(context.getUser().getUuid(), link.getUserKey())) {
            printer.error("Ссылка пользователя по ключу не найдена");
            return;
        }

        dataSource.remove(key);
        printer.info("Ссылка удалена");
    }
}
