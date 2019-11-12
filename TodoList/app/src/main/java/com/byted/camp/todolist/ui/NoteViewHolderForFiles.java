package com.byted.camp.todolist.ui;

import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.byted.camp.todolist.NoteOperator;
import com.byted.camp.todolist.R;
import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NoteViewHolderForFiles extends RecyclerView.ViewHolder {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);

    private final NoteOperator operator;

    private TextView item_filename;

    public NoteViewHolderForFiles(@NonNull View itemView, NoteOperator operator) {
        super(itemView);
        this.operator = operator;

        item_filename = itemView.findViewById(R.id.item_filename);
    }

    public void bind(String filename) {
        item_filename.setText(filename);
    }
}
