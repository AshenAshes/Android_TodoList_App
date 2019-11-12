package com.byted.camp.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FileInfActivity extends AppCompatActivity {
    private String filename;
    private TextView fileInf;
    private TextView fileTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_inf);

        Intent intent = getIntent();
        filename = intent.getStringExtra("filename");

        fileTitle = findViewById(R.id.file_title);
        fileTitle.setText(filename);
        fileInf = findViewById(R.id.fileInf);
        fileInf.setText(filename);

        //TODO:变量filename是文件名，从数据库中找到对应的.org内容写入fileInf中(fileInf是个TextView)

    }
}
