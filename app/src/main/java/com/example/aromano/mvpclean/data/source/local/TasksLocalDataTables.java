package com.example.aromano.mvpclean.data.source.local;

import android.provider.BaseColumns;

/**
 * Created by aRomano on 20/09/2016.
 */
public final class TasksLocalDataTables {

    // To prevent direct instantiation
    private TasksLocalDataTables() {}

    /* Inner class that defines the table contents */
    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_COMPLETED = "completed";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
}
