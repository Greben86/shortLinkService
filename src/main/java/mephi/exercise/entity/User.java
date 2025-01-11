package mephi.exercise.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Объект пользователя
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "set")
@Setter
@Getter
@EqualsAndHashCode()
public final class User implements Serializable {

    @Serial
    private static final long serialVersionUID = -3115729263766508225L;

    private String uuid;
    private String login;

    @Override
    public String toString() {
        var builder = new StringBuilder()
                .append(login).append(" => [")
                .append("UUID: \"").append(uuid).append("\"")
                .append("]");
        return builder.toString();
    }
}
