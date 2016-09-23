package com.example.aromano.mvpclean.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aromano.mvpclean.BaseActivity;
import com.example.aromano.mvpclean.R;
import com.example.aromano.mvpclean.addtask.AddEditTaskActivity;
import com.example.aromano.mvpclean.data.Task;
import com.example.aromano.mvpclean.data.source.ITasksDataSource;
import com.example.aromano.mvpclean.data.source.TasksRepository;
import com.example.aromano.mvpclean.data.source.local.TasksLocalDataSource;
import com.example.aromano.mvpclean.data.source.remote.TasksRemoteDataSource;

import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends BaseActivity implements ITasksView {
    ITasksPresenter presenter;

    ListView lv_content;
    FloatingActionButton fab_add_task;
    TasksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        initializeView();

        ITasksDataSource tasksRepository = TasksRepository.getInstance(TasksRemoteDataSource.getInstance(), TasksLocalDataSource.getInstance(getApplicationContext()));

        new TasksPresenter(this, tasksRepository);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.start();
    }

    private void initializeView() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        lv_content = (ListView) findViewById(R.id.lv_content);
        fab_add_task = (FloatingActionButton) findViewById(R.id.fab_add_task);
        adapter = new TasksAdapter(new ArrayList<Task>(0), this, itemListener);
        lv_content.setAdapter(adapter);

        fab_add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.addTaskClicked();
            }
        });
    }

    @Override
    public void showTasks(List<Task> tasks) {
        adapter.replaceData(tasks);
    }

    @Override
    public void showAddTask() {
        Intent intent = new Intent(this, AddEditTaskActivity.class);
        startActivityForResult(intent, AddEditTaskActivity.RC_ADD_TASK);
    }

    @Override
    public void showEditTask(Task task) {
        Intent intent = new Intent(this, AddEditTaskActivity.class);
        intent.putExtra("task", task);
        startActivityForResult(intent, AddEditTaskActivity.RC_ADD_TASK);
    }

    @Override
    public void showDeleteTask(final Task task) {
        new AlertDialog.Builder(this)
            .setTitle("Delete task")
            .setMessage("Are you sure you want to delete " + task.getTitle())
            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            })
            .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    presenter.deleteTask(task);
                    presenter.loadTasks(false);
                    dialogInterface.dismiss();
                }
            })
            .show();

    }

    @Override
    public void showProgressBar(boolean show) {
        if (show) {
            showProgressDialog();
        } else {
            hideProgressDialog();
        }
    }

    @Override
    public void showLoadingTasksError() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_refresh:
                presenter.loadTasks(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.result(requestCode, resultCode);
    }

    /**
     * Listener for clicks on tasks in the ListView.
     */
    TaskItemListener itemListener = new TaskItemListener() {
        @Override
        public void onTaskEditClick(Task task) {
            presenter.editTaskClicked(task);
        }

        @Override
        public void onTaskDeleteClick(Task task) {
            presenter.deleteTaskClicked(task);
        }

        @Override
        public void onToggleStateClick(Task task) {
            presenter.alterTaskState(task);
        }
    };

    private static class TasksAdapter extends BaseAdapter {

        private List<Task> tasks;
        private TaskItemListener itemListener;
        private Context context;

        public TasksAdapter(List<Task> tasks, Context context, TaskItemListener itemListener) {
            setList(tasks);
            this.itemListener = itemListener;
            this.context = context;
        }

        public void replaceData(List<Task> tasks) {
            setList(tasks);
            notifyDataSetChanged();
        }

        private void setList(List<Task> tasks) {
            this.tasks = tasks;
        }

        @Override
        public int getCount() {
            return tasks.size();
        }

        @Override
        public Task getItem(int i) {
            return tasks.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rootView = view;
            if (rootView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rootView = inflater.inflate(R.layout.listitem_task, viewGroup, false);
            }

            final Task task = getItem(i);

            TextView tv_title = (TextView) rootView.findViewById(R.id.tv_title);
            TextView tv_description = (TextView) rootView.findViewById(R.id.tv_description);
            TextView tv_timestamp = (TextView) rootView.findViewById(R.id.tv_timestamp);
            CheckBox cb_completed = (CheckBox) rootView.findViewById(R.id.cb_completed);

            tv_title.setText(task.getTitle());
            tv_description.setText(task.getDescription());
            tv_timestamp.setText(task.getFormattedTimestamp());

            // Active/completed task UI
            cb_completed.setChecked(task.isCompleted());

            cb_completed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onToggleStateClick(task);
                }
            });

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemListener.onToggleStateClick(task);
                }
            });

            rootView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showPopupMenu(view, task);
                    return false;
                }
            });

            return rootView;
        }

        private void showPopupMenu(View view, final Task task) {
            PopupMenu popup = new PopupMenu(context, view);
            // Inflate the menu from xml
            popup.getMenuInflater().inflate(R.menu.popup_item_tasks, popup.getMenu());
            // Setup menu item selection
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_action_edit:
                            itemListener.onTaskEditClick(task);
                            return true;
                        case R.id.menu_action_delete:
                            itemListener.onTaskDeleteClick(task);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            // Show the menu
            popup.show();
        }

    }

    public interface TaskItemListener {

        void onTaskEditClick(Task task);

        void onTaskDeleteClick(Task task);

        void onToggleStateClick(Task task);

    }

}
