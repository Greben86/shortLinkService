package mephi.exercise;

import mephi.exercise.commands.converters.FileConverter;
import mephi.exercise.entity.Link;
import mephi.exercise.entity.User;
import mephi.exercise.repository.Impl.DataConnectorImpl;
import mephi.exercise.repository.Impl.LinkDataSource;
import mephi.exercise.repository.Impl.LinkEntityMapper;
import mephi.exercise.repository.Impl.UserDataSource;
import mephi.exercise.repository.Impl.UserEntityMapper;
import mephi.exercise.service.CommandProcessor;
import mephi.exercise.service.RedirectHttpServer;
import mephi.exercise.service.Printer;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Простой сервис коротких ссылок, короткие ссылки только с протоколом http
 * Класс имплементирует интерфейс {@link Callable} чтобы реализовать ожидание завершения выполнения потока командного процессора
 */
@CommandLine.Command
public class Main implements Callable<Integer> {

    // Разделитель
    public static final String DELIMITER = ";";
    // Хост по умолчанию
    private static final String DEFAULT_HOST = "localhost";
    // Порт по умолчанию
    private static final String DEFAULT_PORT = "8080";

    /**
     * Метод-точка входа в приложение, запускает первую команду, которую реализует этот класс
     *
     * @param args <b>переданные аргументы:</b>
     *             <p>-H, --host - хост, если хост не указан, то будет использован хост по умолчанию - localhost</p>
     *             <p>-P, --port - порт, если порт не указан, то будет использован порт по умолчанию - 8080</p>
     *             <p>-F, --file - файл хранения данных, в этом фале будут сохранены пользователи и ссылки для последующего чтения</p>
     */
    public static void main(String[] args) {
        new CommandLine(new Main()).execute(args);
    }

    @CommandLine.Option(
            names = {"-H", "--host"},
            description = "Используемый хост",
            defaultValue = DEFAULT_HOST
    )
    private String host;

    @CommandLine.Option(
            names = {"-P", "--port"},
            description = "Используемый порт",
            defaultValue = DEFAULT_PORT
    )
    private int port;

    @CommandLine.Option(
            names = {"-F", "--file"},
            description = "Файл хранения данных",
            converter = FileConverter.class
    )
    private File file;

    @CommandLine.Option(
            names = {"-R", "--readonly"},
            description = "Только чтение файла"
    )
    private boolean readOnly;

    @Override
    public Integer call() {
        var printer = new Printer(System.out, System.err, host, port);

        // Чтение файла и загрузка объектов в память
        var dataConnector = new DataConnectorImpl(printer, file);
        dataConnector.loadFile();

        var userEntityMapper = new UserEntityMapper(printer);
        var userDataSource = new UserDataSource(dataConnector.readEntities(User.class, userEntityMapper));
        var linkEntityMapper = new LinkEntityMapper(printer);
        var linkDataSource = new LinkDataSource(dataConnector.readEntities(Link.class, linkEntityMapper));

        // Старт перенаправляющего сервера
        new RedirectHttpServer(linkDataSource, port).start();

        // Старт командного процессора
        int result = new CommandLine(new CommandProcessor(System.in, linkDataSource, userDataSource, printer)).execute();

        // Сохранение данных в файл перед выходом из приложения,
        // если выполнение не завершилось с ошибкой и не активирован режим только для чтения
        if (result == 0 && !readOnly) {
            dataConnector.clearFile();
            dataConnector.saveEntities(User.class, userEntityMapper, userDataSource.getAll());
            dataConnector.saveEntities(Link.class, linkEntityMapper, linkDataSource.getAll());
        }

        return result;
    }
}