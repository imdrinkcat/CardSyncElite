package com.drinkcat.cardsyncelite.controller;

import com.drinkcat.cardsyncelite.Main;
import com.drinkcat.cardsyncelite.module.SyncTask;
import com.drinkcat.cardsyncelite.util.DataStoreUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

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

    private MainController Main;


    public TaskCardController() {
    }

    public SyncTask getTask() {
        return task;
    }

    public void setMain(MainController main) {
        this.Main = main;
    }

    public void setTask(SyncTask task) {
        this.task = task;
        this.taskCardName.setText(task.getTaskName());
    }

    public void deleteTask(MouseEvent mouseEvent) {
        DataStoreUtil.removeTask(this.task);
        Main.refreshAll();
    }
}
