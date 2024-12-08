package dev.marylfo.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class FileService {

    public static List<String> getLines(String fileName) {
        try {
            Path path = Paths.get(Objects.requireNonNull(FileService.class.getClassLoader().getResource(fileName)).getPath());
            return Files.readAllLines(path);
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }

        return null;
    }

}
