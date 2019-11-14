package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.byted.camp.todolist.Calendar.calendar.AdvanceTime;
import com.byted.camp.todolist.Calendar.calendar.CalendarEvent;
import com.byted.camp.todolist.Calendar.calendar.CalendarProviderManager;
import com.byted.camp.todolist.PickerView.CustomDatePicker;
import com.byted.camp.todolist.PickerView.CustomFatherItemPicker;
import com.byted.camp.todolist.PickerView.CustomFutureDatePicker;
import com.byted.camp.todolist.PickerView.CustomLoopPicker;
import com.byted.camp.todolist.PickerView.CustomPriorityPicker;
import com.byted.camp.todolist.PickerView.CustomStatePicker;
import com.byted.camp.todolist.PickerView.DateFormatUtils;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
    private TextView item_father_item;
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
    private CustomFatherItemPicker mFatherItemPicker;
    private CustomDatePicker mScheduledDatePicker;
    private CustomFutureDatePicker mDeadlineDatePicker;
    private CustomFutureDatePicker mShowDatePicker;

    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;
    private long timeStamp;

    private List<String> itemTitlesFromDatabase = new ArrayList<>();
    private databaseCallback fatherItemNewCallback;

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
        item_father_item = findViewById(R.id.item_fatheritem);
        item_scheduled_date = findViewById(R.id.item_sheduled_date);
        item_deadline_date = findViewById(R.id.item_deadline_date);
        item_show_date = findViewById(R.id.item_show_date);
        item_loop = findViewById(R.id.item_loop);
        item_switch = findViewById(R.id.item_switch);
        bar_commit = findViewById(R.id.bar_commit);
        bar_back = findViewById(R.id.bar_back);
        item_loop_clickListener = new myOnClick();

        item_filename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                item_father_item.setText("None");
                itemTitlesFromDatabase.clear();
                itemTitlesFromDatabase.addAll(getItem(item_filename.getText().toString()));
                fatherItemNewCallback.setOnDatabaseResult(itemTitlesFromDatabase);
            }
        });

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
        item_father_item.setOnClickListener(this);
        item_scheduled_date.setOnClickListener(this);
        item_deadline_date.setOnClickListener(this);
        item_show_date.setOnClickListener(this);

        bar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bar_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String closedTime;
                Calendar Time;
                int offset;
                String year,month,day,week,hour,minute;

                Time = Calendar.getInstance();
                Time.setTimeInMillis(System.currentTimeMillis());
                year = Time.get(Calendar.YEAR)+"";
                month = (Time.get(Calendar.MONTH) + 1)+"";
                day = Time.get(Calendar.DAY_OF_MONTH)+"";
                offset = Time.get(Calendar.DAY_OF_WEEK);
                switch (offset){
                    case 1: week = "Sun"; break;
                    case 2: week = "Mon"; break;
                    case 3: week = "Tue"; break;
                    case 4: week = "Wed"; break;
                    case 5: week = "Thur"; break;
                    case 6: week = "Fri"; break;
                    case 7: week = "Sat"; break;
                    default: week = "null"; break;
                }
                hour = Time.get(Calendar.HOUR_OF_DAY)+"";
                minute = Time.get(Calendar.MINUTE)+"";
                closedTime = year+"-"+month+"-"+day+" "+week+" "+hour+":"+minute;
                Log.d("Time",closedTime);

                long diff=System.currentTimeMillis()-timeStamp;
                if(diff<500) return;
                timeStamp=System.currentTimeMillis();

                CharSequence content = item_content.getText();
                String filename;
                Log.d("itemValue",item_filename.getText().toString().trim());
                if(!item_filename.getText().toString().equals(""))
                    filename = item_filename.getText().toString().trim();
                else
                    filename = "new file";
                String title;
                if(!item_title.getText().toString().equals(""))
                    title = item_title.getText().toString().trim();
                else
                    title = "Titled";
                String fatherItem = item_father_item.getText().toString();
                String deadline = item_deadline_date.getText().toString().trim();
                String scheduled = item_scheduled_date.getText().toString().trim();
                Log.d("deadline",deadline);
                Log.d("scheduled",scheduled);
                String show = item_show_date.getText().toString().trim();
                String tag = item_tag.getText().toString().trim();
                String repeat = item_loop.getText().toString().trim();

                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(ItemActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim(),filename,title,tag,deadline,scheduled,show,repeat,
                        item_state.getText().toString().trim(),
                        getSelectedPriority(),fatherItem,closedTime);//写入数据库
                if (succeed) {
                    Toast.makeText(ItemActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(ItemActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }


                String rRule="";
                String dateneed = deadline.replaceAll("-","");//重复规则的转换
                switch (repeat) {
                    case "每天":

                        rRule = "FREQ=DAILY;UNTIL="+dateneed+"T000000Z";
                        break;
                    case "每周":
                        rRule = "FREQ=WEEKLY;UNTIL="+dateneed+"T000000Z";
                        break;
                    case "每月":
                        rRule = "FREQ=MONTHLY;UNTIL="+dateneed+"T000000Z";
                        break;
                }

                CalendarEvent calendarEvent;
                calendarEvent = new CalendarEvent(title,content.toString().trim(),
                        null,DateFormatUtils.str2Long(scheduled,false),
                        DateFormatUtils.str2Long(deadline,false),//实例化系统日历事件
                        AdvanceTime.FIFTH_MINUTES,
                        (!rRule.equals(""))?rRule:null);
                int result = CalendarProviderManager.addCalendarEvent(ItemActivity.this, calendarEvent);//插入
                if (result == 0) {
                    Toast.makeText(ItemActivity.this, "插入成功", Toast.LENGTH_SHORT).show();
                } else if (result == -1) {
                    Toast.makeText(ItemActivity.this, "插入失败", Toast.LENGTH_SHORT).show();
                } else if (result == -2) {
                    Toast.makeText(ItemActivity.this, "没有权限", Toast.LENGTH_SHORT).show();
                }
                //insert(content.toString().trim(),fileText.getText().toString().trim(), getSelectedPriority());
                finish();
            }
        });

        initLoopPicker();
        initStatePicker();
        initPriorityPicker();
        initFatherItemPicker();
        initScheduledDatePicker();
        initDeadlineDatePicker();
        initShowDatePicker();
    }

    public List<String> getItem(String filename){
        if (database == null) {
            return Collections.emptyList();
        }
        List<String> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                    "file like ?", new String[]{filename},
                    null, null,
                    TodoContract.TodoNote.COLUMN_CAPTION);
            while (cursor.moveToNext()) {
                String caption = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CAPTION));
                result.add(caption);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }
    public Boolean saveNote2Database(String content, String filename, String title, String tag, String deadline, String scheduled,
                                     String show,String repeat, String state, int priority,String fatherItem,String closed){
        if(database==null||TextUtils.isEmpty(content)){
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoNote.COLUMN_DEADLINE,deadline);
        values.put(TodoContract.TodoNote.COLUMN_SHOW,show);
        values.put(TodoContract.TodoNote.COLUMN_STATE,state);
        values.put(TodoContract.TodoNote.COLUMN_SCHEDULED,scheduled);
        values.put(TodoContract.TodoNote.COLUMN_WEEK,getDayofWeek(scheduled));
        values.put(TodoContract.TodoNote.COLUMN_REPEAT,repeat);
        values.put(TodoContract.TodoNote.COLUMN_CAPTION,title);
        values.put(TodoContract.TodoNote.COLUMN_FILE,filename);
        values.put(TodoContract.TodoNote.COLUMN_TAG,tag);
        values.put(TodoContract.TodoNote.COLUMN_CLOSED,closed);
        values.put(TodoContract.TodoNote.COLUMN_CONTENT,content);
        values.put(TodoContract.TodoNote.COLUMN_PRIORITY,priority);
        values.put(TodoContract.TodoNote.COLUMN_FATHERITEM,fatherItem);
        long rowId = database.insert(TodoContract.TodoNote.TABLE_NAME, null, values);
        return rowId!=-1;
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
            case R.id.item_fatheritem:
                mFatherItemPicker.onResume();
                mFatherItemPicker.show(item_father_item.getText().toString());
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
        },"每天");
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
        },"None");
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
        }, "None");
        // 允许点击屏幕或物理返回键关闭
        mPriorityPicker.setCancelable(true);
        // 允许循环滚动
        mPriorityPicker.setScrollLoop(false);
    }

    private void initFatherItemPicker(){
        item_father_item.setText("None");

        mFatherItemPicker = new CustomFatherItemPicker(this, new CustomFatherItemPicker.Callback() {
            @Override
            public void onFatherItemPicker(String fatherItem) {
                item_father_item.setText(fatherItem);
            }
        }, this, "None");
        // 允许点击屏幕或物理返回键关闭
        mFatherItemPicker.setCancelable(true);
        // 不允许循环滚动
        mFatherItemPicker.setScrollLoop(false);
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

    public interface databaseCallback{
        void setOnDatabaseResult(List<String> itemTitlesFromDatabase);
    }

    public void sendCallback(databaseCallback callback){
        fatherItemNewCallback = callback;
    }
}












