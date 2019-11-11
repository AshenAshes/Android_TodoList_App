package com.byted.camp.todolist.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.byted.camp.todolist.NoteOperator;
import com.byted.camp.todolist.R;
import com.byted.camp.todolist.beans.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private final NoteOperator operator;
    private final List<Note> notes = new ArrayList<>();

    public NoteListAdapter(NoteOperator operator) {
        this.operator = operator;
    }

    public void refresh(List<Note> newNotes) {
        notes.clear();
        if (newNotes != null) {
            notes.addAll(newNotes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View itemView;

        Log.d("state:",notes.get(pos).getState()+"");
        if(notes.get(pos).getState().equals("Todo")){//To do
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_todo, parent, false);
        }
        else if(notes.get(pos).getState().equals("Done")){//done
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_done, parent, false);
        }
        else{//none
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_none, parent, false);
        }
        return new NoteViewHolder(itemView, operator);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int pos) {
        holder.bind(notes.get(pos));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
