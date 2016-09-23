package com.example.aromano.mvpclean.data.source.remote;

import com.example.aromano.mvpclean.data.Task;
import com.example.aromano.mvpclean.data.source.ITasksDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aRomano on 20/09/2016.
 */
public class FakeTasksRemoteDataSource implements ITasksDataSource {

    private static FakeTasksRemoteDataSource INSTANCE;

    // todo implement proper remote with firebase or smt
    private Map<String, Task> cache;

    // Prevent direct instantiation.
    private FakeTasksRemoteDataSource() {
        cache = new HashMap<>();
    }

    public static FakeTasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeTasksRemoteDataSource();
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
        cache.put(task.getId(), task);
    }

    @Override
    public void deleteTask(Task task) {
        cache.remove(task.getId());
    }

    @Override
    public void editTaskState(Task task) {
        cache.put(task.getId(), task);
    }

    @Override
    public void invalidateData() {

    }

}
