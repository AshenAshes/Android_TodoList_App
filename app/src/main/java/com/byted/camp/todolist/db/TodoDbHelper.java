package com.byted.camp.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class TodoDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "todo1.db";
    private static final int DB_VERSION = 2;
    public TodoDbHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }//构造数据库

    @Override
    public void onCreate(SQLiteDatabase db){
       db.execSQL(TodoContract.SQL_CREATE_NOTES);
    }//创建表

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i = oldVersion ; i<newVersion;i++){
            switch(i){
                case 1:
                    db.execSQL(TodoContract.SQL_ADD_PRIORITY_COLUMN);
                    break;
            }
        }
    }
}
