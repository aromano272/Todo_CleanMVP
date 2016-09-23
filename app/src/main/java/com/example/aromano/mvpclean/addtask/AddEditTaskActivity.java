package com.example.aromano.mvpclean.addtask;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.aromano.mvpclean.R;
import com.example.aromano.mvpclean.data.Task;
import com.example.aromano.mvpclean.data.source.ITasksDataSource;
import com.example.aromano.mvpclean.data.source.TasksRepository;
import com.example.aromano.mvpclean.data.source.local.TasksLocalDataSource;
import com.example.aromano.mvpclean.data.source.remote.TasksRemoteDataSource;

public class AddEditTaskActivity extends AppCompatActivity implements IAddEditTaskView {
    public static final int RC_ADD_TASK = 3000;
    private IAddEditTaskPresenter presenter;
    private EditText et_title;
    private EditText et_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initializeView();

        Task task = getIntent().getParcelableExtra("task");

        ITasksDataSource tasksRepository = TasksRepository.getInstance(TasksRemoteDataSource.getInstance(), TasksLocalDataSource.getInstance(getApplicationContext()));

        new AddEditTaskPresenter(task, tasksRepository, this);
    }

    private void initializeView() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        et_title = (EditText) findViewById(R.id.et_title);
        et_description = (EditText) findViewById(R.id.et_description);

        findViewById(R.id.fab_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.confirmClicked();
            }
        });
    }

    @Override
    public void populateData(Task task) {
        et_title.setText(task.getTitle());
        et_description.setText(task.getDescription());
    }

    @Override
    public String getTaskTitle() {
        return et_title.getText().toString();
    }

    @Override
    public String getTaskDescription() {
        return et_description.getText().toString();
    }

    @Override
    public void showMissingTitleError() {
        et_title.setError("Field is required");
    }

    @Override
    public void returnToTasksList() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }
}
