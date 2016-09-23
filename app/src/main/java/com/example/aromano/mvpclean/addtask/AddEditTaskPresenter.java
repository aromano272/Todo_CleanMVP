package com.example.aromano.mvpclean.addtask;

import android.support.annotation.NonNull;

import com.example.aromano.mvpclean.data.Task;
import com.example.aromano.mvpclean.data.source.ITasksDataSource;
import com.example.aromano.mvpclean.main.ITasksView;

import java.lang.ref.WeakReference;

/**
 * Created by aRomano on 21/09/2016.
 */

public class AddEditTaskPresenter implements IAddEditTaskPresenter {

    private Task task;

    private final ITasksDataSource tasksRepository;

    private final WeakReference<IAddEditTaskView> addTaskView;

    public AddEditTaskPresenter(Task task, @NonNull ITasksDataSource tasksRepository, @NonNull IAddEditTaskView addTaskView) {
        this.tasksRepository = tasksRepository;
        this.addTaskView = new WeakReference<>(addTaskView);

        if ((this.task = task) != null) {
            addTaskView.populateData(task);
        }
    }

    private IAddEditTaskView getView() { return addTaskView == null ? null : addTaskView.get(); }

    @Override
    public void confirmClicked() {
        Task newTask = sanitizeInput();

        if (newTask == null) {
            return;
        }
        // if its an edit
        if (task != null) {
            tasksRepository.deleteTask(task);
        }
        tasksRepository.saveTask(newTask);
        getView().returnToTasksList();
    }

    private Task sanitizeInput() {
        String title = getView().getTaskTitle();
        String description = getView().getTaskDescription();

        if (title.isEmpty()) {
            getView().showMissingTitleError();
            return null;
        }

        return new Task(title, description);
    }

    @Override
    public void start() {

    }
}
