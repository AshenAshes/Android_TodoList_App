package com.byted.camp.todolist.backstage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

public class FileWriteService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId){
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int Minutes = 3*1000;//30s
        long triggerTime = SystemClock.elapsedRealtime()+Minutes;
        Intent i = new Intent(this,AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pendingIntent);
        return super.onStartCommand(intent,flag,startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this,AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,i,0);
        alarmManager.cancel(pendingIntent);
    }
}
