package com.example.aromano.mvpclean.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.aromano.mvpclean.data.Task;
import com.example.aromano.mvpclean.data.source.ITasksDataSource;
import com.example.aromano.mvpclean.data.source.local.TasksDbHelper;
import com.example.aromano.mvpclean.data.source.local.TasksLocalDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aRomano on 20/09/2016.
 */
public class TasksRemoteDataSource implements ITasksDataSource {

    private static TasksRemoteDataSource INSTANCE;

    // todo implement proper remote with firebase or smt
    private Map<String, Task> cache;

    // Prevent direct instantiation.
    private TasksRemoteDataSource() {
        cache = new HashMap<>();
    }

    public static TasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TasksRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void loadTasks(LoadTasksCallback callback) {
        callback.onTasksLoaded(new ArrayList<>(cache.values()));
    }

    @Override
    public void getTask(String taskId, GetTaskCallback callback) {
        if (cache.get(taskId) != null) {
            callback.onTaskLoaded(cache.get(taskId));
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveTask(Task task) {

    }

    @Override
    public void deleteTask(Task task) {

    }

    @Override
    public void editTaskState(Task task) {

    }

    @Override
    public void invalidateData() {

    }

}
