package rdm.restartscheduler;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class testService extends Service {
    public static final String CHANNEL_ID = "TEST";
    public static final String CHANNEL_ID2 = "TEST2";
    private AlarmManager alarmMgr;
    private PendingIntent pendingInt;
    DbHelper dbHelper;
    String day;
    String timeNow;
    ArrayList<String> taskList = null;
    ArrayList<String> taskList2 = null;
    ArrayList<String> taskList3 = null;
    ArrayList<String> taskList4 = null;
    String TAG = "AHH5";
    boolean showNotification = false;
    int oldDayCheck;
    int newDayCheck;
    String oldTimeCheck;
    String newTimeCheck;
    boolean isTrue=false;
    int tempDay;
    public NotificationManagerCompat notificationManager;
    SharedPreferences mPrefs2;

    @Override
    public void onCreate() {
        dbHelper = new DbHelper(this);
        taskList = dbHelper.getTaskList();
        taskList2 = dbHelper.getTaskList2();
        taskList3 = dbHelper.getTaskList3();
        taskList4 = dbHelper.getTaskList4();

        /*if(isForeground("rdm.restartscheduler")){
            stopSelf();
        }
        */
        super.onCreate();

        Long time = new GregorianCalendar().getTimeInMillis()+1000*60*5;
        Intent intentAlarm = new Intent(this, AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = NotificationManagerCompat.from(this);
        //main notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        /*Notification notification = new Builder(this, CHANNEL_ID)
                .setContentTitle("Weekly Scheduler")
                .setContentText("Scheduler still running")
                .setSmallIcon(R.drawable.ic_white)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        */

        //second notification

        Intent notificationIntent2 = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0, notificationIntent2,0);

        final Notification notification2 = new Builder(this, CHANNEL_ID2)
                .setContentTitle("Weekly Scheduler")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent2)
                .setContentText("One or more of your weeklies has been rest")
                .setAutoCancel(true)
                .setSound(soundUri)
                .build();



        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                Calendar c = Calendar.getInstance();
                Integer mDay = c.get(Calendar.DAY_OF_WEEK);
                if (mDay == 1) {
                    day = "Sunday";
                }
                if (mDay == 2) {
                    day = "Monday";
                }
                if (mDay == 3) {
                    day = "Tuesday";
                }
                if (mDay == 4) {
                    day = "Wednesday";
                }
                if (mDay == 5) {
                    day = "Thursday";
                }
                if (mDay == 6) {
                    day = "Friday";
                }
                if (mDay == 7) {
                    day = "Saturday";
                }
                String hoursEnd = c.get(Calendar.HOUR_OF_DAY) + "";
                String minutesEnd = c.get(Calendar.MINUTE) + "";
                String secondsEnd = c.get(Calendar.SECOND) + "";

                if (c.get(Calendar.HOUR_OF_DAY) < 10) {
                    hoursEnd = "0" + hoursEnd;
                }
                if (c.get(Calendar.MINUTE) < 10) {
                    minutesEnd = "0" + minutesEnd;
                }
                if (c.get(Calendar.SECOND) < 10) {
                    secondsEnd = "0" + secondsEnd;
                }

                timeNow = hoursEnd + ":" + minutesEnd + ":" + secondsEnd;


                if (taskList3 != null) {
                    taskList = dbHelper.getTaskList();
                    taskList2 = dbHelper.getTaskList2();
                    taskList3 = dbHelper.getTaskList3();
                    taskList4 = dbHelper.getTaskList4();
                    Integer count = taskList.size();
                    for (int i = 0; i < count; i++) {
                        if (taskList3.get(i).equals(timeNow) && taskList4.get(i).equals("Yes") && taskList2.get(i).equals(day)) {
                            dbHelper.swapOffBox(taskList.get(i));
                            taskList = dbHelper.getTaskList();
                            taskList2 = dbHelper.getTaskList2();
                            taskList3 = dbHelper.getTaskList3();
                            taskList4 = dbHelper.getTaskList4();
                            showNotification = true;
                        }
                    }
                }
                
                if(showNotification == true){
                    showNotification = false;
                    notificationManager.notify(2, notification2);
                }
            }



        },0,500);

        return START_REDELIVER_INTENT;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.setAction("testService");
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }
    */


}
