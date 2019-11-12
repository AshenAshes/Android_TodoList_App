package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.byted.camp.todolist.Calendar.calendar.CalendarEvent;
import com.byted.camp.todolist.Calendar.calendar.CalendarProviderManager;
import com.byted.camp.todolist.PickerView.CustomDatePicker;
import com.byted.camp.todolist.PickerView.CustomFatherItemPicker;
import com.byted.camp.todolist.PickerView.CustomFutureDatePicker;
import com.byted.camp.todolist.PickerView.CustomLoopPicker;
import com.byted.camp.todolist.PickerView.CustomPriorityPicker;
import com.byted.camp.todolist.PickerView.CustomStatePicker;
import com.byted.camp.todolist.PickerView.DateFormatUtils;
import com.byted.camp.todolist.beans.Note;
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

public class EditItemActivity extends AppCompatActivity implements View.OnClickListener {
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
    private TextView bar_save,bar_back;
    private myOnClick item_loop_clickListener;

    private String id;
    private Note note;
    private String Fcontent;
    private String Fdeadline;
    private String Fshow;
    private String Fscheduled;
    private String Fstate;
    private String Fpriority;
    private String Ftitle;
    private String Ftag;
    private String Ffilename;
    private String FfatherItem;
    private String Floop;

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
        setContentView(R.layout.activity_edit_item);

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

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        Log.d("idValue",id+"");
        note = initialNoteInfFromDatabase();
        if(note != null){
            Fcontent = note.getContent();
            Fdeadline = note.getDeadline();
            Fshow = note.getShow();
            Fscheduled = note.getScheduled();
            Fstate = note.getState();
            Fpriority = note.getPriority()+"";
            Ftitle = note.getCaption();
            Ftag = note.getTag();
            Ffilename = note.getFilename();
            FfatherItem = note.getFatherItem();
            Floop = note.getRepeat();
        }
        else{
            Log.d("error","note = null");
        }

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
        bar_save = findViewById(R.id.bar_save);
        bar_back = findViewById(R.id.bar_back);
        item_loop_clickListener = new myOnClick();

        item_content.setText(Fcontent);
        item_title.setText(Ftitle);
        item_filename.setText(Ffilename);
        item_state.setText(Fstate);
        item_priority.setText(Fpriority);
        item_tag.setText(Ftag);
        item_father_item.setText(FfatherItem);
        item_scheduled_date.setText(Fscheduled);
        item_deadline_date.setText(Fdeadline);
        item_show_date.setText(Fshow);
        item_loop.setText(Floop);

//        if(Floop.equals("不重复"))
//            item_switch.setChecked(false);
//        else
//            item_switch.setChecked(true);

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

        bar_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                String deadline = item_deadline_date.getText().toString().trim();
                String scheduled = item_scheduled_date.getText().toString().trim();
                Log.d("deadline",deadline);
                Log.d("scheduled",scheduled);
                String show = item_show_date.getText().toString().trim();
                String tag = item_tag.getText().toString().trim();
                String fatherItem = item_father_item.getText().toString();
                String repeat = item_loop.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(EditItemActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean succeed = updateNote2Database(content.toString().trim(),filename,title,tag,deadline,scheduled,show,repeat,
                        item_state.getText().toString().trim(),
                        getSelectedPriority(),fatherItem);
                if (succeed) {
                    Toast.makeText(EditItemActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(EditItemActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }

                String rRule="";
                switch (repeat) {
                    case "每天":
                        rRule = "FREQ=DAILY";
                        break;
                    case "每周":
                        rRule = "FREQ=WEEKLY";
                        break;
                    case "每月":
                        rRule = "FREQ=MONTHLY";
                        break;
                }

                CalendarEvent calendarEvent = new CalendarEvent(title,content.toString().trim(),
                        null, DateFormatUtils.str2Long(scheduled,false),
                        DateFormatUtils.str2Long(deadline,false),
                        (int)DateFormatUtils.str2Long(deadline,false)-(int)DateFormatUtils.str2Long(show,false),
                        (!rRule.equals(""))?rRule:null);

                long calID = CalendarProviderManager.obtainCalendarAccountID(EditItemActivity.this);
                List<CalendarEvent> events = CalendarProviderManager.queryAccountEvent(EditItemActivity.this, calID,Ftitle,DateFormatUtils.str2Long(Fscheduled,false),DateFormatUtils.str2Long(Fdeadline,false));
                if (null != events) {
                    if (events.size() == 0) {
                        Toast.makeText(EditItemActivity.this, "没有事件可以更新", Toast.LENGTH_SHORT).show();
                    } else {
                        long eventID = events.get(0).getId();
                        int result3 = CalendarProviderManager.updateCalendarEventTitle(
                                EditItemActivity.this, eventID, "改吃晚饭的房间第三方监督司法");
                        if (result3 == 1) {
                            Toast.makeText(EditItemActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditItemActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(EditItemActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
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
                    "file = ?", new String[]{filename},
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

    public Boolean updateNote2Database(String content, String filename, String title, String tag, String deadline, String scheduled,
                                     String show,String repeat, String state, int priority,String fatherItem){
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
        values.put(TodoContract.TodoNote.COLUMN_FATHERITEM,fatherItem);
        values.put(TodoContract.TodoNote.COLUMN_CONTENT,content);
        values.put(TodoContract.TodoNote.COLUMN_PRIORITY,priority);
        long rowId = database.update(TodoContract.TodoNote.TABLE_NAME, values,"_id="+id,null);
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
        item_state.setText(Fstate);

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
        item_priority.setText(Fpriority);

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
        item_father_item.setText(FfatherItem);

        mFatherItemPicker = new CustomFatherItemPicker(this, new CustomFatherItemPicker.Callback() {
            @Override
            public void onFatherItemPicker(String fatherItem) {
                item_father_item.setText(fatherItem);
            }
        }, this, "None");
        // 允许点击屏幕或物理返回键关闭
        mFatherItemPicker.setCancelable(true);
        // 允许循环滚动
        mFatherItemPicker.setScrollLoop(false);
    }

    private void initScheduledDatePicker() {
        long beginTimestamp = DateFormatUtils.str2Long("2010-05-01", false);
        long endTimestamp = System.currentTimeMillis();
        long scheduledTimestamp = DateFormatUtils.str2Long(Fscheduled,false);

        item_scheduled_date.setText(Fscheduled);

        // 通过时间戳初始化日期，毫秒级别
        mScheduledDatePicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                item_scheduled_date.setText(DateFormatUtils.long2Str(timestamp, false));
            }
        }, beginTimestamp, endTimestamp, scheduledTimestamp);
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
        long DeadlineDateTimestamp = DateFormatUtils.str2Long(Fdeadline,false);

        item_deadline_date.setText(Fdeadline);

        // 通过时间戳初始化日期，毫秒级别
        mDeadlineDatePicker = new CustomFutureDatePicker(this, new CustomFutureDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                item_deadline_date.setText(DateFormatUtils.long2Str(timestamp, false));
            }
        }, beginTimestamp, endTimestamp, DeadlineDateTimestamp);
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
        long ShowDateTimestamp = DateFormatUtils.str2Long(Fshow,false);

        item_show_date.setText(Fshow);

        // 通过时间戳初始化日期，毫秒级别
        mShowDatePicker = new CustomFutureDatePicker(this, new CustomFutureDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                item_show_date.setText(DateFormatUtils.long2Str(timestamp, false));
            }
        }, beginTimestamp, endTimestamp, ShowDateTimestamp);
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
        mFatherItemPicker.onDestroy();
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

    private Note initialNoteInfFromDatabase() {
        if (database == null) {
            return null;
        }
        Cursor cursor = null;
        try {
            cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                    "_id like ?", new String[]{String.valueOf(id)},
                    null, null,
                    TodoContract.TodoNote.COLUMN_FILE);
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote._ID));
                String caption = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CAPTION));
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CONTENT));
                String intState = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_STATE));
                int intPriority = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_PRIORITY));
                String fileName = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_FILE));
                String deadline = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_DEADLINE));
                String fatherItem = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_FATHERITEM));
                String show = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_SHOW));
                int week = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_WEEK));
                String repeat = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_REPEAT));
                String scheduled = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_SCHEDULED));

                String tag = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_TAG));
                Note note = new Note(id);
                note.setContent(content);
                note.setShow(show);
                note.setFatherItem(fatherItem);
                note.setTag(tag);
                note.setScheduled(scheduled);
                note.setWeek(week);
                note.setRepeat(repeat);
                note.setCaption(caption);
                note.setState(intState);
                note.setPriority(intPriority);
                note.setDeadline(deadline);
                note.setFilename(fileName);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return note;
    }
}
