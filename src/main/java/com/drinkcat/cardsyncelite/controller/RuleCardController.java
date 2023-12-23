package com.drinkcat.cardsyncelite.controller;

import com.drinkcat.cardsyncelite.module.SyncRule;
import com.drinkcat.cardsyncelite.module.SyncTask;
import com.drinkcat.cardsyncelite.util.DataStoreUtil;
import com.drinkcat.cardsyncelite.util.ExtensionsUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.nio.file.Paths;

public class RuleCardController {
    public MainController Main;
    @FXML
    public Button detailChangeTargetPath;
    @FXML
    public Button deleteRuleButton;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public Label progressInfoText;
    private SyncRule rule;
    @FXML
    public TextField targetDic;
    @FXML
    public ComboBox fileTypeBox;
    @FXML
    public CheckBox fileCheckBox;

    public void setDisable(boolean b) {
        Platform.runLater(() -> {
            fileTypeBox.setDisable(b);
            fileCheckBox.setDisable(b);
            targetDic.setDisable(b);
            deleteRuleButton.setDisable(b);
            detailChangeTargetPath.setDisable(b);
        });
    }
    @FXML
    public void initialize() {
        progressInfoText.setText("等待开始...");
        progressBar.setProgress(0);
        fileTypeBox.getItems().addAll(
                ExtensionsUtil.videoExtensions,
                ExtensionsUtil.photoExtensions,
                ExtensionsUtil.allExtensions
        );

        fileTypeBox.setConverter(new StringConverter<Object>() {
            @Override
            public String toString(Object o) {
                if (o == null) {
                    return "";
                }
                var extensionUtil = (ExtensionsUtil) o;
                return extensionUtil.toString();
            }

            @Override
            public Object fromString(String s) {
                return null;
            }
        });
    }


    public ComboBox getFileTypeBox() {
        return fileTypeBox;
    }

    public void setFileTypeBox(ComboBox fileTypeBox) {
        this.fileTypeBox = fileTypeBox;
    }

    public CheckBox getFileCheckBox() {
        return fileCheckBox;
    }

    public void setFileCheckBox(CheckBox fileCheckBox) {
        this.fileCheckBox = fileCheckBox;
    }

    public SyncRule getRule() {
        return rule;
    }

    public void setRule(SyncRule rule) {
        this.rule = rule;
        this.progressBar.setProgress(rule.getProgress());
        this.progressInfoText.setText(rule.getInfoText());
        this.fileCheckBox.setSelected(rule.isNeedCheck());
        this.targetDic.setText(rule.getTarget().toString());
        if(rule.getExtensions().equals(ExtensionsUtil.videoExtensions.extensions))
            this.fileTypeBox.setValue(ExtensionsUtil.videoExtensions);
        else if(rule.getExtensions().equals(ExtensionsUtil.photoExtensions.extensions))
            this.fileTypeBox.setValue(ExtensionsUtil.photoExtensions);
        else
            this.fileTypeBox.setValue(ExtensionsUtil.allExtensions);
    }

    @FXML
    public void changeTargetPath(MouseEvent mouseEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择目的目录");

        // 获取当前舞台（Stage）
        Stage stage = (Stage) targetDic.getScene().getWindow();

        // 显示文件目录选择窗口，并等待用户选择
        File selectedDirectory = directoryChooser.showDialog(stage);

        // 处理用户的选择
        if (selectedDirectory != null) {
            String selectedPath = selectedDirectory.getAbsolutePath();
            if(selectedPath == null) return;
            rule.setTarget(Paths.get(selectedPath));
            targetDic.setText(selectedPath);
        }
    }
    @FXML
    public void fileCheckBoxChange(MouseEvent mouseEvent) {
        rule.setNeedCheck(fileCheckBox.isSelected());
    }

    @FXML
    public void deleteRule(MouseEvent mouseEvent) {
        SyncTask task = rule.getTask();
        task.getSyncRules().remove(rule);
        DataStoreUtil.updateTask(task);
        Main.refreshRules(task);
    }

    public void setMain(MainController main) {
        Main = main;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public Label getProgressInfoText() {
        return progressInfoText;
    }

    public void setProgressInfoText(Label progressInfoText) {
        this.progressInfoText = progressInfoText;
    }

    public void changeFilter(ActionEvent actionEvent) {
        var selected = (ExtensionsUtil) fileTypeBox.getSelectionModel().getSelectedItem();
        rule.setExtensions(selected.extensions);
    }
}
