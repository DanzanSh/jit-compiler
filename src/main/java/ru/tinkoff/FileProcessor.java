package ru.tinkoff;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileProcessor implements Runnable {
    private final Path inputFile;
    private final Path outputFile;
    private final Integer counter;

    public FileProcessor(Path inputFile, Path outputFile, Integer counter) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.counter = counter;
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        try {
            processFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Total execution time: " + (System.nanoTime() - start));
    }

    private void processFile() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(inputFile);
             BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (int i = 0; i < counter * 3000; i++) {
                    String processedLine = computationallyIntensiveTask(line);
                    writer.write(processedLine);
                    writer.newLine();
                }
            }
        }
    }

    private String computationallyIntensiveTask(String line) {
        StringBuilder sb = new StringBuilder(line);
        sb.reverse();
        return sb.toString().toUpperCase();
    }
}
