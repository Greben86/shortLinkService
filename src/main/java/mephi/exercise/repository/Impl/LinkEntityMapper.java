package mephi.exercise.repository.Impl;

import lombok.RequiredArgsConstructor;
import mephi.exercise.entity.Link;
import mephi.exercise.repository.EntityMapper;
import mephi.exercise.service.Printer;
import org.apache.commons.lang3.StringUtils;

import java.io.ObjectStreamClass;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static mephi.exercise.Main.DELIMITER;

/**
 * Маппер ссылок
 */
@RequiredArgsConstructor
public class LinkEntityMapper implements EntityMapper<Link> {

    private static final int FIELD_COUNT = Link.class.getDeclaredFields().length;
    private static final String LINK_UID = String.valueOf(ObjectStreamClass.lookup(Link.class).getSerialVersionUID());

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    private final Printer printer;

    /**
     * {@inheritDoc}
     */
    @Override
    public Link map(String line) {
        if (StringUtils.isBlank(line)) {
            return null;
        }

        var splittedLine = line.split(DELIMITER);
        if (!LINK_UID.equals(splittedLine[0])) {
            printer.error("Не совпадает версия объекта " + Link.class.getName());
            return null;
        }

        if (splittedLine.length != FIELD_COUNT) {
            printer.error("Не совпадает количество атрибутов объекта");
            return null;
        }

        try {
            return Link.builder()
                    .setKey(splittedLine[1])
                    .setSourceUrl(splittedLine[2])
                    .setUserKey(splittedLine[3])
                    .setExpireTime(StringUtils.isNotBlank(splittedLine[4]) ? dateFormat.parse(splittedLine[4]) : null)
                    .setExpireCount(Integer.parseInt(splittedLine[5]))
                    .build();
        } catch (ParseException e) {
            printer.error(e.getMessage());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String map(Link link) {
        return new StringBuilder()
                .append(LINK_UID)
                .append(DELIMITER)
                .append(link.getKey())
                .append(DELIMITER)
                .append(link.getSourceUrl())
                .append(DELIMITER)
                .append(link.getUserKey())
                .append(DELIMITER)
                .append(expireTimeFormat(link))
                .append(DELIMITER)
                .append(link.getExpireCount())
                .toString();
    }

    /**
     * Преобразование даты окончания действия ссылки в строку
     *
     * @param link ссылка
     * @return строка с датой в формате {@code yyyy.MM.dd HH:mm:ss} если дата есть, иначе пустая строка
     */
    private String expireTimeFormat(Link link) {
        return link.getExpireTime() != null ? dateFormat.format(link.getExpireTime()) : StringUtils.EMPTY;
    }
}
