package rdm.restartscheduler;


import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static rdm.restartscheduler.App.CHANNEL_ID3;


public class AlarmReceiver extends BroadcastReceiver {
    ArrayList<String> taskList = null;
    ArrayList<String> taskList2 = null;
    ArrayList<String> taskList3 = null;
    ArrayList<String> taskList4 = null;
    public static final String CHANNEL_ID2 = "TEST3";
    public NotificationManagerCompat notificationManager;
    boolean showNotification=false;
    boolean isTrue = false;
    Boolean targetInZone = false;
    int tempDay;
    DbHelper dbHelper;

    String TAG = "AHH5";

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = NotificationManagerCompat.from(context);
        Intent notificationIntent2 = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, notificationIntent2,0);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        final Notification notification3 = new NotificationCompat.Builder(context, CHANNEL_ID3)
                .setContentTitle("Weekly Scheduler")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent2)
                .setContentText("One or more of your weeklies has been rest")
                .setAutoCancel(true)
                .setSound(soundUri)
                .build();


        dbHelper = new DbHelper(context);
        //grabs old time/day
        SharedPreferences mPrefs2 = PreferenceManager.getDefaultSharedPreferences(context);
        String oldTimeCheck = mPrefs2.getString("OldTime", null);
        int oldDayCheck = mPrefs2.getInt("OldDay",0);


        taskList = dbHelper.getTaskList();
        taskList2 = dbHelper.getTaskList2();
        taskList3 = dbHelper.getTaskList3();
        taskList4 = dbHelper.getTaskList4();

        //grabs current time/day
        Calendar c = Calendar.getInstance();
        Integer mDay = c.get(Calendar.DAY_OF_WEEK);

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

        String timeNow = hoursEnd + ":" + minutesEnd + ":" + secondsEnd;

        //Log.d(TAG, "alarm going");

        int newDayCheck = mDay;
        String newTimeCheck = timeNow;
        if (taskList3 != null) {
            taskList = dbHelper.getTaskList();
            taskList2 = dbHelper.getTaskList2();
            taskList3 = dbHelper.getTaskList3();
            taskList4 = dbHelper.getTaskList4();
            Integer count = taskList.size();
            for (int i = 0; i < count; i++) {
                if (taskList2.get(i).equals("Sunday")) {
                    tempDay = 1;
                }
                if (taskList2.get(i).equals("Monday")) {
                    tempDay = 2;
                }
                if (taskList2.get(i).equals("Tuesday")) {
                    tempDay = 3;
                }
                if (taskList2.get(i).equals("Wednesday")) {
                    tempDay = 4;
                }
                if (taskList2.get(i).equals("Thursday")) {
                    tempDay = 5;
                }
                if (taskList2.get(i).equals("Friday")) {
                    tempDay = 6;
                }
                if (taskList2.get(i).equals("Saturday")) {
                    tempDay = 7;
                }
                //if the old day and new day are the same, do the time comparison
                if( (oldDayCheck == newDayCheck) && (tempDay == newDayCheck) ){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        LocalTime target = LocalTime.parse(taskList3.get(i));
                                targetInZone = ( target.isAfter(LocalTime.parse(oldTimeCheck)) && target.isBefore(LocalTime.parse(newTimeCheck)) );
                    } else {
                        try {
                            targetInZone = isTimeBetweenTwoTime(oldTimeCheck,newTimeCheck,taskList3.get(i));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    if(taskList3.get(i).equals(oldTimeCheck) || taskList3.get(i).equals(newTimeCheck)){
                        targetInZone = true;
                    }



                    if((targetInZone == true) && (taskList4.get(i).equals("Yes")) ){
                        targetInZone=false;
                        dbHelper.swapOffBox(taskList.get(i));
                        taskList = dbHelper.getTaskList();
                        taskList2 = dbHelper.getTaskList2();
                        taskList3 = dbHelper.getTaskList3();
                        taskList4 = dbHelper.getTaskList4();
                        showNotification = true;
                    }
                }

                if(oldDayCheck != newDayCheck){

                    if( (oldDayCheck == tempDay) || (newDayCheck == tempDay) ){
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            LocalTime target = LocalTime.parse(taskList3.get(i));
                            targetInZone = (target.isAfter(LocalTime.parse(oldTimeCheck)) || target.isBefore(LocalTime.parse(newTimeCheck)));
                        }else {
                            try {
                                targetInZone = isTimeBetweenTwoTime(oldTimeCheck,newTimeCheck,taskList3.get(i));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        if(taskList3.get(i).equals(oldTimeCheck) || taskList3.get(i).equals(newTimeCheck)){
                            targetInZone = true;
                        }


                        if((targetInZone == true) && (taskList4.get(i).equals("Yes")) ){
                            targetInZone=false;
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
                    notificationManager.notify(3, notification3);
                }
            } //end of for loop
        }

        /*Log.d(TAG, "old time: " + oldTimeCheck + " " + oldDayCheck);
        Log.d(TAG, "new time: " + newTimeCheck + " "  + newDayCheck);
        if(taskList3!=null) {
            Integer counter = taskList.size();
            for (int i = 0; i < counter; i++) {
                Log.d(TAG, "on create: " + taskList.get(i) + " " + taskList2.get(i) + " " + taskList4.get(i) + " " + taskList3.get(i));
            }
        }

        Log.d(TAG, "-----------------------------------------------------------------------");*/

        SharedPreferences.Editor editor = mPrefs2.edit();
        editor.putString("OldTime", timeNow);
        editor.putInt("OldDay", mDay);
        editor.apply();

        Long time = new GregorianCalendar().getTimeInMillis()+1000*60*15;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(context,1,  intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }


    }

    public static boolean isTimeBetweenTwoTime(String argStartTime,
                                               String argEndTime, String argCurrentTime) throws ParseException {
        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        //
        if (argStartTime.matches(reg) && argEndTime.matches(reg)
                && argCurrentTime.matches(reg)) {
            boolean valid = false;
            // Start Time
            java.util.Date startTime = new SimpleDateFormat("HH:mm:ss")
                    .parse(argStartTime);
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startTime);

            // Current Time
            java.util.Date currentTime = new SimpleDateFormat("HH:mm:ss")
                    .parse(argCurrentTime);
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(currentTime);

            // End Time
            java.util.Date endTime = new SimpleDateFormat("HH:mm:ss")
                    .parse(argEndTime);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endTime);

            //
            if (currentTime.compareTo(endTime) < 0) {

                currentCalendar.add(Calendar.DATE, 1);
                currentTime = currentCalendar.getTime();

            }

            if (startTime.compareTo(endTime) < 0) {

                startCalendar.add(Calendar.DATE, 1);
                startTime = startCalendar.getTime();

            }
            //
            if (currentTime.before(startTime)) {
                valid = false;
            } else {

                if (currentTime.after(endTime)) {
                    endCalendar.add(Calendar.DATE, 1);
                    endTime = endCalendar.getTime();

                }


                if (currentTime.before(endTime)) {
                    valid = true;
                } else {
                    valid = false;
                }

            }
            return valid;

        } else {
            throw new IllegalArgumentException(
                    "Not a valid time, expecting HH:MM:SS format");
        }

    }

}
