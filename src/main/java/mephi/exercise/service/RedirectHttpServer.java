package mephi.exercise.service;

import lombok.RequiredArgsConstructor;
import mephi.exercise.entity.Link;
import mephi.exercise.repository.DataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Простой перенаправляющий HTTP-сервер
 */
@RequiredArgsConstructor
public class RedirectHttpServer implements Runnable {

    private final DataSource<Link> linkDataSource;

    // Используемый порт
    private final int port;

    /**
     * Метод стартует сервер в потоке-демоне
     */
    public void start() {
        var service = Executors.newSingleThreadExecutor(runnable -> {
            var thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setDaemon(true);
            return thread;
        });
        service.execute(this);
    }

    /**
     * Метод содержит логику работы сервера
     */
    @Override
    public void run() {
        try (var serverSocket = new ServerSocket(port)) {
            while (true) {
                // ожидаем подключения
                var socket = serverSocket.accept();

                // для подключившегося клиента открываем потоки
                // чтения и записи
                try (var input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                     var output = new PrintWriter(socket.getOutputStream())) {

                    // ждем первой строки запроса
                    while (!input.ready()) {
                        Thread.sleep(TimeUnit.MILLISECONDS.toMillis(100));
                    }

                    // считываем все что было отправлено клиентом
                    String url = null;
                    while (input.ready()) {
                        var line = input.readLine();
                        if (line.startsWith("GET")) {
                            url = line.split(" ")[1].trim();
                        }
                    }

                    if (url == null) {
                        printOutput(output, "Метод не поддерживается");
                        continue;
                    }

                    var key = url.substring(1);
                    var link = linkDataSource.get(key);
                    if (link == null) {
                        printOutput(output, "Ссылка не найдена");
                        continue;
                    }

                    if (link.getExpireTime() != null && link.getExpireTime().before(new Date())) {
                        printOutput(output, "Ссылка устарела");
                        linkDataSource.remove(key);
                        continue;
                    }

                    if (link.getExpireCount() == 0) {
                        printOutput(output, "Количество переходов исчерпано");
                        linkDataSource.remove(key);
                        continue;
                    }

                    // перенаправляем
                    redirect(output, link.getSourceUrl());

                    // Декремент счетчика переходов
                    synchronized (link) {
                        if (link.getExpireCount() != -1) {
                            link.setExpireCount(link.getExpireCount()-1);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    // отправляем ответ с сообщением
    private void printOutput(PrintWriter output, String message) {
        output.println("HTTP/1.1 200 OK");
        output.println("Content-Type: text/html; charset=utf-8");
        output.println();
        output.printf("<h1>%s</h1>", message);
        output.flush();
    }

    // отправляем ответ с перенаправлением
    private void redirect(PrintWriter output, String url) {
        output.println("HTTP/1.1 302");
        output.printf("Location: %s", url.startsWith("http") ? url : "https://"+url);
        output.println();
        output.flush();
    }
}