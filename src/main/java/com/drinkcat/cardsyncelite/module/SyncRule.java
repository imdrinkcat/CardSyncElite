package com.drinkcat.cardsyncelite.module;

import com.drinkcat.cardsyncelite.controller.MainController;
import com.drinkcat.cardsyncelite.util.DataStoreUtil;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.nio.file.Path;
import java.util.List;

public class SyncRule {
    private final ObjectProperty<Path> target = new SimpleObjectProperty<>();
    private final BooleanProperty needCheck = new SimpleBooleanProperty();
    private final BooleanProperty maintainStructure = new SimpleBooleanProperty();
    private final BooleanProperty needRename = new SimpleBooleanProperty();
    private final ListProperty<String> extensions = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final IntegerProperty taskID = new SimpleIntegerProperty();
    private final ObjectProperty<SyncTask> task = new SimpleObjectProperty<>();
    private final IntegerProperty ruleID = new SimpleIntegerProperty();
    private final StringProperty infoText = new SimpleStringProperty("等待开始...");
    private final DoubleProperty progress = new SimpleDoubleProperty(0.0);
    private MainController Main;

    public SyncRule() {

    }

    public SyncRule(Path target, boolean needCheck, boolean maintainStructure, boolean needRename, List<String> extensions) {
        setTarget(target);
        setNeedCheck(needCheck);
        setMaintainStructure(maintainStructure);
        setNeedRename(needRename);
        setExtensions(extensions);
    }

    public void addListeners() {
        // 添加对应的ChangeListener
        target.addListener((observable, oldValue, newValue) -> handleTargetChange(newValue));
        needCheck.addListener((observable, oldValue, newValue) -> handleNeedCheckChange(newValue));
        maintainStructure.addListener((observable, oldValue, newValue) -> handleMaintainStructureChange(newValue));
        needRename.addListener((observable, oldValue, newValue) -> handleNeedRenameChange(newValue));
        extensions.addListener((observable, oldValue, newValue) -> handleExtensionsChange(newValue));
        infoText.addListener((observable, oldValue, newValue) -> handleInfoTextChange(newValue));
        progress.addListener((observable, oldValue, newValue) -> handleProgressChange((Double) newValue));
    }

    private void handleTargetChange(Path newValue) {
        // 处理 target 变化的逻辑
        System.out.println("Target changed to: " + newValue);
        DataStoreUtil.updateTask(getTask());
    }

    private void handleInfoTextChange(String newValue) {
        // 处理 infoText 变化的逻辑
        System.out.println("InfoText changed to: " + newValue);
        getMain().refreshRules(getTask());
    }

    private void handleProgressChange(Double newValue) {
        // 处理 progress 变化的逻辑
        System.out.println("Progress changed to: " + newValue);
        getMain().refreshRules(getTask());
    }

    private void handleNeedCheckChange(boolean newValue) {
        // 处理 needCheck 变化的逻辑
        System.out.println("NeedCheck changed to: " + newValue);
        DataStoreUtil.updateTask(getTask());
    }

    private void handleMaintainStructureChange(boolean newValue) {
        // 处理 maintainStructure 变化的逻辑
        System.out.println("MaintainStructure changed to: " + newValue);
        DataStoreUtil.updateTask(getTask());
    }

    private void handleNeedRenameChange(boolean newValue) {
        // 处理 needRename 变化的逻辑
        System.out.println("NeedRename changed to: " + newValue);
        DataStoreUtil.updateTask(getTask());
    }

    private void handleExtensionsChange(List<String> newValue) {
        // 处理 extensions 变化的逻辑
        System.out.println("Extensions changed to: " + newValue);
        DataStoreUtil.updateTask(getTask());
    }

    public Path getTarget() {
        return target.get();
    }

    public ObjectProperty<Path> targetProperty() {
        return target;
    }

    public void setTarget(Path target) {
        this.target.set(target);
    }

    public boolean isNeedCheck() {
        return needCheck.get();
    }

    public BooleanProperty needCheckProperty() {
        return needCheck;
    }

    public void setNeedCheck(boolean needCheck) {
        this.needCheck.set(needCheck);
    }

    public boolean isMaintainStructure() {
        return maintainStructure.get();
    }

    public BooleanProperty maintainStructureProperty() {
        return maintainStructure;
    }

    public void setMaintainStructure(boolean maintainStructure) {
        this.maintainStructure.set(maintainStructure);
    }

    public boolean isNeedRename() {
        return needRename.get();
    }

    public BooleanProperty needRenameProperty() {
        return needRename;
    }

    public void setNeedRename(boolean needRename) {
        this.needRename.set(needRename);
    }

    public List<String> getExtensions() {
        return extensions.get();
    }

    public ListProperty<String> extensionsProperty() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.extensions.setAll(extensions);
    }

    public int getTaskID() {
        return taskID.get();
    }

    public IntegerProperty taskIDProperty() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID.set(taskID);
    }

    public int getRuleID() {
        return ruleID.get();
    }

    public IntegerProperty ruleIDProperty() {
        return ruleID;
    }

    public void setRuleID(int ruleID) {
        this.ruleID.set(ruleID);
    }

    public SyncTask getTask() {
        return task.get();
    }

    public ObjectProperty<SyncTask> taskProperty() {
        return task;
    }

    public void setTask(SyncTask task) {
        this.task.set(task);
    }

    public String getInfoText() {
        return infoText.get();
    }

    public void setInfoText(String infoText) {
        this.infoText.set(infoText);
    }

    public Double getProgress() {
        return progress.get();
    }

    public void setProgress(Double progress) {
        this.progress.set(progress);
    }

    public MainController getMain() {
        return Main;
    }

    public void setMain(MainController main) {
        Main = main;
    }
}
