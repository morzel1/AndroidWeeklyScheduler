package rdm.restartscheduler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Main3Activity extends AppCompatActivity {
    String selectedTheme;
    Integer testResult;
    long result;
    String TAG = "AHH4";
    String initialTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        initialTheme = bundle.getString("ThemePicked");

        if(initialTheme!=null) {
            if (initialTheme.equals("Black and Green")) {
                setTheme(R.style.theme1);
            } else if (initialTheme.equals("Black and White")) {
                setTheme(R.style.theme2);
            } else if (initialTheme.equals("Black and Grey")){
                setTheme(R.style.theme3);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Spinner spinner2 = findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this, R.array.items2, R.layout.custom_spinner);
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner);
        spinner2.setAdapter(dataAdapter);
        spinner2.setVisibility(View.VISIBLE);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTheme = parent.getItemAtPosition(position).toString();
                testResult = position;
                result = id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void confirm(View view){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ThemePicker",selectedTheme);
        SharedPreferences mPrefs;
        mPrefs = getSharedPreferences("UserTheme", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("UserTheme", selectedTheme).commit();
        startActivity(intent);
    }

    public void cancel(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
