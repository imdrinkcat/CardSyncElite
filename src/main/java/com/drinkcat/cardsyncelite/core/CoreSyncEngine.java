package com.drinkcat.cardsyncelite.core;

import com.drinkcat.cardsyncelite.module.SyncRule;
import com.drinkcat.cardsyncelite.module.SyncTask;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoreSyncEngine {
    /*
      所需参数
      1. 搜索目录
      2. 目标目录
      3. 文件类型 / 拓展名
      选项控制
      1. 是否需要检验
      2. 是否维持原目录架构
      3. 是否更改名称
      4. 线程数量
      */

    private boolean needCheck;
    private boolean maintainStructure;
    private boolean needRename;
    private int maxThreads;
    private List<String> extensions;
    private Path source;
    private List<Path> sourceFiles;

    public CoreSyncEngine(SyncTask task) {
        this.source = task.getSource();
        this.maxThreads = task.getMaxThreads();
    }
    public CoreSyncEngine(Path source) {
        this.source = source;
    }

    private void search() {
        List<Path> searchFiles = null;
        try {
            searchFiles = CoreSearchEngine.searchTask(source, extensions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(searchFiles == null) sourceFiles = new ArrayList<>();
        else sourceFiles = searchFiles;
    }
    private SyncTask syncTask;
    public void copyAndCheck(SyncRule rule, SyncTask syncTask) {
        this.needCheck = rule.isNeedCheck();
        this.syncTask = syncTask;
        this.needRename = rule.isNeedRename();
        this.maintainStructure = rule.isMaintainStructure();
        this.extensions = rule.getExtensions();
        try {
            search();
            rule.setInfoText("正在搜索中...");
            if(syncTask.stopFlag) return;
            copyAndCheck(rule, rule.getTarget());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyAndCheck(SyncRule rule, Path target) throws Exception {
        copyAndCheck(rule, sourceFiles, target, 0);
    }
    public void copyAndCheck(SyncRule rule, List<Path> files, Path target, int cnt) throws Exception {
        if(cnt == 3) return;
        if(syncTask.stopFlag) return;
        // 复制
        rule.setInfoText("复制中...");
        Map<File, File> successFiles = null;
        try {
            successFiles = CoreCopyEngine.copyTask(syncTask, rule, files, target, this.maxThreads, this.maintainStructure, this.needRename);
        } catch (InterruptedException e) {
            throw new Exception("复制进程被异常中断");
        }

        if(syncTask.stopFlag) return;
        // 校验
        if(!needCheck) return;
        rule.setInfoText("检验中...");
        var crcErrFiles = CoreCheckEngine.checkTask(syncTask, rule, successFiles, this.maxThreads);
        if(syncTask.stopFlag) return;
        if(!crcErrFiles.isEmpty()) copyAndCheck(rule, crcErrFiles, target, cnt+1);
        else System.out.println("校验成功!");
    }

    public boolean isNeedCheck() {
        return needCheck;
    }

    public void setNeedCheck(boolean needCheck) {
        this.needCheck = needCheck;
    }

    public boolean isMaintainStructure() {
        return maintainStructure;
    }

    public void setMaintainStructure(boolean maintainStructure) {
        this.maintainStructure = maintainStructure;
    }

    public boolean isNeedRename() {
        return needRename;
    }

    public void setNeedRename(boolean needRename) {
        this.needRename = needRename;
    }
    public Path getSource() {
        return source;
    }

    public void setSource(Path source) {
        this.source = source;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }
}
