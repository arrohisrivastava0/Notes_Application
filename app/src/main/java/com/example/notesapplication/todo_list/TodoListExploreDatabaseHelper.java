package com.example.notesapplication.todo_list;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class TodoListExploreDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todo_list_database";
    private static final int DATABASE_VERSION= 1;
    public static final String TABLE_TODO = "todo_list";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TODO_HEAD="todo_list_head";
    public static final String COLUMN_TODO_STATUS="todo_list_status";


    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_TODO + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TODO_HEAD+" TEXT, " + COLUMN_TODO_STATUS+" TEXT);";

    public TodoListExploreDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TODO);
        onCreate(db);
    }
}
