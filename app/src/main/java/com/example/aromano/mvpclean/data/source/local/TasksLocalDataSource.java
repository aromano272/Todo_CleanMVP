package com.example.aromano.mvpclean.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.example.aromano.mvpclean.data.Task;
import com.example.aromano.mvpclean.data.source.local.TasksLocalDataTables.TaskEntry;
import com.example.aromano.mvpclean.data.source.ITasksDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aRomano on 20/09/2016.
 */

public class TasksLocalDataSource implements ITasksDataSource {

    private static TasksLocalDataSource INSTANCE;

    private TasksDbHelper mDbHelper;

    // Prevent direct instantiation.
    private TasksLocalDataSource(@NonNull Context context) {
        mDbHelper = new TasksDbHelper(context);
    }

    public static TasksLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new TasksLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void loadTasks(LoadTasksCallback callback) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                TaskEntry.COLUMN_NAME_ENTRY_ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskEntry.COLUMN_NAME_COMPLETED,
                TaskEntry.COLUMN_NAME_TIMESTAMP
        };

        Cursor c = db.query(
                TaskEntry.TABLE_NAME, projection, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String itemId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
                String title = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
                String description =
                        c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
                boolean completed =
                        c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
                long timestamp = c.getLong(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TIMESTAMP));
                Task task = new Task(title, description, itemId, completed, timestamp);
                tasks.add(task);
            }
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (tasks.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else {
            callback.onTasksLoaded(tasks);
        }
    }

    @Override
    public void getTask(String taskId, GetTaskCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                TaskEntry.COLUMN_NAME_ENTRY_ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskEntry.COLUMN_NAME_COMPLETED,
                TaskEntry.COLUMN_NAME_TIMESTAMP
        };

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { taskId };

        Cursor c = db.query(
                TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        Task task = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String itemId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
            String title = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
            String description =
                    c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
            boolean completed =
                    c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
            long timestamp = c.getLong(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TIMESTAMP));
            task = new Task(title, description, itemId, completed, timestamp);
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (task != null) {
            callback.onTaskLoaded(task);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveTask(Task task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_ENTRY_ID, task.getId());
        values.put(TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TaskEntry.COLUMN_NAME_DESCRIPTION, task.getDescription());
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, task.isCompleted());
        values.put(TaskEntry.COLUMN_NAME_TIMESTAMP, task.getTimestamp());

        db.insert(TaskEntry.TABLE_NAME, null, values);

        db.close();
    }

    @Override
    public void deleteTask(Task task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { task.getId() };

        db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    @Override
    public void editTaskState(Task task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, task.isCompleted());

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { task.getId() };

        db.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void invalidateData() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(TaskEntry.TABLE_NAME, null, null);

        db.close();
    }



}
