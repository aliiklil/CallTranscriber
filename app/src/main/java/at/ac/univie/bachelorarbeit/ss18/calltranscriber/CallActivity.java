package at.ac.univie.bachelorarbeit.ss18.calltranscriber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.TextView;

public class CallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        TextView textViewNumber = (TextView) findViewById(R.id.activity_call_number_value);
        TextView textViewDate = (TextView) findViewById(R.id.activity_call_date_value);
        TextView textViewTime = (TextView) findViewById(R.id.activity_call_time_value);
        TextView textViewDuration = (TextView) findViewById(R.id.activity_call_duration_value);

        Intent intent = getIntent();

        setTitle(intent.getStringExtra("name").toString());

        textViewNumber.setText(intent.getStringExtra("number").toString());
        textViewDate.setText(intent.getStringExtra("date").toString());
        textViewTime.setText(intent.getStringExtra("time").toString());
        textViewDuration.setText(DateUtils.formatElapsedTime(Long.valueOf(intent.getStringExtra("duration").toString())));

    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

}
