package mephi.exercise.commands.converters;

import org.apache.commons.lang3.time.DateUtils;
import picocli.CommandLine;

import java.util.Date;

import static mephi.exercise.Main.DELIMITER;

/**
 * Конвертер вводимого времени, введенная строка конвертируется в прибавление к текущей дате дней, часов, минут.
 * В результате получается дата в будущем, когда ссылка перестает быть активной
 * Например:
 *   2d - добавить 2 дня
 *   5h - добавить 5 часов
 *   45m - добавить 45 минут
 * Можно комбинировать значения, разделяя их запятой, например:
 *   1d,3h,30m - добавить день, 3 часа и 30 минут
 *   1h,40m - добавить час и 40 минут
 */
public class ExpireConverter implements CommandLine.ITypeConverter<Date> {

    @Override
    public Date convert(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }

        var parts = s.split(DELIMITER);

        var expireDate = new Date();
        int skiped = 0;
        for (var part : parts) {
            if (part.endsWith("m")) {
                int minutes = Integer.parseInt(part.replace("m", ""));
                expireDate = DateUtils.addMinutes(expireDate, minutes);
                continue;
            }

            if (part.endsWith("h")) {
                int hours = Integer.parseInt(part.replace("h", ""));
                expireDate = DateUtils.addHours(expireDate, hours);
                continue;
            }

            if (part.endsWith("d")) {
                int days = Integer.parseInt(part.replace("d", ""));
                expireDate = DateUtils.addDays(expireDate, days);
                continue;
            }

            skiped++;
        }

        return skiped< parts.length ? expireDate : null;
    }
}
