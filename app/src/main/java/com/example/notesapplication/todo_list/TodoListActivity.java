package com.example.notesapplication.todo_list;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notesapplication.R;
import com.example.notesapplication.textnote.TextNoteData;
import com.example.notesapplication.textnote.TextNoteDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class TodoListActivity extends AppCompatActivity {
    private ImageButton saveTodoList, addItemTodo;
    private EditText addItemET, headingTodoListET;
    private long todoID;
    private boolean isExisting;
    private TodoListDatabaseHelper todoListDatabaseHelper;
    private TodoListRVAdapter todoListRVAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        todoListDatabaseHelper=new TodoListDatabaseHelper(this);
        saveTodoList=findViewById(R.id.saveTodoList);
        addItemTodo=findViewById(R.id.TodoAddItemAddBtn);
        addItemET=findViewById(R.id.TodoAddItemET);
        List<TodoListData> todoListData=loadTodoListFromDB(todoID);
        headingTodoListET=findViewById(R.id.headingTodoList);
        boolean isNewNote = getIntent().getBooleanExtra("isNewNote", false);
        todoID=getIntent().getLongExtra("noteId",-1);
        if(todoID!=-1){
            isExisting=true;
        }
        saveTodoList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if ((headingTodoListET.getText().toString().trim().isEmpty())&& (todoListRVAdapter.getItemCount()==0))
                    finish();
                else {
                    if(isExisting){
                        updateNoteInDatabase(todoID,addItemET.getText().toString());
                        finish();
                    }
                    else {
                        saveTextNoteToDatabase(addItemET.getText().toString(), 9);
                        Log.d("SaveButton", "Save button clicked");
                        finish();
                    }
                }
            }
        });
    }

    @SuppressLint("Range")
    private String loadTodoHeadingFromDB(long todoID){
        String todoHead="";
        SQLiteDatabase sqLiteDatabase=todoListDatabaseHelper.getReadableDatabase();
        String[] projection={TodoListDatabaseHelper.COLUMN_TODO_HEADING};
        String selection = TextNoteDatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(todoID)};
        try(Cursor cursor= sqLiteDatabase.query(
                TodoListDatabaseHelper.TABLE_TODO_LIST,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        ) ){
            if (cursor != null && cursor.moveToFirst())
                todoHead = cursor.getString(cursor.getColumnIndex(TodoListDatabaseHelper.COLUMN_TODO_HEADING));
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"An error occurred", Toast.LENGTH_SHORT).show();
        }
        return todoHead;
    }

    private List<TodoListData> loadTodoListFromDB(long todoID){
        List<TodoListData> todoListData=new ArrayList<>();
        SQLiteDatabase sqLiteDatabase=todoListDatabaseHelper.getReadableDatabase();
        String[] projection = {
                TodoListDatabaseHelper.COLUMN_ID,
                TodoListDatabaseHelper.COLUMN_LIST_ID,
                TodoListDatabaseHelper.COLUMN_TODO_ITEM,
                TodoListDatabaseHelper.COLUMN_IS_COMPLETED
        };
        Cursor cursor=sqLiteDatabase.query(
                TodoListDatabaseHelper.TABLE_TODO_LIST_ITEMS,
                projection,
                null,
                null,
                null,
                null,
                TodoListDatabaseHelper.COLUMN_LIST_ID + " DESC"
        );
        if (cursor != null&&cursor.moveToFirst()){
            do{
                @SuppressLint("Range") long id=cursor.getLong(cursor.getColumnIndex(TodoListDatabaseHelper.COLUMN_LIST_ID));
                @SuppressLint("Range") String items=cursor.getString(cursor.getColumnIndex(TodoListDatabaseHelper.COLUMN_TODO_ITEM));
                todoListData.add(new TodoListData(id, items));
            }while (cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        sqLiteDatabase.close();
        return todoListData;
    }
    private void saveTextNoteToDatabase(String todoListHead, int itemNo){
        SQLiteDatabase sqLiteDatabase=todoListDatabaseHelper.getWritableDatabase();
        ContentValues contentValuesText=new ContentValues();
        contentValuesText.put(TodoListDatabaseHelper.COLUMN_TODO_HEADING, todoListHead);

        long rowID=sqLiteDatabase.insertWithOnConflict(
                TodoListDatabaseHelper.TABLE_TODO_LIST,
                null,
                contentValuesText,
                SQLiteDatabase.CONFLICT_REPLACE
        );


        if (rowID!=-1)
            Toast.makeText(this, "Note Saved",Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show();

        sqLiteDatabase.close();
        finish();
    }

    private void updateNoteInDatabase(long todoID, String todoItem) {
        SQLiteDatabase sqLiteDatabase=todoListDatabaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(TodoListDatabaseHelper.COLUMN_TODO_ITEM, todoItem);
        int rowsAffected = sqLiteDatabase.update(TodoListDatabaseHelper.TABLE_TODO_LIST_ITEMS, contentValues,
                TodoListDatabaseHelper.COLUMN_LIST_ID+"=?",
                new String[]{String.valueOf(todoID)});
        sqLiteDatabase.close();
    }
}
