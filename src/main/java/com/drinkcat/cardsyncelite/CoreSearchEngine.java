package com.drinkcat.cardsyncelite;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CoreSearchEngine {
    private static CompletableFuture<List<Path>> searchFilesByExtensionAsync(Path directoryPath, List<String> extensions) {
        return CompletableFuture.supplyAsync(() -> {
            List<Path> resultFiles = new ArrayList<>();
            try {
                Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (hasMatchingExtension(file, extensions)) {
                            resultFiles.add(file);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException("文件访问异常");
            }
            return resultFiles;
        });
    }
    private static boolean hasMatchingExtension(Path file, List<String> extensions) {
        String fileName = file.getFileName().toString().toLowerCase();
        for (String extension : extensions) {
            if (fileName.endsWith(extension.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    public static List<Path> searchTask(Path source, List<String> extensions) throws Exception {
        // 搜索
        CompletableFuture<List<Path>> searchFilesFuture = CoreSearchEngine.searchFilesByExtensionAsync(source, extensions);
        System.out.println("正在搜索中...");

        searchFilesFuture.exceptionally(ex -> {
            System.out.println("文件搜索时发生错误: " + ex.getMessage());
            return null;
        });

        try {
            return searchFilesFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new Exception("文件搜索时发生未知错误。");
        }
    }
}