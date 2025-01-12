package mephi.exercise.commands.converters;

import picocli.CommandLine;

import java.io.File;

public class FileConverter implements CommandLine.ITypeConverter<File> {

    @Override
    public File convert(String s) {
        var file = new File(s);
        if (file.exists() && file.isFile()) {
            return file;
        }

        return null;
    }
}
