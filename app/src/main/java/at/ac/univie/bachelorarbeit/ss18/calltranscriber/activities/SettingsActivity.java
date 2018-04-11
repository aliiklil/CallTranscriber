package at.ac.univie.bachelorarbeit.ss18.calltranscriber.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import at.ac.univie.bachelorarbeit.ss18.calltranscriber.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

}
