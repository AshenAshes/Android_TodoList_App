package com.byted.camp.todolist.ui;

import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

public class NoteViewHolder extends RecyclerView.ViewHolder {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);

    private final NoteOperator operator;

//    private CheckBox checkBox;
//    private TextView contentText;
//    private TextView dateText;
//    private View deleteBtn;
    private ImageView item_image;
    private TextView item_title;
    private TextView item_filename;
    private TextView item_content;

    public NoteViewHolder(@NonNull View itemView, NoteOperator operator) {
        super(itemView);
        this.operator = operator;

        item_image = itemView.findViewById(R.id.item_image);
        item_title = itemView.findViewById(R.id.item_title);
        item_filename = itemView.findViewById(R.id.item_filename);
        item_content = itemView.findViewById(R.id.item_content);
//        checkBox = itemView.findViewById(R.id.checkbox);
//        contentText = itemView.findViewById(R.id.text_content);
//        dateText = itemView.findViewById(R.id.text_date);
//        deleteBtn = itemView.findViewById(R.id.btn_delete);
    }

    public void bind(final Note note) {
        item_title.setText(note.getCaption());
//        item_filename.setText(note.getFilename());
        item_content.setText(note.getContent());

//        contentText.setText(note.getContent());
//        dateText.setText(SIMPLE_DATE_FORMAT.format(note.getScheduled()));
//        dateText.setText(note.getDate());
//        checkBox.setOnCheckedChangeListener(null);
//        checkBox.setChecked(note.getState() == State.DONE);
//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                note.setState(isChecked ? State.DONE : State.TODO);
//                operator.updateNote(note);
//            }
//        });
//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                operator.deleteNote(note);
//            }
//        });
//
//        if (note.getState() == State.DONE) {
//            contentText.setTextColor(Color.GRAY);
//            contentText.setPaintFlags(contentText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//        } else {
//            contentText.setTextColor(Color.BLACK);
//            contentText.setPaintFlags(contentText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
//        }

//        itemView.setBackgroundColor(note.getPriority().color);
    }
}
