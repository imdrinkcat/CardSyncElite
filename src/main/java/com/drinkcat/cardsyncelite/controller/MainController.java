package com.drinkcat.cardsyncelite.controller;

import com.drinkcat.cardsyncelite.Main;
import com.drinkcat.cardsyncelite.MainApplication;
import com.drinkcat.cardsyncelite.module.SyncRule;
import com.drinkcat.cardsyncelite.module.SyncTask;
import com.drinkcat.cardsyncelite.util.AlertUtil;
import com.drinkcat.cardsyncelite.util.DataStoreUtil;
import com.drinkcat.cardsyncelite.util.ExtensionsUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MainController{
    @FXML
    public Button createSyncRule;
    @FXML
    public ListView ruleList;
    @FXML
    public VBox taskDetailPane;
    @FXML
    public Button startTask;
    @FXML
    public SplitPane basePane;
    @FXML
    public AnchorPane taskPane;
    @FXML
    public HBox configPane;
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
    private static SyncTask selectedTask = null;

    @FXML
    public void initialize() {
        // 初始化任务列表
        refreshAll();
        taskList.setCellFactory(new Callback<ListView<SyncTask>, ListCell<SyncTask>>() {
            @Override
            public ListCell<SyncTask> call(ListView<SyncTask> syncTaskListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(SyncTask task, boolean empty) {
                        super.updateItem(task, empty);
                        if(empty|| task == null) {
                            this.setText(null);
                            this.setGraphic(null);
                            return;
                        }
                        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("view/TaskCardView.fxml"));
                        try {
                            this.setGraphic(loader.load());
                            TaskCardController controller = loader.getController();
                            controller.setTask(task);
                            controller.setMain(MainController.this);
                        } catch (IOException e) {
                            throw new RuntimeException("任务列表视图加载失败!");
                        }
                    }
                };
            }
        });

        // 初始化同步规则列表
        ruleList.setCellFactory(new Callback<ListView<SyncRule>, ListCell<SyncRule>>() {
            @Override
            public ListCell<SyncRule> call(ListView<SyncRule> syncRuleListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(SyncRule rule, boolean empty) {
                        super.updateItem(rule, empty);
                        if(empty|| rule == null) {
                            this.setText(null);
                            this.setGraphic(null);
                            return;
                        }
                        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("view/RuleCardView.fxml"));
                        try {
                            this.setGraphic(loader.load());
                            RuleCardController controller = loader.getController();
                            rule.setMain(MainController.this);
                            controller.setDisable(configPane.isDisable());
                            controller.setRule(rule);
                            controller.setProgressInfoText(new Label(rule.getInfoText()));
                            controller.setProgressBar(new ProgressBar(rule.getProgress()));
                            controller.setMain(MainController.this);
                        } catch (IOException e) {
                            throw new RuntimeException("任务列表视图加载失败!");
                        }
                    }
                };
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
        selectedTask = taskList.getSelectionModel().getSelectedItem();
        changeTaskDetail(selectedTask);
    }

    @FXML
    void onChangeThreadNum(int num) {
        taskList.getSelectionModel().getSelectedItem().setMaxThreads(num);
    }

    public void changeTaskDetail(SyncTask task) {
        Platform.runLater(() -> {
            taskDetailPane.setDisable(false);

            detailThreadNum.getSelectionModel().selectedItemProperty().removeListener(choiceBoxListener);
            detailTaskName.setText(task.getTaskName());
            detailThreadNum.getSelectionModel().select(task.getMaxThreads() - 1);
            detailSourcePath.setText(task.getSource().toString());
            detailThreadNum.getSelectionModel().selectedItemProperty().addListener(choiceBoxListener);
            refreshRules(task);
        });
    }

    public void refreshRules(SyncTask task) {
        Platform.runLater(() -> {
            ruleList.setItems(FXCollections.observableArrayList(task.getSyncRules()));
        });
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
            String selectedPath = selectedDirectory.getAbsolutePath();
            if(selectedPath == null) return;
            selectedTask.setSource(Paths.get(selectedPath));
            detailSourcePath.setText(selectedPath);
        }
    }

    @FXML
    public void startSyncTask(MouseEvent mouseEvent) {
        taskPane.setDisable(true);
        detailSourcePath.setDisable(true);
        detailThreadNum.setDisable(true);
        detailChangeSourcePath.setDisable(true);
        startTask.setDisable(true);
        createSyncRule.setDisable(true);

        selectedTask.startSyncTask(MainController.this);
    }

    public void taskCompleteCallback() {
        taskPane.setDisable(false);
        detailSourcePath.setDisable(false);
        detailThreadNum.setDisable(false);
        detailChangeSourcePath.setDisable(false);
        startTask.setDisable(false);
        createSyncRule.setDisable(false);

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示信息");
            alert.setHeaderText(null);
            alert.setContentText("同步任务完成!");
            alert.showAndWait();
        });
    }

    @FXML
    public void createNewRule(MouseEvent mouseEvent) {
        SyncRule rule = new SyncRule();
        rule.setMain(MainController.this);
        rule.setExtensions(ExtensionsUtil.allExtensions.extensions);
        rule.setNeedCheck(true);
        rule.setTask(selectedTask);
        rule.setTarget(Path.of("/"));
        rule.setNeedRename(false);
        rule.setMaintainStructure(false);
        rule.setTaskID(selectedTask.getTaskID());
        selectedTask.getSyncRules().add(rule);
        DataStoreUtil.updateTask(selectedTask);
        refreshRules(selectedTask);
    }

    public void refreshAll() {
        var tasks = DataStoreUtil.getTask();
        tasks.forEach(SyncTask::startListening);
        ObservableList<SyncTask> syncTasks = FXCollections
                .observableArrayList(tasks);
        taskList.setItems(syncTasks);
        changeTaskDetail(selectedTask);
    }

    public void editTaskName(MouseEvent mouseEvent) {
        TextInputDialog dialog = new TextInputDialog(selectedTask.getTaskName());
        dialog.setTitle("修改名称");
        dialog.setHeaderText(null);
        dialog.setContentText("请输入项目名称: ");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            System.out.println("Task name: " + result.get());
            selectedTask.setTaskName(result.get());

            refreshAll();
        }
    }

    public void createNewTask(MouseEvent mouseEvent) {
        TextInputDialog dialog = new TextInputDialog("Task Name");
        dialog.setTitle("设置任务名称");
        dialog.setHeaderText(null);
        dialog.setContentText("请输入任务名称: ");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            System.out.println("Task name: " + result.get());
            SyncTask task = new SyncTask();
            task.setTaskName(result.get());
            task.setSource(Paths.get(System.getProperty("user.dir")));
            task.setMaxThreads(5);
            DataStoreUtil.addTask(task);
            refreshAll();
        }
    }
}
