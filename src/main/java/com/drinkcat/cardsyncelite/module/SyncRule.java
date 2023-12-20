package com.drinkcat.cardsyncelite.module;

import java.nio.file.Path;
import java.util.List;

public class SyncRule {
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
    private Path target;
    private boolean needCheck;
    private boolean maintainStructure;
    private boolean needRename;
    private List<String> extensions;
    private int TaskID;
    private int RuleID;

    public SyncRule() {
    }

    public SyncRule(Path target, boolean needCheck, boolean maintainStructure, boolean needRename, List<String> extensions) {
        this.target = target;
        this.needCheck = needCheck;
        this.maintainStructure = maintainStructure;
        this.needRename = needRename;
        this.extensions = extensions;
    }

    public Path getTarget() {
        return target;
    }

    public void setTarget(Path target) {
        this.target = target;
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

    public List<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }

    public int getTaskID() {
        return TaskID;
    }

    public void setTaskID(int taskID) {
        TaskID = taskID;
    }

    public int getRuleID() {
        return RuleID;
    }

    public void setRuleID(int ruleID) {
        RuleID = ruleID;
    }
}