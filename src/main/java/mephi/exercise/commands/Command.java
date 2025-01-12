package mephi.exercise.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Список всех команд
 */
@RequiredArgsConstructor
public enum Command {
    NEW("new"),
    LOGIN("login"),
    USERS("users"),
    ADD("add"),
    EDIT("edit"),
    LIST("list"),
    REMOVE("remove"),
    HELP("help"),
    EXIT("exit"),
    EMPTY(""),
    UNKNOWN(null);

    @Getter
    private final String name;

    private static final Map<String, Command> commandMap = Stream.of(values())
            .collect(Collectors.toMap(Command::getName, Function.identity()));

    /**
     * Получить команду по названию
     *
     * @param name название команды
     * @return команда, найденная по названию или null, если такой команды нет
     */
    public static Optional<Command> getByName(String name) {
        return Optional.ofNullable(commandMap.get(name));
    }
}
