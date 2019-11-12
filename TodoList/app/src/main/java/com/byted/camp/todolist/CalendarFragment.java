package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.ui.NoteListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CalendarFragment extends Fragment {
    private static final int REQUEST_CODE_ADD = 1002;
    private View view;
    private ImageView image;
    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;
    private String date;
    private boolean isResumeFirst = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar,container,false);
        dbHelper = new TodoDbHelper(this.getContext());
        database = dbHelper.getWritableDatabase();
        image = view.findViewById(R.id.image_noitem);
        recyclerView = view.findViewById(R.id.list_items);
        Context context = getActivity();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {}
            @Override
            public void updateNote(Note note) {}
        });
        notesAdapter.setOnItemClickListener(new NoteListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView item_id = view.findViewById(R.id.item_id);
                String id = item_id.getText().toString();

                Intent intent = new Intent(getActivity(),EditItemActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
        if(notesAdapter.getItemCount() == 0)
            image.setVisibility(View.VISIBLE);
        else
            image.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isResumeFirst){
            notesAdapter.refresh(loadNotesFromDatabase());
            if(notesAdapter.getItemCount() == 0)
                image.setVisibility(View.VISIBLE);
            else
                image.setVisibility(View.INVISIBLE);
        }

        isResumeFirst = false;
    }

    public void setDate(String date_in){
        date = date_in;
    }

    private List<Note> loadNotesFromDatabase() {
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
