package com.byted.camp.todolist.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.byted.camp.todolist.NoteOperator;
import com.byted.camp.todolist.R;
import com.byted.camp.todolist.beans.Note;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);

    private final NoteOperator operator;

    private TextView item_id;
    private ImageView item_image;
    private TextView item_title;
    private TextView item_filename;
    private TextView item_content;
    private TextView item_deadline_date;
    private ImageView item_priority_a;
    private ImageView item_priority_b;
    private ImageView item_priority_c;
    private ImageView item_priority_d;
    private ImageView item_priority_none;

    public NoteViewHolder(@NonNull View itemView, NoteOperator operator) {
        super(itemView);
        this.operator = operator;

        item_id = itemView.findViewById(R.id.item_id);
        item_image = itemView.findViewById(R.id.item_image);
        item_title = itemView.findViewById(R.id.item_title);
        item_filename = itemView.findViewById(R.id.item_filename);
        item_content = itemView.findViewById(R.id.item_content);
        item_deadline_date = itemView.findViewById(R.id.item_deadline_date);
        item_priority_a = itemView.findViewById(R.id.item_priority_A);
        item_priority_b = itemView.findViewById(R.id.item_priority_B);
        item_priority_c = itemView.findViewById(R.id.item_priority_C);
        item_priority_d = itemView.findViewById(R.id.item_priority_D);
        item_priority_none = itemView.findViewById(R.id.item_priority_none);
    }

    public void bind(final Note note) {
        item_id.setText(note.getID()+"");
        item_title.setText(note.getCaption());
        item_filename.setText("<"+note.getFilename()+">");
        item_content.setText(note.getContent());
        item_deadline_date.setText(note.getDeadline());

        switch (note.getPriority()) {
            case 1:
                item_priority_a.setVisibility(View.VISIBLE);
                item_priority_b.setVisibility(View.INVISIBLE);
                item_priority_c.setVisibility(View.INVISIBLE);
                item_priority_d.setVisibility(View.INVISIBLE);
                item_priority_none.setVisibility(View.INVISIBLE);
                break;
            case 2:
                item_priority_a.setVisibility(View.INVISIBLE);
                item_priority_b.setVisibility(View.VISIBLE);
                item_priority_c.setVisibility(View.INVISIBLE);
                item_priority_d.setVisibility(View.INVISIBLE);
                item_priority_none.setVisibility(View.INVISIBLE);
                break;
            case 3:
                item_priority_a.setVisibility(View.INVISIBLE);
                item_priority_b.setVisibility(View.INVISIBLE);
                item_priority_c.setVisibility(View.VISIBLE);
                item_priority_d.setVisibility(View.INVISIBLE);
                item_priority_none.setVisibility(View.INVISIBLE);
                break;
            case 4:
                item_priority_a.setVisibility(View.INVISIBLE);
                item_priority_b.setVisibility(View.INVISIBLE);
                item_priority_c.setVisibility(View.INVISIBLE);
                item_priority_d.setVisibility(View.VISIBLE);
                item_priority_none.setVisibility(View.INVISIBLE);
                break;
            case 5:
                item_priority_a.setVisibility(View.INVISIBLE);
                item_priority_b.setVisibility(View.INVISIBLE);
                item_priority_c.setVisibility(View.INVISIBLE);
                item_priority_d.setVisibility(View.INVISIBLE);
                item_priority_none.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        if(note.getDeadline() == null)
            Log.d("deadline","null");
        else
            Log.d("deadline",note.getDeadline());
    }
}
