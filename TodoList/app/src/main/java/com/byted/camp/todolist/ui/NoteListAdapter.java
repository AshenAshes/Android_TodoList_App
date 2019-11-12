package com.byted.camp.todolist.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;


import com.byted.camp.todolist.NoteOperator;
import com.byted.camp.todolist.R;
import com.byted.camp.todolist.beans.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteViewHolder> implements View.OnClickListener {

    private final NoteOperator operator;
    private final List<Note> notes = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public NoteListAdapter(NoteOperator operator) {
        this.operator = operator;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public void refresh(List<Note> newNotes) {
        notes.clear();
        if (newNotes != null) {
            notes.addAll(newNotes);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        //Implement your logic here
        Note note = notes.get(position);
        if(note.getState().equals("Todo"))
            return 1;
        else if(note.getState().equals("Done"))
            return 2;
        else
            return 0;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View itemView;

        if(pos == 1){//To do
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_todo, parent, false);
        }
        else if(pos == 2){//done
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_done, parent, false);
        }
        else{//none
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_none, parent, false);
        }

        itemView.setOnClickListener(this);
        return new NoteViewHolder(itemView, operator);
    }

    @Override
    public void onClick(View view) {
        if(mOnItemClickListener != null)
            mOnItemClickListener.onItemClick(view, (int)view.getTag());
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int pos) {
        holder.itemView.setTag(pos);
        holder.bind(notes.get(pos));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
