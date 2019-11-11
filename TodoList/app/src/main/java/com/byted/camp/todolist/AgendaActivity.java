package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.byted.camp.todolist.PickerView.DateFormatUtils;
import com.byted.camp.todolist.extra.DoubleBack;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract.TodoNote;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.ui.NoteListAdapter;
import com.google.android.material.tabs.TabLayout;
import com.jaeger.library.StatusBarUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class AgendaActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ADD = 1002;
    private static final int PAGE_COUNT=7;
    DoubleBack doubleBack=new DoubleBack();

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;

    private TabLayout tableLayout;
    private ViewPager pager;
    private LinearLayout buttonAgenda,buttonTodo,buttonFiles,buttonSettings;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private long todaySystemDate;
    private String todayDate;
    private int todayWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        todaySystemDate = System.currentTimeMillis();
        //get XXXX-XX-XX
        todayDate = DateFormatUtils.long2Str(todaySystemDate, false);
        todayWeek = getDayofWeek(todayDate);

        //透明状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        tableLayout = findViewById(R.id.tab_layout);
        pager = findViewById(R.id.view_pager);

        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                CalendarFragment calendarFragment = new CalendarFragment();

                calendarFragment.setDate(position);
                return calendarFragment;
            }
            @Override
            public int getCount() {
                return PAGE_COUNT;
            }
            @Override
            public CharSequence getPageTitle(int position){
                switch(position){
                    case 0:
                        return "日";
                    case 1:
                        return "一";
                    case 2:
                        return "二";
                    case 3:
                        return "三";
                    case 4:
                        return "四";
                    case 5:
                        return "五";
                    case 6:
                        return "六";
                    default:
                        return "空";
                }
            }
        });

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        tableLayout.setupWithViewPager(pager);

        buttonAgenda = findViewById(R.id.button_agenda);
        buttonTodo = findViewById(R.id.button_todo);
        buttonFiles = findViewById(R.id.button_files);
        buttonSettings = findViewById(R.id.button_settings);

        bindActivity(R.id.button_todo, TodoActivity.class);
        bindActivity(R.id.button_files, FilesActivity.class);
        bindActivity(R.id.button_settings, SettingsActivity.class);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivityForResult(
                        new Intent(AgendaActivity.this, ItemActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                AgendaActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                AgendaActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    private void bindActivity(final int btnId, final Class<?> activityClass){
        findViewById(btnId).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AgendaActivity.this, activityClass);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
        database = null;
        dbHelper.close();
        dbHelper = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 获取状态栏高度
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.action_settings:
//                return true;
//            case R.id.action_debug:
////                startActivity(new Intent(this, DebugActivity.class));
//                return true;
//            default:
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        if (database == null) {
            return Collections.emptyList();
        }
        List<Note> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(TodoNote.TABLE_NAME, null,
                    null, null,
                    null, null,
                    TodoNote.COLUMN_PRIORITY + " DESC");

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(TodoNote._ID));
                String content = cursor.getString(cursor.getColumnIndex(TodoNote.COLUMN_CONTENT));
                long dateMs = cursor.getLong(cursor.getColumnIndex(TodoNote.COLUMN_DEADLINE));
                int intState = cursor.getInt(cursor.getColumnIndex(TodoNote.COLUMN_STATE));
                int intPriority = cursor.getInt(cursor.getColumnIndex(TodoNote.COLUMN_PRIORITY));

                //TODO:fix bugs
                Note note = new Note(id);
                note.setContent(content);
                note.setDate(new Date(dateMs));
                note.setState(State.from(intState));
                note.setPriority(Priority.from(intPriority));

                result.add(note);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    private void deleteNote(Note note) {
        if (database == null) {
            return;
        }
        int rows = database.delete(TodoNote.TABLE_NAME,
                TodoNote._ID + "=?",
                new String[]{String.valueOf(note.id)});
        if (rows > 0) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private void updateNode(Note note) {
        if (database == null) {
            return;
        }
        ContentValues values = new ContentValues();
        //TODO:fix bugs
        values.put(TodoNote.COLUMN_STATE, note.getState().intValue);

        int rows = database.update(TodoNote.TABLE_NAME, values,
                TodoNote._ID + "=?",
                new String[]{String.valueOf(note.id)});
        if (rows > 0) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
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
    public void onBackPressed() {
        //实现Home键效果
        //super.onBackPressed();这句话一定要注掉,不然又去调用默认的back处理方式了
        long nowTime=System.currentTimeMillis();
        long minusTime=nowTime-doubleBack.getFirstclickTime();
        if(minusTime > 2000){
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_LONG).show();
            doubleBack.setFirstClickTime(nowTime);
        }
        else{
            Intent intent = new Intent(AgendaActivity.this,ExitActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
