package com.drinkcat.cardsyncelite.module;

import com.drinkcat.cardsyncelite.controller.MainController;
import com.drinkcat.cardsyncelite.core.CoreSyncEngine;
import com.drinkcat.cardsyncelite.util.DataStoreUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

public class SyncTask {
    private List<SyncRule> syncRules = new ArrayList<>();
    private final SimpleStringProperty taskNameProperty;
    private final SimpleIntegerProperty maxThreadsProperty;
    private final SimpleObjectProperty sourceProperty;
    private int taskID;
    private ChangeListener<Path> sourceListener;
    private ChangeListener<String> taskNameListener;
    private ChangeListener<Number> maxThreadsListener;

    public SyncTask() {
        taskNameProperty = new SimpleStringProperty();
        maxThreadsProperty = new SimpleIntegerProperty();
        sourceProperty = new SimpleObjectProperty<Path>();
    }

    public void startListening() {
        taskNameListener = (observable, oldValue, newValue) -> {
            // 在这里处理 taskName 的变化
            System.out.println("TaskName changed: " + newValue);
            DataStoreUtil.updateTask(this);
        };

        maxThreadsListener = (observable, oldValue, newValue) -> {
            // 在这里处理 maxThreads 的变化
            System.out.println("MaxThreads changed: " + newValue);
            DataStoreUtil.updateTask(this);
        };

        sourceListener = (observable, oldValue, newValue) -> {
            // 在这里处理 source 的变化
            System.out.println("Source changed: " + newValue);
            DataStoreUtil.updateTask(this);
        };

        taskNameProperty.addListener(taskNameListener);
        maxThreadsProperty.addListener(maxThreadsListener);
        sourceProperty.addListener(sourceListener);
    }

    public volatile boolean stopFlag = false;
    public void startSyncTask(MainController Main) {
        CoreSyncEngine syncEngine = new CoreSyncEngine(this);
        var task = CompletableFuture.supplyAsync(() -> {
            var rules = getSyncRules();
            for(var rule : rules) {
                if(stopFlag) {
                    break;
                }
                syncEngine.copyAndCheck(rule, SyncTask.this);
                rule.setInfoText("已完成");
            }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                stopFlag = true;
            }));
            return null;
        });
        task.whenComplete((ret, e) -> {
            Main.taskCompleteCallback();
        });
    }

    public List<SyncRule> getSyncRules() {
        return syncRules;
    }

    public void setSyncRules(List<SyncRule> syncRules) {
        this.syncRules = syncRules;
    }

    public String getTaskName() {
        return taskNameProperty.get();
    }

    public void setTaskName(String taskName) {
        taskNameProperty.set(taskName);
    }

    public int getMaxThreads() {
        return maxThreadsProperty.get();
    }

    public void setMaxThreads(int maxThreads) {
        maxThreadsProperty.set(maxThreads);
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public Path getSource() {
        return (Path) sourceProperty.get();
    }

    public void setSource(Path source) {
        sourceProperty.set(source);
    }
}
