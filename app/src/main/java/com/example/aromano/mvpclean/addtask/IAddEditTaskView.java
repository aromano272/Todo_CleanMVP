package com.example.aromano.mvpclean.addtask;

import com.example.aromano.mvpclean.BaseView;
import com.example.aromano.mvpclean.data.Task;

/**
 * Created by aRomano on 20/09/2016.
 */

public interface IAddEditTaskView {

    String getTaskTitle();

    String getTaskDescription();

    void showMissingTitleError();

    void returnToTasksList();

    void populateData(Task task);

}
