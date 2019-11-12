package com.byted.camp.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.extra.DoubleBack;
import com.byted.camp.todolist.ui.NoteListAdapter;
import com.byted.camp.todolist.ui.NoteListAdapterForFiles;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FilesActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ADD = 1002;
    DoubleBack doubleBack=new DoubleBack();

    private Toolbar toolbar;
    private LinearLayout buttonAgenda,buttonTodo,buttonFiles,buttonSettings;
    private FloatingActionButton fab;

    private EditText searchText;
    private Button button_showAll, button_search;
    private RecyclerView recyclerView;

    private String searchString="";

    private NoteListAdapterForFiles notesAdapter;
    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;

    private List<String> filenameSet = new ArrayList<>();
    private List<Note> noteSet = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        buttonAgenda = findViewById(R.id.button_agenda);
        buttonTodo = findViewById(R.id.button_todo);
        buttonFiles = findViewById(R.id.button_files);
        buttonSettings = findViewById(R.id.button_settings);

        bindActivity(R.id.button_agenda, AgendaActivity.class);
        bindActivity(R.id.button_todo,TodoActivity.class);
        bindActivity(R.id.button_settings, SettingsActivity.class);

        searchText = findViewById(R.id.search_input);
        button_showAll = findViewById(R.id.button_showAll);
        button_search = findViewById(R.id.button_search);

        button_showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchString = "";
                noteSet = loadNotesFromDatabase();
                filenameSet.clear();
                for(Note temp: noteSet){
                    if(!filenameSet.contains(temp.getFilename()))
                        filenameSet.add(temp.getFilename());
                }
                notesAdapter.refresh(filenameSet);
            }
        });

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchString = searchText.getText().toString();
                noteSet = loadNotesFromDatabase();
                filenameSet.clear();
                for(Note temp: noteSet){
                    if(!filenameSet.contains(temp.getFilename()))
                        filenameSet.add(temp.getFilename());
                }
                notesAdapter.refresh(filenameSet);
            }
        });

        recyclerView = findViewById(R.id.list_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapterForFiles(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {}
            @Override
            public void updateNote(Note note) {}
        });
        notesAdapter.setOnItemClickListener(new NoteListAdapterForFiles.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView item_filename = view.findViewById(R.id.item_filename);
                String filename = item_filename.getText().toString();

                Intent intent = new Intent(FilesActivity.this,FileInfActivity.class);
                intent.putExtra("filename",filename);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        noteSet = loadNotesFromDatabase();
        filenameSet.clear();
        for(Note temp: noteSet){
            if(!filenameSet.contains(temp.getFilename()))
                filenameSet.add(temp.getFilename());
        }
        notesAdapter.refresh(filenameSet);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivityForResult(
                        new Intent(FilesActivity.this, ItemActivity.class),
                        REQUEST_CODE_ADD);
            }
        });
    }

    private List<Note> loadNotesFromDatabase() {
        if (database == null) {
            Log.d("null","null");
            return Collections.emptyList();
        }
        List<Note> result = new LinkedList<>();
        Cursor cursor = null;
        try {
//            cursor = database.query(TodoContract.TodoNote.TABLE_NAME,
//                    null,null,null,null,null,null);
            if(searchString==""){
                cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                        null, null,
                        null, null,
                        TodoContract.TodoNote.COLUMN_FILE);
            }
            else{
                cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                        "file like ?", new String[]{searchString},
                        null, null,
                        TodoContract.TodoNote.COLUMN_FILE);
            }

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote._ID));
                String caption = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CAPTION));
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CONTENT));
                String intState = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_STATE));
                int intPriority = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_PRIORITY));
                String fileName = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_FILE));
                String deadline = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_DEADLINE));
                Note note = new Note(id);
                note.setContent(content);
                note.setCaption(caption);
                note.setState(intState);
                note.setPriority(intPriority);
                note.setFilename(fileName);
                note.setDeadline(deadline);
                result.add(note);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Log.d("resultSize",result.size()+"");
        return result;
    }



    private void bindActivity(final int btnId, final Class<?> activityClass){
        findViewById(btnId).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FilesActivity.this, activityClass);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //实现Home键效果
        //super.onBackPressed();这句话一定要注掉,不然又去调用默认的back处理方式了
        long nowTime=System.currentTimeMillis();
        long minusTime=nowTime-doubleBack.getFirstclickTime();
        if(minusTime > 2000){
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_LONG).show();
            doubleBack.setFirstClickTime(nowTime);
        }
        else{
            Intent intent = new Intent(FilesActivity.this,ExitActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
