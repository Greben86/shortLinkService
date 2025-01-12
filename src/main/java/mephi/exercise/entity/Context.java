package mephi.exercise.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Контекст выполнения
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public final class Context {
    private boolean signUp = false;
    private User user;
    private Date signUpTime;
    private boolean exitOnly = false;
}
