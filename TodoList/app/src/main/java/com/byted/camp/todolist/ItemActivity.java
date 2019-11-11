package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.byted.camp.todolist.PickerView.CustomDatePicker;
import com.byted.camp.todolist.PickerView.CustomFutureDatePicker;
import com.byted.camp.todolist.PickerView.CustomLoopPicker;
import com.byted.camp.todolist.PickerView.CustomPriorityPicker;
import com.byted.camp.todolist.PickerView.CustomStatePicker;
import com.byted.camp.todolist.PickerView.DateFormatUtils;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ItemActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText item_content;
    private EditText item_title;
    private EditText item_filename;
    private TextView item_state;
    private TextView item_priority;
    private EditText item_tag;
    private TextView item_scheduled_date;
    private TextView item_deadline_date;
    private TextView item_show_date;
    private TextView item_loop;
    private Switch item_switch;
    private TextView bar_commit,bar_back;
    private myOnClick item_loop_clickListener;

    private CustomLoopPicker mLoopPicker;
    private CustomStatePicker mStatePicker;
    private CustomPriorityPicker mPriorityPicker;
    private CustomDatePicker mScheduledDatePicker;
    private CustomFutureDatePicker mDeadlineDatePicker;
    private CustomFutureDatePicker mShowDatePicker;

    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;
    private long timeStamp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        //透明状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();

        item_content = findViewById(R.id.item_content);
        item_title = findViewById(R.id.item_title);
        item_filename = findViewById(R.id.item_filename);
        item_state = findViewById(R.id.item_state);
        item_priority = findViewById(R.id.item_priority);
        item_tag = findViewById(R.id.item_tag);
        item_scheduled_date = findViewById(R.id.item_sheduled_date);
        item_deadline_date = findViewById(R.id.item_deadline_date);
        item_show_date = findViewById(R.id.item_show_date);
        item_loop = findViewById(R.id.item_loop);
        item_switch = findViewById(R.id.item_switch);
        bar_commit = findViewById(R.id.bar_commit);
        bar_back = findViewById(R.id.bar_back);
        item_loop_clickListener = new myOnClick();

        item_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if(isChecked){
                    item_loop.setText("每天");
                    item_loop.setOnClickListener(item_loop_clickListener);
                }
                else{
                    item_loop.setText("不重复");
                    item_loop.setOnClickListener(null);
                }
            }
        });

        item_state.setOnClickListener(this);
        item_priority.setOnClickListener(this);
        item_tag.setOnClickListener(this);
        item_scheduled_date.setOnClickListener(this);
        item_deadline_date.setOnClickListener(this);
        item_show_date.setOnClickListener(this);

        bar_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long diff=System.currentTimeMillis()-timeStamp;
                if(diff<500) return;
                timeStamp=System.currentTimeMillis();

                CharSequence content = item_content.getText();
                String filename = item_filename.getText().toString().trim();
                String title = item_title.getText().toString().trim();
                String deadline = item_deadline_date.getText().toString().trim();
                String scheduled = item_scheduled_date.getText().toString().trim();
                String show = item_show_date.getText().toString().trim();
                String tag = item_tag.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(ItemActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim(),filename,title,tag,deadline,scheduled,show,
                        getSelectedState(),
                        getSelectedPriority());
                if (succeed) {
                    Toast.makeText(ItemActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(ItemActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                //insert(content.toString().trim(),fileText.getText().toString().trim(), getSelectedPriority());
                finish();
            }
        });

        initLoopPicker();
        initStatePicker();
        initPriorityPicker();
        initScheduledDatePicker();
        initDeadlineDatePicker();
        initShowDatePicker();
    }

    public Boolean saveNote2Database(String content, String filename, String title, String tag, String deadline, String scheduled,
                                     String show, int state, int priority){
        if(database==null||TextUtils.isEmpty(content)){
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoNote.COLUMN_DEADLINE,deadline);
        values.put(TodoContract.TodoNote.COLUMN_SHOW,show);
        values.put(TodoContract.TodoNote.COLUMN_STATE,state);
        values.put(TodoContract.TodoNote.COLUMN_SCHEDULED,scheduled);
        values.put(TodoContract.TodoNote.COLUMN_WEEK,getDayofWeek(scheduled));
        values.put(TodoContract.TodoNote.COLUMN_WEEKLOP,0);
        values.put(TodoContract.TodoNote.COLUMN_MONTHLOP,0);
        values.put(TodoContract.TodoNote.COLUMN_YEARLOP,0);
        values.put(TodoContract.TodoNote.COLUMN_CAPTION,title);
        values.put(TodoContract.TodoNote.COLUMN_FILE,filename);
        values.put(TodoContract.TodoNote.COLUMN_TAG,tag);
        values.put(TodoContract.TodoNote.COLUMN_CONTENT,content);
        values.put(TodoContract.TodoNote.COLUMN_PRIORITY,priority);
        long rowId = database.insert(TodoContract.TodoNote.TABLE_NAME, null, values);
        return rowId!=-1;
    }

    public int getSelectedState(){
        String state = item_state.getText().toString();
        switch (state){
            case "TODO": return 1;
            case "DONE": return 2;
            default:return 0;
        }
    }

    public int getSelectedPriority(){
        String Priority = item_priority.getText().toString();
        switch(Priority){
            case "A": return 1;
            case "B": return 2;
            case "C": return 3;
            case "D": return 4;
            default: return 5;
        }
    }

    public class myOnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.item_loop:
                    mLoopPicker.show(item_loop.getText().toString());
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_show_date:
                mShowDatePicker.show(item_show_date.getText().toString());
                break;
            case R.id.item_deadline_date:
                mDeadlineDatePicker.show(item_deadline_date.getText().toString());
                break;
            case R.id.item_sheduled_date:
                mScheduledDatePicker.show(item_scheduled_date.getText().toString());
                break;
            case R.id.item_state:
                mStatePicker.show(item_state.getText().toString());
                break;
            case R.id.item_priority:
                mPriorityPicker.show(item_priority.getText().toString());
                break;
            case R.id.item_tag:
                break;
        }
    }

    private void initLoopPicker(){
        item_loop.setText("不重复");

        mLoopPicker = new CustomLoopPicker(this, new CustomLoopPicker.Callback() {
            @Override
            public void onLoopSelected(String loop) {
                item_loop.setText(loop);
            }
        });
        // 允许点击屏幕或物理返回键关闭
        mLoopPicker.setCancelable(true);
        // 允许循环滚动
        mLoopPicker.setScrollLoop(true);
    }

    private void initStatePicker(){
        item_state.setText("None");

        mStatePicker = new CustomStatePicker(this, new CustomStatePicker.Callback() {
            @Override
            public void onStateSelected(String state) {
                item_state.setText(state);
            }
        });
        // 允许点击屏幕或物理返回键关闭
        mStatePicker.setCancelable(true);
        // 不允许循环滚动
        mStatePicker.setScrollLoop(false);
    }

    private void initPriorityPicker(){
        item_priority.setText("None");

        mPriorityPicker = new CustomPriorityPicker(this, new CustomPriorityPicker.Callback() {
            @Override
            public void onPrioritySelected(String priority) {
                item_priority.setText(priority);
            }
        });
        // 允许点击屏幕或物理返回键关闭
        mPriorityPicker.setCancelable(true);
        // 允许循环滚动
        mPriorityPicker.setScrollLoop(true);
    }

    private void initScheduledDatePicker() {
        long beginTimestamp = DateFormatUtils.str2Long("2010-05-01", false);
        long endTimestamp = System.currentTimeMillis();

        item_scheduled_date.setText(DateFormatUtils.long2Str(endTimestamp, false));

        // 通过时间戳初始化日期，毫秒级别
        mScheduledDatePicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                item_scheduled_date.setText(DateFormatUtils.long2Str(timestamp, false));
            }
        }, beginTimestamp, endTimestamp);
        // 允许点击屏幕或物理返回键关闭
        mScheduledDatePicker.setCancelable(true);
        // 不显示时和分
        mScheduledDatePicker.setCanShowPreciseTime(false);
        // 不允许循环滚动
        mScheduledDatePicker.setScrollLoop(false);
        // 不允许滚动动画
        mScheduledDatePicker.setCanShowAnim(false);
    }

    private void initDeadlineDatePicker() {
        long beginTimestamp = System.currentTimeMillis();
        long endTimestamp = System.currentTimeMillis();

        item_deadline_date.setText(DateFormatUtils.long2Str(beginTimestamp, false));

        // 通过时间戳初始化日期，毫秒级别
        mDeadlineDatePicker = new CustomFutureDatePicker(this, new CustomFutureDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                item_deadline_date.setText(DateFormatUtils.long2Str(timestamp, false));
            }
        }, beginTimestamp, endTimestamp);
        // 允许点击屏幕或物理返回键关闭
        mDeadlineDatePicker.setCancelable(true);
        // 不显示时和分
        mDeadlineDatePicker.setCanShowPreciseTime(false);
        // 不允许循环滚动
        mDeadlineDatePicker.setScrollLoop(false);
        // 不允许滚动动画
        mDeadlineDatePicker.setCanShowAnim(false);
    }

    private void initShowDatePicker() {
        long beginTimestamp = System.currentTimeMillis();
        long endTimestamp = System.currentTimeMillis();

        item_show_date.setText(DateFormatUtils.long2Str(beginTimestamp, false));

        // 通过时间戳初始化日期，毫秒级别
        mShowDatePicker = new CustomFutureDatePicker(this, new CustomFutureDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                item_show_date.setText(DateFormatUtils.long2Str(timestamp, false));
            }
        }, beginTimestamp, endTimestamp);
        // 允许点击屏幕或物理返回键关闭
        mShowDatePicker.setCancelable(true);
        // 不显示时和分
        mShowDatePicker.setCanShowPreciseTime(false);
        // 不允许循环滚动
        mShowDatePicker.setScrollLoop(false);
        // 不允许滚动动画
        mShowDatePicker.setCanShowAnim(false);
    }

    //偏移量1-7表示周日一二三四五六
    private int getDayofWeek(String dateTime) {
        Calendar cal = Calendar.getInstance();

        if (dateTime.equals("")) {
            cal.setTime(new Date(System.currentTimeMillis()));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date;
            try {
                date = sdf.parse(dateTime);
            } catch (ParseException e) {
                date = null;
                e.printStackTrace();
            }
            if (date != null) {
                cal.setTime(new Date(date.getTime()));
            }
        }
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoopPicker.onDestroy();
        mStatePicker.onDestroy();
        mPriorityPicker.onDestroy();
        mScheduledDatePicker.onDestroy();
        mDeadlineDatePicker.onDestroy();
        mShowDatePicker.onDestroy();
        database.close();
        database = null;
        dbHelper.close();
        dbHelper = null;
    }
}












