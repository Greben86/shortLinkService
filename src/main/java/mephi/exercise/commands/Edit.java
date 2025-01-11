package mephi.exercise.commands;

import lombok.RequiredArgsConstructor;
import mephi.exercise.commands.converters.ExpireConverter;
import mephi.exercise.entity.Context;
import mephi.exercise.entity.Link;
import mephi.exercise.repository.DataSource;
import mephi.exercise.service.Printer;
import picocli.CommandLine;

import java.util.Date;
import java.util.Objects;

/**
 * Команда редактирования ссылки пользователя
 * Например: {@code edit ltnrhhcmdd -C 10 -E 1d} - будет обновлена короткая ссылка с ключом ltnrhhcmdd,
 *   по ссылке можно будет перейти 10 раз, и ссылка будет доступна 1 день
 */
@RequiredArgsConstructor
@CommandLine.Command(name = "edit", description = "Редактировать ссылку пользователя")
public class Edit implements Runnable {

    private final DataSource<Link> dataSource;
    private final Context context;
    private final Printer printer;

    @CommandLine.Parameters(description = "Код ссылки")
    private String key;

    @CommandLine.Option(
            names = {"-C", "--count"},
            description = "Количество переходов"
    )
    private int count;

    @CommandLine.Option(
            names = {"-E", "--expire"},
            description = "Какое время живет ссылка (1d 1h 1m)", converter = ExpireConverter.class
    )
    private Date expire;

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

        link.setExpireCount(count > 0 ? count : link.getExpireCount());
        link.setExpireTime(expire != null ? expire : link.getExpireTime());

        printer.print("Ссылка обновлена: ");
        printer.link(link);
    }
}
