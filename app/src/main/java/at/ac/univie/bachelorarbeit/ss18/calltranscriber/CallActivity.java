package at.ac.univie.bachelorarbeit.ss18.calltranscriber;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class CallActivity extends AppCompatActivity {

    private Button buttonPlayAndPause;
    private SeekBar seekBar;
    private TextView textViewElapsedTime;
    private TextView textViewRemainingTime;
    private MediaPlayer mediaPlayer;
    private int audioDuration;
    private Handler handler = new Handler();

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

        buttonPlayAndPause = (Button) findViewById(R.id.activity_call_play_pause_button);
        textViewElapsedTime = (TextView) findViewById(R.id.activity_call_elapsed_time);
        textViewRemainingTime = (TextView) findViewById(R.id.activity_call_remaining_time);
        seekBar = (SeekBar) findViewById(R.id.activity_call_seekBar);
        
        Uri uri = Uri.parse("/sdcard/calltranscriber/" + intent.getStringExtra("fileName").toString());

        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.setVolume(1, 1);
        mediaPlayer.setLooping(false);
        audioDuration = mediaPlayer.getDuration();

        seekBar.setMax(audioDuration);
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            seekBar.setProgress(progress);
                            mediaPlayer.seekTo(progress);
                        }
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) { }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) { }
                }
        );

        CallActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(mediaPlayer != null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());

                    String elapsedTime = createTimeString(mediaPlayer.getCurrentPosition());
                    textViewElapsedTime.setText(elapsedTime);

                    String remainingTime = createTimeString(audioDuration - mediaPlayer.getCurrentPosition());
                    textViewRemainingTime.setText("- " + remainingTime);
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private String createTimeString(int time) {

        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;

        String timeString = minutes + ":";

        if (seconds <= 9) {
            timeString = timeString + "0";
        }

        timeString = timeString + seconds;

        return timeString;
    }

    public void onPlayAndPause(View view) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            buttonPlayAndPause.setBackgroundResource(R.drawable.play);
        } else {
            mediaPlayer.start();
            buttonPlayAndPause.setBackgroundResource(R.drawable.stop);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

}