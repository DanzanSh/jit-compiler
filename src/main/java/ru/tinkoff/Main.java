package ru.tinkoff;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class Main {

    private static final ExecutorService threadPool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        ioIntensive();
    }

    private static void ioIntensive() throws IOException {
        Path inputDir = Paths.get( "src","main","resources","input");
        Path outputDir = Paths.get( "src","main","resources","output");

        for (int i = 1; i < 500; i++) {
            try (Stream<Path> files = Files.list(inputDir)) {
                int finalI = i;
                files.forEach(inputFile -> {
                    String outputFileName = finalI + "_" + inputFile.getFileName().toString();
                    Path outputFile = outputDir.resolve(outputFileName);
                    FileProcessor fileProcessor = new FileProcessor(inputFile, outputFile, finalI);
                    threadPool.submit(fileProcessor);
                });
            }
        }
    }

    private static void cpuIntensive() throws ExecutionException, InterruptedException {
        for (int i = 1; i <= 10; i++) {
            long start = System.currentTimeMillis();
            List<Future<Double>> futures = new ArrayList<>();

            for (int j = 0; j < i * 20_000; j++) {
                final int k = j;
                futures.add(CompletableFuture.supplyAsync(
                        () -> count(k),
                        threadPool
                ));
            }

            double value = 0;
            for (Future<Double> future : futures) {
                value += future.get();
            }

            System.out.println(value);
            System.out.println("Elapsed time: " + (System.currentTimeMillis() - start));
        }
    }

    private static double count(double a) {
        for (int i = 0; i < 5000; i++) {
            a = a + Math.tan(a) + Math.sin(a) + Math.cos(a);
        }
        return a;
    }
}