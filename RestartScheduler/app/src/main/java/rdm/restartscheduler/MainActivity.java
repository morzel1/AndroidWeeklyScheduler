package rdm.restartscheduler;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    DbHelper dbHelper;
    ArrayAdapter<String> mAdapter;
    ArrayList<String> taskList;
    ArrayList<String> taskList2;
    ArrayList<String> taskList3;
    ArrayList<String> taskList4;
    String TAG = "AHH5";
    ListView lstTask;
    String timeSet = "empty";
    String task = "empty";
    boolean complete = false;
    public String pickedDay = "";
    public String pickedTime = "";
    String day;
    String timeNow;
    Intent mServiceIntent = new Intent();
    String getCurrentDateTime;
    String getOldTime;
    SharedPreferences mPrefs;
    SharedPreferences testPref;
    String selectedTheme = "Black and Green";
    Spinner spinner2;
    long result;
    Integer testResult;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), 1, myIntent, 0);

        alarmManager.cancel(pendingIntent);

        mPrefs = getSharedPreferences("UserTheme", 0);
        selectedTheme = mPrefs.getString("UserTheme", null);

        testPref = PreferenceManager.getDefaultSharedPreferences(this);


        //Bundle gIntent = getIntent().getExtras();
        //if (gIntent!=null){
        //SharedPreferences.Editor mEditor = mPrefs.edit();
        //mEditor.putString("UserTheme", selectedTheme).commit();
        //}

        if(selectedTheme!=null) {
            if (selectedTheme.equals("Black and Green")) {
                setTheme(R.style.theme1);
            } else if (selectedTheme.equals("Black and White")) {
                setTheme(R.style.theme2);
            }else if (selectedTheme.equals("Black and Grey")){
                setTheme(R.style.theme3);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DbHelper(this);

        updateDisplay();
        lstTask = (ListView)findViewById(R.id.lstTask);


        loadTaskList();

    }


    public void onResume() {
        super.onResume();
        stopService();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), 1, myIntent, 0);

        alarmManager.cancel(pendingIntent);
    }

    public void startService(){
        //sets intent and extra stuff to use
        mServiceIntent = new Intent(this,testService.class);
        startService(mServiceIntent);
    }

    public void stopService(){
        Intent mServiceIntent = new Intent(this, testService.class);
        stopService(mServiceIntent);
    }

    private void loadTaskList() {
        taskList = dbHelper.getTaskList();
        taskList2 = dbHelper.getTaskList2();
        taskList3 = dbHelper.getTaskList3();
        taskList4 = dbHelper.getTaskList4();

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map;
        final SimpleAdapter adapter;
        String trimmedTime = "";

        int count = taskList.size();
        if(mAdapter==null){
            for(int i = 0; i < count; i++) {
                //trims string to parsable length
                trimmedTime = taskList3.get(i);
                for(int j=0; j<3; j++){
                    trimmedTime = trimmedTime.substring(0, trimmedTime.length()-1);
                }

                //converts time from 24 hour format to 12 hour
                try{
                    SimpleDateFormat _24Hour = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat _12Hour = new SimpleDateFormat("hh:mm: a");
                    Date _24HourDt = _24Hour.parse(trimmedTime);
                    trimmedTime = _12Hour.format(_24HourDt);
                }catch(final ParseException e){
                    e.printStackTrace();
                }

                map = new HashMap<String, String>();
                map.put("name", taskList.get(i));
                map.put("day", taskList2.get(i));
                map.put("time", trimmedTime);
                map.put("box", taskList4.get(i));
                list.add(map);
            }
            adapter = new SimpleAdapter(this, list, R.layout.row, new String[] {"name","day","time","box"}, new int[]{R.id.task_title,R.id.task_day,R.id.task_time,R.id.textView});
            lstTask.setAdapter(adapter);

        }
        else{
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        /*if(taskList3!=null) {
            Integer counter = taskList.size();
            for (int i = 0; i < counter; i++) {
                Log.d(TAG, "on create: " + taskList.get(i) + " " + taskList2.get(i) + " " + taskList4.get(i) + " " + taskList3.get(i));
            }
        }*/

    }

    private void updateDisplay() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
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

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (taskList3 != null) {
                            Integer count = taskList.size();
                            for (int i = 0; i < count; i++) {
                                if (taskList3.get(i).equals(timeNow) && taskList4.get(i).equals("Yes") && taskList2.get(i).equals(day)) {
                                    dbHelper.swapOffBox(taskList.get(i));
                                    loadTaskList();
                                }
                            }
                        }
                    }
                });



            }
        },0,500);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        //Change menu icon color
        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_task:
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(Html.fromHtml("<font color='#000'>Add a new task</font>"))
                        .setMessage(Html.fromHtml("<font color='#000'>Type your task</font>"))
                        .setView(taskEditText)
                        .setPositiveButton(Html.fromHtml("<font color='#000'>Add</font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                task = String.valueOf(taskEditText.getText());
                                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                intent.putExtra("nameKey", task);
                                intent.putExtra("ThemePicked",selectedTheme);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(Html.fromHtml("<font color='#000'>Cancel</font>"),null)
                        .create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

                return true;

            case R.id.action_switch_theme:
                Intent int1 = new Intent(MainActivity.this, Main3Activity.class);
                int1.putExtra("ThemePicked",selectedTheme);
                startActivity(int1);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }



    public void deleteTask(final View view){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(Html.fromHtml("<font color='#000'>Are you sure you want to delete?</font>"))
                .setPositiveButton(Html.fromHtml("<font color='#000'>Delete</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        View parent = (View)view.getParent();
                        TextView taskTextView = (TextView)parent.findViewById(R.id.task_title);
                        String task = String.valueOf(taskTextView.getText());
                        dbHelper.deleteTask(task);
                        loadTaskList();
                    }
                })
                .setNegativeButton(Html.fromHtml("<font color='#000'>Cancel</font>"),null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }

    public void updateBox(View view){
        View parent = (View)view.getParent();
        TextView taskTextView = (TextView)parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        setContentView(R.layout.row);
        dbHelper.updateBox(task);
        Integer counter = taskList.size();
        for(int i=0; i<counter;i++){
            TextView tv1 = findViewById(R.id.textView);
            if(taskList4.get(i).equals("Yes")){
                tv1.setText("No");
            } else {
                tv1.setText("Yes");
            }
        }

        setContentView(R.layout.activity_main);
        Intent test1 = new Intent(this, MainActivity.class);

        finish();
        overridePendingTransition(0, 0);
        startActivity(test1);
        overridePendingTransition(0, 0);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String sSelected=parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        }

    @Override
    public void onStop() {
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("UserTheme", selectedTheme);

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

        timeNow = hoursEnd + ":" + minutesEnd + ":" + secondsEnd;

        SharedPreferences mPrefs2 = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPrefs2.edit();
        editor.putString("OldTime", timeNow);
        editor.putInt("OldDay", mDay);
        editor.apply();



        startService();
        super.onStop();
    }


    @Override
    protected void onStart() {
        stopService();
        super.onStart();
    }
}