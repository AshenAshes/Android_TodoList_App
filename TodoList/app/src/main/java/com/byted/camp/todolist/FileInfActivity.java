package com.byted.camp.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
        fileInf.setText(loadTextFromFile());

    }

    private CharSequence loadTextFromFile(){
        FileInputStream in = null;
        BufferedReader reader = null;
        String text="";
        try {
            in = openFileInput(filename);           //“data”为文件名
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            int id = 0;
            while ((line = reader.readLine()) != null) {
                text+=line+"\r\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return text;
    }
}
