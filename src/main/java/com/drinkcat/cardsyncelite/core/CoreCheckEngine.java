package com.drinkcat.cardsyncelite.core;
import com.drinkcat.cardsyncelite.module.SyncRule;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class CoreCheckEngine {
    private static String getMD5(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
        }
        byte[] b = md.digest();
        System.out.println(file + ": " + new BigInteger(1, b).toString(16));
        return new BigInteger(1, b).toString(16);
    }
    public static List<Path> checkTask(SyncRule rule, Map<File, File> files, int maxThreads) {
        System.out.println("开始文件校验");
        int fileCount = files.size() * 2;
        int completed = 0;
        rule.setProgress(0.0);
        List<Path> crcErrFiles = new ArrayList<>();
        Map<File, String> md5Map = new HashMap<>();

        List<CompletableFuture<String>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);

        files.forEach((source, target) -> {
            createFutureTask(md5Map, futures, executor, source);
            createFutureTask(md5Map, futures, executor, target);
        });

        // 创建一个包含所有 CompletableFuture 的数组
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0]));

        AtomicInteger completedCount = new AtomicInteger(0); // 使用 AtomicInteger 保证原子性

        futures.forEach(x -> {
            x.whenComplete((ret, e) -> {
                // 在 CompletableFuture 完成时更新进度
                int currentCount = completedCount.incrementAndGet();
                double progress = (double) currentCount / fileCount;
                rule.setProgress(progress);
            });
        });

        // 在主线程中等待所有任务完成
        allOf.join();

        // 检验MD5
        files.forEach((source, target) -> {
            if(md5Map.get(source).equals("ERROR")
                    || md5Map.get(target).equals("ERROR")
                    || !md5Map.get(source).equals(md5Map.get(target))) {
                System.out.println(source + "校验异常");
                crcErrFiles.add(source.toPath());
            }
        });
        // 关闭线程池
        executor.shutdown();
        return crcErrFiles;
    }
    private static void createFutureTask(Map<File, String> md5Map, List<CompletableFuture<String>> futures, ExecutorService executor, File source) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                return getMD5(source);
            } catch (NoSuchAlgorithmException | IOException e) {
                return "ERROR";
            }
        }, executor);
        future.whenComplete((md5Value, e) -> {
            md5Map.put(source, md5Value);
        });
        futures.add(future);
    }
}
