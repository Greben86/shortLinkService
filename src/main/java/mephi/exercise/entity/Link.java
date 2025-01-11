package mephi.exercise.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Объект ссылки
 */
@AllArgsConstructor
@Builder(setterPrefix = "set")
@Getter
@EqualsAndHashCode(of = {"sourceUrl", "userKey"})
public final class Link implements Serializable {

    @Serial
    private static final long serialVersionUID = -3818234891051946383L;

    private final String key;
    private final String sourceUrl;
    // Храню значение идентификатора пользователя, а не самого пользователя
    // так как это упрощает проверку ссылочной целостности при сохранении в файл и чтении из файла
    private final String userKey;
    @Setter
    private Date expireTime;
    @Setter
    private int expireCount;

    @Override
    public String toString() {
        var builder = new StringBuilder()
                .append(key).append(" => [")
                .append("URL: \"").append(sourceUrl).append("\"");
        if (expireTime != null) {
            builder.append(", Time: \"").append(expireTime).append("\"");
        }
        if (expireCount >= 0) {
            builder.append(", Count: \"").append(expireCount).append("\"");
        }
        builder.append("]");
        return builder.toString();
    }
}
