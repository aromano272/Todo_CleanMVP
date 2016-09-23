package com.example.aromano.mvpclean.main;

import com.example.aromano.mvpclean.BaseView;
import com.example.aromano.mvpclean.data.Task;

import java.util.List;

/**
 * Created by aRomano on 20/09/2016.
 */

public interface ITasksView {

    void showProgressBar(boolean show);

    void showTasks(List<Task> tasks);

    void showAddTask();

    void showEditTask(Task task);

    void showDeleteTask(Task task);

    void showLoadingTasksError();

}
