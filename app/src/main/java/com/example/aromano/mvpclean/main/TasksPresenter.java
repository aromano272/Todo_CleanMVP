package com.example.aromano.mvpclean.main;

import android.support.annotation.NonNull;

import com.example.aromano.mvpclean.addtask.AddEditTaskActivity;
import com.example.aromano.mvpclean.data.Task;
import com.example.aromano.mvpclean.data.source.ITasksDataSource;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by aRomano on 20/09/2016.
 */

public class TasksPresenter implements ITasksPresenter {

    private final ITasksDataSource tasksRepository;

    private final WeakReference<ITasksView> tasksView;

    public TasksPresenter(@NonNull ITasksView tasksView, @NonNull ITasksDataSource tasksRepository) {
        this.tasksRepository = tasksRepository;
        this.tasksView = new WeakReference<>(tasksView);
    }

    private ITasksView getView() { return tasksView == null ? null : tasksView.get(); }

    @Override
    public void addTaskClicked() {
        getView().showAddTask();
    }

    @Override
    public void editTaskClicked(Task task) {
        getView().showEditTask(task);
    }

    @Override
    public void deleteTaskClicked(Task task) {
        getView().showDeleteTask(task);
    }

    @Override
    public void deleteTask(Task task) {
        tasksRepository.deleteTask(task);
        loadTasks(false);
    }

    @Override
    public void alterTaskState(Task task) {
        task.setCompleted(!task.isCompleted());
        tasksRepository.editTaskState(task);
        loadTasks(false);
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        getView().showProgressBar(true);

        if (forceUpdate) {
            tasksRepository.invalidateData();
        }

        tasksRepository.loadTasks(new ITasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                getView().showProgressBar(false);
                processTasks(tasks);
            }

            @Override
            public void onDataNotAvailable() {
                getView().showLoadingTasksError();
            }
        });
    }

    private void processTasks(List<Task> tasks) {
        // Show the list of tasks
        getView().showTasks(tasks);
    }

    @Override
    public void start() {
        loadTasks(false);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        switch (requestCode) {
            case AddEditTaskActivity.RC_ADD_TASK:
                loadTasks(false);
                break;
        }
    }
}
