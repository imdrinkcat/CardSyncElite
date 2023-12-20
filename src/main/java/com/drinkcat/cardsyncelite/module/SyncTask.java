package com.drinkcat.cardsyncelite.module;

import com.drinkcat.cardsyncelite.util.DataStoreUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SyncTask {
    private List<SyncRule> syncRules = new ArrayList<>();
    private SimpleStringProperty taskNameProperty;
    private SimpleIntegerProperty maxThreadsProperty;
    private int taskID;
    private Path source;

    private ChangeListener<String> taskNameListener;
    private ChangeListener<Number> maxThreadsListener;

    public SyncTask() {
        taskNameProperty = new SimpleStringProperty();
        maxThreadsProperty = new SimpleIntegerProperty();
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

        taskNameProperty.addListener(taskNameListener);
        maxThreadsProperty.addListener(maxThreadsListener);
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
        return source;
    }

    public void setSource(Path source) {
        this.source = source;
    }
}
