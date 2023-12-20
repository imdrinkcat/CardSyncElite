package com.drinkcat.cardsyncelite.controller;

import com.drinkcat.cardsyncelite.module.SyncTask;
import com.drinkcat.cardsyncelite.util.DataStoreUtil;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainController{
    @FXML
    private Label detailTaskName;
    @FXML
    private ChoiceBox detailThreadNum;
    @FXML
    private TextField detailSourcePath;
    @FXML
    private Button detailChangeSourcePath;
    @FXML
    private ListView<SyncTask> taskList;
    private ChangeListener<Integer> choiceBoxListener;
    @FXML
    public void initialize() {
        var tasks = DataStoreUtil.getTask();
        tasks.forEach(SyncTask::startListening);

        // 初始化任务列表
        ObservableList<SyncTask> syncTasks = FXCollections
                .observableArrayList(tasks);
        taskList.setItems(syncTasks);
        taskList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(SyncTask item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTaskName());
                }
            }
        });

        // 初始化线程下拉框
        Integer[] threadNumOps = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        choiceBoxListener = (observable, oldValue, newValue) -> onChangeThreadNum(newValue);
        detailThreadNum.getItems().addAll(threadNumOps);
        detailThreadNum.getSelectionModel().selectedItemProperty().addListener(choiceBoxListener);
    }

    @FXML
    public void onTaskClick() {
        var selectedItem = taskList.getSelectionModel().getSelectedItem();
        changeTaskDetail(selectedItem);
    }

    @FXML
    void onChangeThreadNum(int num) {
        taskList.getSelectionModel().getSelectedItem().setMaxThreads(num);
    }

    public void changeTaskDetail(SyncTask task) {
        detailThreadNum.getSelectionModel().selectedItemProperty().removeListener(choiceBoxListener);
        detailTaskName.setText(task.getTaskName());
        detailThreadNum.getSelectionModel().select(task.getMaxThreads() - 1);
        detailSourcePath.setText(task.getSource().toString());

        detailThreadNum.getSelectionModel().selectedItemProperty().addListener(choiceBoxListener);
    }

    @FXML
    public void changeSourcePath(MouseEvent mouseEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择源目录");

        // 获取当前舞台（Stage）
        Stage stage = (Stage) detailChangeSourcePath.getScene().getWindow();

        // 显示文件目录选择窗口，并等待用户选择
        File selectedDirectory = directoryChooser.showDialog(stage);

        // 处理用户的选择
        if (selectedDirectory != null) {
            // 用户选择了目录，可以在这里进行相应的处理
            String selectedPath = selectedDirectory.getAbsolutePath();
            System.out.println("用户选择的目录：" + selectedPath);

            // 调用你的其他方法，传递用户选择的目录
            // yourMethod(selectedPath);
        } else {
            // 用户取消了选择
            System.out.println("用户取消了选择");
        }
    }
}
