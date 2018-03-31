package at.ac.univie.bachelorarbeit.ss18.calltranscriber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call2);
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

}
