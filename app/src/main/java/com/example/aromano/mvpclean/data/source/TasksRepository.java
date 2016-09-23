package com.example.aromano.mvpclean.data.source;

import android.support.annotation.NonNull;

import com.example.aromano.mvpclean.data.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aRomano on 20/09/2016.
 */

public class TasksRepository implements ITasksDataSource {

    private static TasksRepository INSTANCE = null;

    private final ITasksDataSource tasksRemoteDataSource;

    private final ITasksDataSource tasksLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Task> mCachedTasks;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private TasksRepository(@NonNull ITasksDataSource tasksRemoteDataSource,
                            @NonNull ITasksDataSource tasksLocalDataSource) {
        this.tasksRemoteDataSource = tasksRemoteDataSource;
        this.tasksLocalDataSource = tasksLocalDataSource;
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tasksRemoteDataSource the backend data source
     * @param tasksLocalDataSource  the device storage data source
     * @return the {@link TasksRepository} instance
     */
    public static TasksRepository getInstance(ITasksDataSource tasksRemoteDataSource,
                                              ITasksDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }



    @Override
    public void loadTasks(final LoadTasksCallback callback) {
        // Respond immediately with cache if available and not dirty
        if (mCachedTasks != null && !mCacheIsDirty) {
            callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getTasksFromRemoteDataSource(callback);
        } else {
            // Query local storage if available. If not, query the network
            tasksLocalDataSource.loadTasks(new LoadTasksCallback() {
                @Override
                public void onTasksLoaded(List<Task> tasks) {
                    refreshCache(tasks);
                    callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback);
                }
            });
        }

    }

    @Override
    public void getTask(final String taskId, final GetTaskCallback callback) {
        Task cachedTask = getTaskFromCache(taskId);

        if (cachedTask != null) {
            callback.onTaskLoaded(cachedTask);
            return;
        }

        tasksLocalDataSource.getTask(taskId, new GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                callback.onTaskLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                tasksRemoteDataSource.getTask(taskId, new GetTaskCallback() {
                    @Override
                    public void onTaskLoaded(Task task) {
                        callback.onTaskLoaded(task);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void saveTask(Task task) {
        tasksRemoteDataSource.saveTask(task);
        tasksLocalDataSource.saveTask(task);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new HashMap<>();
        }
        mCachedTasks.put(task.getId(), task);
    }

    @Override
    public void deleteTask(Task task) {
        tasksRemoteDataSource.deleteTask(task);
        tasksLocalDataSource.deleteTask(task);

        if (mCachedTasks != null) {
            mCachedTasks.remove(task.getId());
        }
    }

    @Override
    public void editTaskState(Task task) {
        tasksRemoteDataSource.editTaskState(task);
        tasksLocalDataSource.editTaskState(task);

        if (mCachedTasks == null) {
            mCachedTasks = new HashMap<>();
        }
        mCachedTasks.put(task.getId(), task);
    }

    private Task getTaskFromCache(String taskId) {
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(taskId);
        }
    }

    private void getTasksFromRemoteDataSource(@NonNull final LoadTasksCallback callback) {
        tasksRemoteDataSource.loadTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
                refreshLocalDataSource(tasks);
                callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshLocalDataSource(List<Task> tasks) {
        tasksLocalDataSource.invalidateData();
        for (Task task : tasks) {
            tasksLocalDataSource.saveTask(task);
        }
    }

    @Override
    public void invalidateData() {
        mCacheIsDirty = true;
    }

    public void refreshCache(List<Task> tasks) {
        if (mCachedTasks == null) {
            mCachedTasks = new HashMap<>();
        }
        mCachedTasks.clear();
        for (Task task : tasks) {
            mCachedTasks.put(task.getId(), task);
        }
        mCacheIsDirty = false;
    }
}
