package com.byted.camp.todolist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import static com.byted.camp.todolist.db.TodoContract.TodoNote.TABLE_NAME;

public class ExitActivity extends AppCompatActivity {
    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;
    private final List<Note> notes = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();
        List<Note> newNotes = loadNotesFromDatabase();
        FileOutputStream out = null;
        BufferedWriter writer = null;
        if(newNotes!=null){
            notes.addAll(newNotes);
            String fileN = null;

            for(int i=0;i<notes.size();i++){
                Log.d("i",i+"");
                String filename = notes.get(i).getFilename();
                try {
                    if(!(fileN.equals(filename))){
                        File file = new File(filename);
                        if(file.exists()){
                            file.delete();
                            Log.d("write", "delete: ");
                        }
                    }
                    out = openFileOutput(filename, Context.MODE_APPEND);//"data"为文件名，第二个参数为文件操作模式：文件已经存在，就往文件里面追加类容，不从新创建文件。
                    writer = new BufferedWriter(new OutputStreamWriter(out));
                    writer.write("* "+notes.get(i).getState()+" [#"+getSelectedPriority(notes.get(i).getPriority())+"] "+notes.get(i).getCaption());
                    writer.newLine();
                    writer.write("DEADLINE:<"+notes.get(i).getDeadline()+">");
                    writer.newLine();
                    writer.write("SCHEDULED:<"+notes.get(i).getScheduled()+">");
                    writer.newLine();
                    writer.write("<"+notes.get(i).getShow()+">");
                    writer.newLine();
                    writer.write(notes.get(i).getContent());
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (writer != null) {
                            Log.d("write", "onCreate: "+i);
                            writer.flush();
                            out.close();
                            writer.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //database.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        finish();
    }
    public String getSelectedPriority(int priority){
        switch(priority){
            case 1: return "A";
            case 2: return "B";
            case 3: return "C";
            case 4: return "D";
            default: return "None";
        }
    }
    private List<Note> loadNotesFromDatabase() {
        if (database == null) {
            return Collections.emptyList();
        }
        List<Note> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(TodoContract.TodoNote.TABLE_NAME,
                    null,null,null,null,null, TodoContract.TodoNote.COLUMN_FILE);
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote._ID));
                String caption = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CAPTION));
                String deadline = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_DEADLINE));
                String show = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_SHOW));
                String scheduled = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_SCHEDULED));
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CONTENT));
                String intState = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_STATE));
                int intPriority = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_PRIORITY));
                String fileName = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_FILE));

                Note note = new Note(id);
                note.setScheduled(scheduled);
                note.setShow(show);
                note.setDeadline(deadline);
                note.setContent(content);
                note.setCaption(caption);
                note.setState(intState);
                note.setPriority(intPriority);
                note.setFilename(fileName);
                result.add(note);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }
}
