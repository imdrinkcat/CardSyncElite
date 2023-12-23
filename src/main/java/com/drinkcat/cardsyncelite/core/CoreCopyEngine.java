package com.drinkcat.cardsyncelite.core;

import com.drinkcat.cardsyncelite.module.SyncRule;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class CoreCopyEngine {

    private final ScheduledThreadPoolExecutor executorService;
    private final Long totalFiles;
    private List<File> errFiles;
    private Map<File, File> successFiles;
    public CoreCopyEngine(int poolSize, Long size) {
        executorService = new ScheduledThreadPoolExecutor(poolSize);
        this.totalFiles = size;
        errFiles = new ArrayList<>();
        successFiles = new HashMap<>();
    }
    private void shutdownThreadPool() {
        this.executorService.shutdown(); // 在程序结束时记得关闭线程池
    }
    private void copyFileAsync(SyncRule rule, File source, File targetDic, String fileName, CountDownLatch latch, boolean maintainStructure) {
        executorService.execute(() -> {
            try {
                validateSourceAndTarget(source, targetDic);
                validateFileName(fileName);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                errFiles.add(source);
                latch.countDown();
                return;
            }

            // 是否保持原来的目录结构
            File target = new File(targetDic.getAbsolutePath() + File.separator + fileName);

            // System.out.println("拷贝到" + target.getAbsolutePath());
            try (var inputStream = new FileInputStream(source);
                 var fIn = inputStream.getChannel();
                 var outputStream = new FileOutputStream(target);
                 var fOut = outputStream.getChannel()) {

                long transferred = 0L;
                long size = fIn.size();

                while (transferred != size) {
                    long delta = fIn.transferTo(transferred, size - transferred, fOut);
                    transferred += delta;
                }
                successFiles.put(source, target);
                System.out.println("拷贝完成" + target.getAbsolutePath());
                rule.setProgress((double) ((totalFiles - latch.getCount()) / latch.getCount()));
            } catch (IOException e) {
                System.err.println("拷贝文件时发生错误: " + e.getMessage());
                errFiles.add(source);
            } finally {
            latch.countDown(); // 无论如何都要减少计数
        }
        });
    }
    private static void validateSourceAndTarget(File source, File targetDic) throws Exception {
        if (!source.exists() || !source.isFile()) {
            throw new IllegalArgumentException("源文件不存在或不是一个文件!");
        }

        // 判断目标路径是否存在
        if (!targetDic.exists()) {
            try {
                // 创建目录及其父目录
                Files.createDirectories(targetDic.toPath());
            } catch (IOException e) {
                throw new Exception("无法创建目录: " + e.getMessage());
            }
        }
    }
    private static void validateFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空!");
        }

        if (fileName.contains(File.separator)) {
            throw new IllegalArgumentException("文件名不能包含目录分隔符!");
        }
    }
    private CountDownLatch latch;
    public static Map<File, File> copyTask(SyncRule rule, List<Path> files, Path target, int maxThreads, boolean maintainStructure, boolean needRename) throws Exception {
        CoreCopyEngine copyEngine = new CoreCopyEngine(maxThreads, (long) files.size());

        copyEngine.latch = new CountDownLatch(files.size());
        long startTime = System.currentTimeMillis();

        // 开始复制操作
        System.out.println("开始复制...");
        rule.setProgress(0.0);
        files.forEach(file -> copyEngine.copyFileAsync(rule, file.toFile(), target.toFile(), file.getFileName().toString(), copyEngine.latch, maintainStructure));
        copyEngine.latch.await();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("所有文件拷贝任务完成！");
        var progress = copyEngine.getProgress();
        System.out.println("总文件数: " + progress[0]);
        System.out.println("成功文件数: " + progress[1]);
        System.out.println("失败文件数: " + progress[2]);
        System.out.println("运行时长: " + duration / 1000 + " 秒");

        if(!copyEngine.errFiles.isEmpty()) {
            copyEngine.errFilesHandler(rule, copyEngine.errFiles, target, maintainStructure);
        }

        copyEngine.shutdownThreadPool();
        return copyEngine.getSuccessFiles();
    }
    private void errFilesHandler(SyncRule rule, List<File> files, Path target, boolean maintainStructure) throws InterruptedException {
        int cnt = 0;
        while(!errFiles.isEmpty()) {
            if(cnt == 3) break;
            errFiles = new ArrayList<>();
            this.latch = new CountDownLatch(files.size());
            files.forEach(file -> this.copyFileAsync(rule, file, target.toFile(), file.getName(), this.latch, maintainStructure));
            latch.await();
            cnt++;
        }
    }
    public Long[] getProgress() {
        Long[] ret = new Long[3];
        ret[0] = totalFiles;    // 总文件
        ret[1] = totalFiles - latch.getCount() - (long) errFiles.size();   // 成功文件
        ret[2] = (long) errFiles.size();   // 失败文件
        return ret;
    }
    public List<File> getErrFiles() {
        return errFiles;
    }
    public Map<File, File> getSuccessFiles() {
        return successFiles;
    }
}
