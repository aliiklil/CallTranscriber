package at.ac.univie.bachelorarbeit.ss18.calltranscriber.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import at.ac.univie.bachelorarbeit.ss18.calltranscriber.R;

public class SettingsActivity extends AppCompatActivity {


    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        checkBox = findViewById(R.id.activity_settings_record_calls_checkbox);

        SharedPreferences sharedPreferences = getSharedPreferences("recordCalls", Context.MODE_PRIVATE);

        if(sharedPreferences.getBoolean("recordCalls", true)) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void onRecordCalls(View view) {

        if(checkBox.isChecked()) {

            SharedPreferences sharedPreferences = getSharedPreferences("recordCalls", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("recordCalls", true);
            editor.apply();

            Toast.makeText(this, "Calls will be recorded", Toast.LENGTH_LONG).show();

        } else {

            SharedPreferences sharedPreferences = getSharedPreferences("recordCalls", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("recordCalls", false);
            editor.apply();

            Toast.makeText(this, "Calls will not be recorded", Toast.LENGTH_LONG).show();
        }

    }
}
