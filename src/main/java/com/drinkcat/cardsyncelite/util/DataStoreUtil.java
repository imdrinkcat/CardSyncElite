package com.drinkcat.cardsyncelite.util;

import com.drinkcat.cardsyncelite.MainApplication;
import com.drinkcat.cardsyncelite.module.SyncRule;
import com.drinkcat.cardsyncelite.module.SyncTask;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.*;

public class DataStoreUtil {
    private static String dbUrl = "jdbc:sqlite:data.db";
    public static void updateDbUrl(MainApplication mainApplication) {
        dbUrl = "jdbc:sqlite:" + System.getProperty("user.dir") + File.separator + "dbs" + File.separator + "data.db";
        System.out.println(dbUrl);
    }
    public static List<SyncTask> getTask() {
        List<SyncTask> taskList = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from Tasks")) {
            while(rs.next()) {
                var task = new SyncTask();
                task.setTaskName(rs.getString("TaskName"));
                task.setMaxThreads(rs.getInt("maxThreads"));
                task.setTaskID(rs.getInt("ID"));
                task.setSource(Paths.get(rs.getString("source")));
                task.setSyncRules(getRule(task));
                taskList.add(task);
            }
        } catch (SQLException e) {
            System.err.println(dbUrl);
            throw new RuntimeException("数据库文件缺失!");
        }
        return taskList;
    }
    public static List<SyncRule> getRule(SyncTask task) {
        int taskID = task.getTaskID();
        List<SyncRule> ruleList = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(dbUrl);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Rules WHERE TaskID = " + taskID)) {
            while(rs.next()) {
                var rule = new SyncRule();
                rule.setTask(task);
                rule.setRuleID(rs.getInt("ID"));
                rule.setTaskID(rs.getInt("TaskID"));
                rule.setTarget(Paths.get(rs.getString("target")));
                rule.setNeedCheck(rs.getInt("needCheck") == 1);
                rule.setMaintainStructure(rs.getInt("maintainStructure") == 1);
                rule.setNeedRename(rs.getInt("needRename") == 1);
                rule.setExtensions(Arrays.asList(rs
                        .getString("extensions")
                        .split(",")));
                rule.addListeners();
                ruleList.add(rule);
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据库文件缺失!");
        }
        return ruleList;
    }
    public static void addTask(SyncTask task) {
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            // 添加任务信息
            String insertTaskQuery = "INSERT INTO Tasks (TaskName, maxThreads, source) VALUES (?, ?, ?)";
            try (PreparedStatement taskStatement = connection.prepareStatement(insertTaskQuery, Statement.RETURN_GENERATED_KEYS)) {
                taskStatement.setString(1, task.getTaskName());
                taskStatement.setInt(2, task.getMaxThreads());
                taskStatement.setString(3, task.getSource().toString());
                taskStatement.executeUpdate();

                // 获取新插入任务的ID
                ResultSet generatedKeys = taskStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int taskID = generatedKeys.getInt(1);
                    task.setTaskID(taskID);
                } else {
                    throw new SQLException("无法获取新插入任务的ID");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("添加任务失败!", e);
        }
    }
    public static void updateTask(SyncTask task) {
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            // 更新任务信息
            String updateTaskQuery = "UPDATE Tasks SET TaskName = ?, maxThreads = ?, source = ? WHERE ID = ?";
            try (PreparedStatement taskStatement = connection.prepareStatement(updateTaskQuery)) {
                taskStatement.setString(1, task.getTaskName());
                taskStatement.setInt(2, task.getMaxThreads());
                taskStatement.setString(3, task.getSource().toString());
                taskStatement.setInt(4, task.getTaskID());
                taskStatement.executeUpdate();

                // 更新任务关联的规则信息
                updateRulesForTask(task);
            }
        } catch (SQLException e) {
            throw new RuntimeException("更新任务失败!", e);
        }
    }
    public static void removeTask(SyncTask task) {
        int taskID = task.getTaskID();
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            // 删除任务关联的规则信息
            removeRulesForTask(taskID);

            // 删除任务信息
            String removeTaskQuery = "DELETE FROM Tasks WHERE ID = ?";
            try (PreparedStatement taskStatement = connection.prepareStatement(removeTaskQuery)) {
                taskStatement.setInt(1, taskID);
                taskStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("删除任务失败!", e);
        }
    }
    private static void addRulesForTask(SyncTask task) {
        // 添加规则信息
        List<SyncRule> ruleList = task.getSyncRules();
        for (SyncRule rule : ruleList) {
            addRule(rule);
        }
    }
    private static void updateRulesForTask(SyncTask task) {
        // 先删除任务关联的所有规则信息
        removeRulesForTask(task.getTaskID());

        // 再添加规则信息
        addRulesForTask(task);
    }
    private static void removeRulesForTask(int taskID) {
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            // 删除规则信息
            String removeRulesQuery = "DELETE FROM Rules WHERE TaskID = ?";
            try (PreparedStatement rulesStatement = connection.prepareStatement(removeRulesQuery)) {
                rulesStatement.setInt(1, taskID);
                rulesStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("删除规则失败!", e);
        }
    }
    private static void addRule(SyncRule rule) {
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            // 添加规则信息
            String insertRuleQuery = "INSERT INTO Rules (TaskID, target, needCheck, maintainStructure, needRename, extensions) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ruleStatement = connection.prepareStatement(insertRuleQuery)) {
                ruleStatement.setInt(1, rule.getTaskID());
                ruleStatement.setString(2, rule.getTarget().toString());
                ruleStatement.setInt(3, rule.isNeedCheck() ? 1 : 0);
                ruleStatement.setInt(4, rule.isMaintainStructure() ? 1 : 0);
                ruleStatement.setInt(5, rule.isNeedRename() ? 1 : 0);
                ruleStatement.setString(6, String.join(",", rule.getExtensions()));
                ruleStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("添加规则失败!", e);
        }
    }
}
