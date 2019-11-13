package com.byted.camp.todolist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.ui.NoteListAdapter;
import com.necer.calendar.BaseCalendar;
import com.necer.calendar.Miui9Calendar;
import com.necer.enumeration.MultipleNumModel;
import com.necer.listener.OnCalendarChangedListener;
import com.necer.listener.OnCalendarMultipleChangedListener;
import com.necer.painter.InnerPainter;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarActivity extends AppCompatActivity {
    Miui9Calendar myCalendar;
    RecyclerView recyclerView;
    ImageView image;
    TextView date;
    ImageView today;
    ImageView back;

    private NoteListAdapter notesAdapter;
    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;
    private boolean isResumeFirst = false;

    private LocalDate selectedDate;
    private List<LocalDate> wholeMonthDate = new ArrayList<>();
    private List<String> markedDate = new ArrayList<>();
    private String mDate;
    private String tempDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        myCalendar = findViewById(R.id.myCalendar);
        myCalendar.setMultipleNum(1, MultipleNumModel.FULL_REMOVE_FIRST);
        image = findViewById(R.id.img);
        recyclerView = findViewById(R.id.recyclerView);
        date = findViewById(R.id.item_date);
        today = findViewById(R.id.bar_today);
        back = findViewById(R.id.bar_back);

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCalendar.toToday();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        selectedDate = myCalendar.getAllSelectDateList().get(0);
        mDate = localDate2DateString(selectedDate);
        date.setText(mDate);

        myCalendar.setOnCalendarMultipleChangedListener(new OnCalendarMultipleChangedListener() {
            @Override
            public void onCalendarChange(BaseCalendar baseCalendar, int year, int month, List<LocalDate> currectSelectList, List<LocalDate> allSelectList) {
                selectedDate = baseCalendar.getAllSelectDateList().get(0);
                mDate = localDate2DateString(selectedDate);
                date.setText(mDate);

                notesAdapter.refresh(loadNotesFromDatabase());
                if(notesAdapter.getItemCount() == 0)
                    image.setVisibility(View.VISIBLE);
                else
                    image.setVisibility(View.GONE);

                wholeMonthDate.addAll(myCalendar.getCurrectDateList());
                markedDate.clear();
                for(LocalDate temp:wholeMonthDate){
                    tempDate = localDate2DateString(temp);
                    if(!loadNotesFromDatabase(tempDate).isEmpty())
                        markedDate.add(tempDate);
                }
                InnerPainter innerPainter = (InnerPainter)myCalendar.getCalendarPainter();
                innerPainter.setPointList(markedDate);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {}
            @Override
            public void updateNote(Note note) {}
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
        if(notesAdapter.getItemCount() == 0)
            image.setVisibility(View.VISIBLE);
        else
            image.setVisibility(View.GONE);
    }

    public String localDate2DateString(LocalDate localDate){
        String year,month,day;
        year=localDate.getValue(0)+"";
        month=localDate.getValue(1)+"";
        day=localDate.getValue(2)+"";

        return year+"-"+month+"-"+day;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isResumeFirst){
            notesAdapter.refresh(loadNotesFromDatabase());
            if(notesAdapter.getItemCount() == 0)
                image.setVisibility(View.VISIBLE);
            else
                image.setVisibility(View.GONE);
        }
        isResumeFirst = false;
    }

    private List<Note> loadNotesFromDatabase() {
        if (database == null) {
            return Collections.emptyList();
        }
        List<Note> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                    "scheduled like ?", new String[]{mDate},
                    null, null,
                    TodoContract.TodoNote.COLUMN_PRIORITY);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote._ID));
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CONTENT));
                String caption = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CAPTION));
                String intState = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_STATE));
                int intPriority = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_PRIORITY));
                String fileName = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_FILE));
                String deadline = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_DEADLINE));
                Note note = new Note(id);
                note.setContent(content);
                note.setCaption(caption);
                note.setState(intState);
                note.setPriority(intPriority);
                note.setDeadline(deadline);
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


    private List<Note> loadNotesFromDatabase(String date) {
        if (database == null) {
            return Collections.emptyList();
        }
        List<Note> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                    "scheduled like ?", new String[]{date},
                    null, null,
                    TodoContract.TodoNote.COLUMN_PRIORITY);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote._ID));
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CONTENT));
                String caption = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CAPTION));
                String intState = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_STATE));
                int intPriority = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_PRIORITY));
                String fileName = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_FILE));
                String deadline = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_DEADLINE));
                Note note = new Note(id);
                note.setContent(content);
                note.setCaption(caption);
                note.setState(intState);
                note.setPriority(intPriority);
                note.setDeadline(deadline);
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
