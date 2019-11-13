package com.byted.camp.todolist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.byted.camp.todolist.PickerView.DateFormatUtils;
import com.byted.camp.todolist.backstage.FileWriteService;
import com.byted.camp.todolist.extra.DoubleBack;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byted.camp.todolist.ui.NoteListAdapter;
import com.google.android.material.tabs.TabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AgendaActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ADD = 1002;
    private static final int PAGE_COUNT=7;
    DoubleBack doubleBack=new DoubleBack();

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    private Intent mIntent;
    private TextView agenda_title;
    private TabLayout tableLayout;
    private ViewPager pager;
    private LinearLayout buttonAgenda,buttonTodo,buttonFiles,buttonSettings;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText(AgendaActivity.this,
//                "create", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_agenda);
        mIntent = new Intent(this, FileWriteService.class);
        startService(mIntent);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        agenda_title = findViewById(R.id.agenda_title);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.READ_CALENDAR}, 1);
        }

        //透明状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        long todaySystemDate;
        final String todayDate;
        int todayWeek;
        int before,after;
        final List<String> aWeekDates = new ArrayList<>();

        todaySystemDate = System.currentTimeMillis();
        //get XXXX-XX-XX
        todayDate = DateFormatUtils.long2Str(todaySystemDate, false);
        todayWeek = getDayofWeek("Today");

        before = 1 - todayWeek;
        after = 7 - todayWeek;

        for(int i=before;i <= after;i++)
            aWeekDates.add(getDayBeforeOrAfter(todayDate,i));

        tableLayout = findViewById(R.id.tab_layout);
        pager = findViewById(R.id.view_pager);

        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            private String getDayBeforeOrAfter(String dateTime, int offset){
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                calendar.add(Calendar.DATE,offset);
                String offsetDate = sdf.format(calendar.getTime());
                return offsetDate;
            }

            @Override
            public Fragment getItem(int position) {
                long todaySystemDate;
                String todayDate;
                int todayWeek;
                int before,after;
                List<String> aWeekDates = new ArrayList<>();

                todaySystemDate = System.currentTimeMillis();
                //get XXXX-XX-XX
                todayDate = DateFormatUtils.long2Str(todaySystemDate, false);
                todayWeek = getDayofWeek(todayDate);

                before = 1 - todayWeek;
                after = 7 - todayWeek;

                for(int i=before;i <= after;i++)
                    aWeekDates.add(getDayBeforeOrAfter(todayDate,i));

                WeekFragment weekFragment = new WeekFragment();

                weekFragment.setDate(aWeekDates.get(position));
                return weekFragment;
            }
            @Override
            public int getCount() {
                return PAGE_COUNT;
            }
            @Override
            public CharSequence getPageTitle(int position){
                switch(position){
                    case 0:
                        return "Sun";
                    case 1:
                        return "Mon";
                    case 2:
                        return "Tue";
                    case 3:
                        return "Wed";
                    case 4:
                        return "Thu";
                    case 5:
                        return "Fri";
                    case 6:
                        return "Sat";
                    default:
                        return "null";
                }
            }
        });

        pager.setCurrentItem(todayWeek-1);
        agenda_title.setText("Today");

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if(!aWeekDates.get(position).equals(todayDate))
                    agenda_title.setText(aWeekDates.get(position));
                else
                    agenda_title.setText("Today");
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
    }

    private String getDayBeforeOrAfter(String dateTime, int offset){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.DATE,offset);
        String offsetDate = sdf.format(calendar.getTime());
        return offsetDate;
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
        stopService(mIntent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_calendar:
                startActivity(new Intent(this, CalendarActivity.class));
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_ADD
//                && resultCode == Activity.RESULT_OK) {
//            notesAdapter.refresh(loadNotesFromDatabase());
//        }
//    }

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
