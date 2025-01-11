package mephi.exercise.commands;

import lombok.RequiredArgsConstructor;
import mephi.exercise.commands.converters.ExpireConverter;
import mephi.exercise.entity.Context;
import mephi.exercise.entity.Link;
import mephi.exercise.repository.DataSource;
import mephi.exercise.service.Printer;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

import java.util.Date;
import java.util.Random;

import static mephi.exercise.Main.DELIMITER;

/**
 * Команда добавления ссылки пользователя
 * Например: {@code add ya.ru -C 10 -E 1d} - будет добавлена короткая ссылка для перехода на ya.ru,
 *   по ссылке можно будет перейти 10 раз, и ссылка будет доступна 1 день
 */
@RequiredArgsConstructor
@CommandLine.Command(name = "add", description = "Добавить новую ссылку")
public class Add implements Runnable {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final int LENGTH_OF_KEY = 10;
    private final Random random = new Random();

    private final DataSource<Link> dataSource;
    private final Context context;
    private final Printer printer;

    @CommandLine.Parameters(description = "Исходная ссылка")
    private String url;

    @CommandLine.Option(
            names = {"-C", "--count"},
            description = "Количество переходов"
    )
    private int count;

    @CommandLine.Option(
            names = {"-E", "--expire"},
            description = "Какое время живет ссылка (1d 1h 1m)",
            converter = ExpireConverter.class
    )
    private Date expire;

    @Override
    public void run() {
        // Если не соблюдать это требование, то такой URL сломает сохранение и последующее чтение
        if (StringUtils.contains(url, DELIMITER)) {
            printer.error("URL не может содержать символ " + DELIMITER);
            return;
        }

        // Если не соблюдать это требование, то такой URL тоже сломает сохранение и последующее чтение
        if (StringUtils.contains(url, StringUtils.SPACE)) {
            printer.error("URL не может содержать символ пробела");
            return;
        }

        var key = generateKey();
        var link = Link.builder()
                .setKey(key)
                .setUserKey(context.getUser().getUuid())
                .setSourceUrl(url)
                .setExpireCount(count > 0 ? count : -1)
                .setExpireTime(expire)
                .build();
        dataSource.add(key, link);

        printer.print("Ссылка сохранена: ");
        printer.link(link);
    }

    /**
     * Метод формирования уникального кода ссылки для короткой ссылки
     * Так как разные пользователи могут сохранить одну и ту же ссылку и при этом короткие ссылки не должны совпадать,
     * по этому в принципе не имеет смысла получать код из самой ссылки
     * Каждый символ кода выбирается случайным образом, после чего код проверяется на совпадение с кодами уже
     * сохраненных ссылок, если совпадений нет, то код принимается, если нет - пробуем еще раз
     *
     * @return
     */
    private String generateKey() {
        String key;
        do {
            // Генерация строки случайным образом
            var sb = new StringBuilder();
            for (int i = 0; i < LENGTH_OF_KEY; i++) {
                sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            }
            key = sb.toString();
        } while (dataSource.containsKey(key));

        return key;
    }
}
