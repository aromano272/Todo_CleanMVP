package com.example.aromano.mvpclean;

import com.example.aromano.mvpclean.data.Task;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aRomano on 22/09/2016.
 */

public class FakeTasksRemoveDataSourceTest {

    private static final List<Task> tasks;

    static {
        Task task = new Task("some title", "some description");
        tasks = new ArrayList<>();
        tasks.add(task);
    }

    @Test
    public void test() {

    }



}
