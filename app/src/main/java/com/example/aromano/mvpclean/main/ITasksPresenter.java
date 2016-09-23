package com.example.aromano.mvpclean.main;

import com.example.aromano.mvpclean.BasePresenter;
import com.example.aromano.mvpclean.data.Task;

/**
 * Created by aRomano on 20/09/2016.
 */

public interface ITasksPresenter extends BasePresenter {

    void addTaskClicked();

    void editTaskClicked(Task task);

    void deleteTaskClicked(Task task);

    void alterTaskState(Task task);

    void deleteTask(Task task);

    void loadTasks(boolean forceUpdate);

    void result(int requestCode, int resultCode);

}
