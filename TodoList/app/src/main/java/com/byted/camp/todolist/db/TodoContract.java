package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

public final class TodoContract {

    public static final String SQL_CREATE_NOTES =
            "CREATE TABLE " + TodoNote.TABLE_NAME
                    + "(" + TodoNote._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TodoNote.COLUMN_DEADLINE + " TEXT, "
                    + TodoNote.COLUMN_SHOW + " TEXT, "
                    + TodoNote.COLUMN_STATE + " INTEGER, "
                    + TodoNote.COLUMN_SCHEDULED + " TEXT, "
                    + TodoNote.COLUMN_WEEK + " INTEGER, "
                    + TodoNote.COLUMN_WEEKLOP + " INTEGER, "
                    + TodoNote.COLUMN_MONTHLOP + " INTEGER, "
                    + TodoNote.COLUMN_YEARLOP + " INTEGER, "
                    + TodoNote.COLUMN_CAPTION + " TEXT, "
                    + TodoNote.COLUMN_FILE + " TEXT, "
                    + TodoNote.COLUMN_TAG + " TEXT, "
                    + TodoNote.COLUMN_CONTENT + " TEXT, "
                    + TodoNote.COLUMN_PRIORITY + " INTEGER)";

    public static final String SQL_ADD_PRIORITY_COLUMN =
            "ALTER TABLE " + TodoNote.TABLE_NAME + " ADD " + TodoNote.COLUMN_PRIORITY + " INTEGER";

    private TodoContract() {
    }

    public static class TodoNote implements BaseColumns {
        public static final String TABLE_NAME = "note";

        public static final String COLUMN_DEADLINE = "deadline";
        public static final String COLUMN_SCHEDULED = "scheduled";
        public static final String COLUMN_SHOW = "show";
        public static final String COLUMN_WEEK = "week";
        public static final String COLUMN_WEEKLOP = "weeklop";
        public static final String COLUMN_MONTHLOP = "monthlop";
        public static final String COLUMN_YEARLOP = "yearlop";

        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_CAPTION = "caption";
        public static final String COLUMN_FILE = "file";
        public static final String COLUMN_TAG = "tag";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_PRIORITY = "priority";
    }

}