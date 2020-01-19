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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoteListAdapterForFiles extends RecyclerView.Adapter<NoteViewHolderForFiles> implements View.OnClickListener {

    private final NoteOperator operator;
    private final List<String> filenames = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public NoteListAdapterForFiles(NoteOperator operator) {
        this.operator = operator;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public void refresh(List<String> newFilenames) {
        filenames.clear();
        if (newFilenames != null) {
            filenames.addAll(newFilenames);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolderForFiles onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent,false);

        itemView.setOnClickListener(this);
        return new NoteViewHolderForFiles(itemView, operator);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolderForFiles holder, int position) {
        holder.itemView.setTag(position);
        holder.bind(filenames.get(position));
    }

    @Override
    public void onClick(View view) {
        if(mOnItemClickListener != null)
            mOnItemClickListener.onItemClick(view, (int)view.getTag());
    }

    @Override
    public int getItemCount() {
        return filenames.size();
    }
}
