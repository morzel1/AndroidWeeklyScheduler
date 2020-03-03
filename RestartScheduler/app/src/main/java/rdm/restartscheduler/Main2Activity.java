package rdm.restartscheduler;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main2Activity extends AppCompatActivity {
    DbHelper dbHelper;
    TimePicker simpleTimePicker;
    String name1;
    String day1 = "";
    String time1 = "";
    String hoursEnd = "";
    String minutesEnd = "";
    String selectedTheme;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        name1 = bundle.getString("nameKey");
        selectedTheme = bundle.getString("ThemePicked");

        if(selectedTheme!=null) {
            if (selectedTheme.equals("Black and Green")) {
                setTheme(R.style.theme1);
                setTheme(R.style.theme1TimePicker);
            } else if (selectedTheme.equals("Black and White")) {
                setTheme(R.style.theme2);
                setTheme(R.style.theme2TimePicker);
            }else if (selectedTheme.equals("Black and Grey")){
                setTheme(R.style.theme3);
                setTheme(R.style.theme2TimePicker);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        dbHelper = new DbHelper(this);



        //setting up drop list for day
        Spinner spinner2 = (Spinner)findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this,R.array.items, R.layout.custom_spinner);
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner);
        spinner2.setAdapter(dataAdapter);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                day1 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //setup time picker
        simpleTimePicker=(TimePicker)findViewById(R.id.simpleTimePicker);
        simpleTimePicker.setCurrentHour(5);
        String hours = Integer.toString(simpleTimePicker.getCurrentHour());
        String minutes = Integer.toString(simpleTimePicker.getCurrentMinute());
        if(simpleTimePicker.getCurrentHour()<10){
            hoursEnd = "0"+hours;
        } else {
            hoursEnd = hours;
        }
        if(simpleTimePicker.getCurrentMinute()<10){
            minutesEnd = "0"+minutes;
        } else {
            minutesEnd = minutes;
        }

        time1 = hoursEnd+":"+minutesEnd+":"+"00";

        simpleTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute){
                String hours = Integer.toString(hourOfDay);
                String minutes = Integer.toString(minute);
                if(hourOfDay<10){
                    hoursEnd = "0"+hours;
                } else {
                    hoursEnd = hours;
                }
                if(minute<10){
                    minutesEnd = "0"+minutes;
                } else {
                    minutesEnd = minutes;
                }
                time1 = hoursEnd+":"+minutesEnd+":"+"00";
            }

        });

    }

    public void confirm(View view){
        dbHelper.insertNewTask(name1,time1,day1);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void cancel(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}