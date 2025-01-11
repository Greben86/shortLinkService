package mephi.exercise.service;

import lombok.RequiredArgsConstructor;
import mephi.exercise.entity.Link;

import java.io.PrintStream;

/**
 * Класс умеет печатать короткие ссылки в консоли и простые сообщения
 */
@RequiredArgsConstructor
public class Printer {

    private final PrintStream out;
    private final PrintStream err;
    private final String host;
    private final int port;

    /**
     * Метод печатает пустое сообщение с переводом строки
     */
    public void info() {
        out.println();
    }

    /**
     * Метод печатает простое сообщение с переводом строки
     *
     * @param message сообщение
     */
    public void info(String message) {
        out.println(message);
    }

    /**
     * Метод печатает простое сообщение без перевода строки
     *
     * @param message сообщение
     */
    public void print(String message) {
        out.print(message);
    }

    /**
     * Метод печатает ошибку
     *
     * @param message текст ошибки
     */
    public void error(String message) {
        err.println(message);
    }

    /**
     * Метод печатает короткую ссылку
     *
     * @param link объект ссылки
     */
    public void link(Link link) {
        out.println(link);
        out.printf("http://%s:%d/%s", host, port, link.getKey());
        out.println();
        out.println();
    }
}
