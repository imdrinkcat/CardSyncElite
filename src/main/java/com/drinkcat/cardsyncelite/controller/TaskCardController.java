package com.drinkcat.cardsyncelite.controller;

import com.drinkcat.cardsyncelite.module.SyncTask;

public class TaskCardController {
    private SyncTask task;

    public TaskCardController() {
        task = new SyncTask();
    }

    public void setTaskName(String taskName) {
        task.setTaskName(taskName);
    }
}
