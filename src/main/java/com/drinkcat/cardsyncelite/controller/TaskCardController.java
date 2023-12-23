package com.drinkcat.cardsyncelite.controller;

import com.drinkcat.cardsyncelite.module.SyncTask;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class TaskCardController {
    @FXML
    public Label taskCardName;
    @FXML
    public Button taskCardEdit;
    @FXML
    public Button taskCardDelete;
    private SyncTask task;


    public TaskCardController() {
    }

    public SyncTask getTask() {
        return task;
    }

    public void setTask(SyncTask task) {
        this.task = task;
        this.taskCardName.setText(task.getTaskName());
    }
}
