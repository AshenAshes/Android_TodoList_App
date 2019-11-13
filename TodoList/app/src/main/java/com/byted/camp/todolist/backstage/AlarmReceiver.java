package com.byted.camp.todolist.backstage;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.byted.camp.todolist.db.TodoContract.TodoNote.TABLE_NAME;

public class AlarmReceiver extends BroadcastReceiver {
    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;
    private Context mContent;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("file", "onReceive: ");
        this.mContent=context;
        dbHelper = new TodoDbHelper(context);
        database = dbHelper.getWritableDatabase();
        writeFatherItem2File();

        //再次开启这个服务，从而可以
        Intent i = new Intent(context, FileWriteService.class);
        context.startService(i);
    }

    public void writeFatherItem2File(){
        List<String> fileNameList = getFilename();
        for(int i =0;i<fileNameList.size();i++){
            String thisFile = fileNameList.get(i);
            FileOutputStream out = null;
            BufferedWriter writer = null;
            try{
                out = mContent.openFileOutput(thisFile, Context.MODE_PRIVATE);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                writeItem(writer,thisFile,"None",1);
            }catch (IOException e) {
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

    public void writeItem(BufferedWriter writer, String file, String fatherItem, int count){
        List<Note> newNotes = loadNotesFromDatabase(file,fatherItem);
        for(int i = 0;i<newNotes.size();i++){
            Note newNote = newNotes.get(i);
            try{
                for(int j=0;j<count;j++){
                    writer.write("*");
                }
                writer.write(" "+newNote.getState()+" [#"+getSelectedPriority(newNote.getPriority())+"] "+newNote.getCaption());
                writer.newLine();
                writer.write("DEADLINE:<"+newNote.getDeadline()+">");
                writer.newLine();
                writer.write("SCHEDULED:<"+newNote.getScheduled()+">");
                writer.newLine();
                writer.write("<"+newNote.getShow()+">");
                writer.newLine();
                if(newNote.getState().equals("Done")){
                    writer.write("CLOSED:<"+newNote.getClosed()+">");
                    writer.newLine();
                }
                writer.write(newNote.getContent());
                writer.newLine();
            }catch (IOException e) {
                e.printStackTrace();
            }
            writeItem(writer,file,newNote.getCaption(),count+1);
        }
        return;
    }

    public List<String> getFilename(){
        if (database == null) {
            return Collections.emptyList();
        }
        List<String> result = new LinkedList<>();
        Cursor cursor = null;
        try{
            cursor = database.query(TABLE_NAME,new String[]{ TodoContract.TodoNote.COLUMN_FILE },null,null,
                    TodoContract.TodoNote.COLUMN_FILE,null, TodoContract.TodoNote.COLUMN_FILE);
            while (cursor.moveToNext()){

                String fileName = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_FILE));
                Log.d("file", "getFilename: "+fileName);
                result.add(fileName);
            }
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
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
    private List<Note> loadNotesFromDatabase(String file, String fatherItem) {
        if (database == null) {
            return Collections.emptyList();
        }
        List<Note> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(TodoContract.TodoNote.TABLE_NAME,
                    null,"("+ TodoContract.TodoNote.COLUMN_FILE+" = ? and "+ TodoContract.TodoNote.COLUMN_FATHERITEM+" = ? )",
                    new String[]{file,fatherItem},null,null,null);
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
                String closed = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CLOSED));
                Log.d("file",file+"+"+caption+"+"+fatherItem);
                Note note = new Note(id);
                note.setScheduled(scheduled);
                note.setClosed(closed);
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
