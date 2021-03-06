package com.byted.camp.todolist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.byted.camp.todolist.extra.DoubleBack;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ADD = 1002;
    DoubleBack doubleBack=new DoubleBack();

    private Toolbar toolbar;
    private LinearLayout buttonAgenda,buttonTodo,buttonFiles,buttonSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        buttonAgenda = findViewById(R.id.button_agenda);
        buttonTodo = findViewById(R.id.button_todo);
        buttonFiles = findViewById(R.id.button_files);
        buttonSettings = findViewById(R.id.button_settings);

        bindActivity(R.id.button_agenda, AgendaActivity.class);
        bindActivity(R.id.button_todo,TodoActivity.class);
        bindActivity(R.id.button_files, FilesActivity.class);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }

    private void bindActivity(final int btnId, final Class<?> activityClass){
        findViewById(btnId).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SettingsActivity.this, activityClass);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
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
            Intent intent = new Intent(SettingsActivity.this,ExitActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
