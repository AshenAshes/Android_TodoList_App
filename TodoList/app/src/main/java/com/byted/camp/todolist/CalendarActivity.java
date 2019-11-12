package com.byted.camp.todolist;

import android.os.Bundle;

import com.necer.calendar.Miui9Calendar;
import com.necer.enumeration.MultipleNumModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity {
    Miui9Calendar myCalendar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        myCalendar = findViewById(R.id.myCalendar);

        myCalendar.setMultipleNum(1, MultipleNumModel.FULL_REMOVE_FIRST);

    }
}
