package at.ac.univie.bachelorarbeit.ss18.calltranscriber.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import at.ac.univie.bachelorarbeit.ss18.calltranscriber.R;

public class CallActivity extends AppCompatActivity {

    private Button buttonPlayAndPause;
    private SeekBar seekBar;
    private TextView textViewElapsedTime;
    private TextView textViewRemainingTime;
    private MediaPlayer mediaPlayer;
    private int audioDuration;
    private Handler handler = new Handler();

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        TextView textViewNumber = (TextView) findViewById(R.id.activity_call_number_value);
        TextView textViewDate = (TextView) findViewById(R.id.activity_call_date_value);
        TextView textViewTime = (TextView) findViewById(R.id.activity_call_time_value);

        intent = getIntent();

        setTitle(intent.getStringExtra("name").toString());

        textViewNumber.setText(intent.getStringExtra("number").toString());
        textViewDate.setText(intent.getStringExtra("date").toString());
        textViewTime.setText(intent.getStringExtra("time").toString());

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

    public void onTranscribe(View view) {

    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {

        setContentView(R.layout.activity_call);

        TextView textViewNumber = (TextView) findViewById(R.id.activity_call_number_value);
        TextView textViewDate = (TextView) findViewById(R.id.activity_call_date_value);
        TextView textViewTime = (TextView) findViewById(R.id.activity_call_time_value);

        intent = getIntent();

        setTitle(intent.getStringExtra("name").toString());

        textViewNumber.setText(intent.getStringExtra("number").toString());
        textViewDate.setText(intent.getStringExtra("date").toString());
        textViewTime.setText(intent.getStringExtra("time").toString());

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

    public void onSendEmail(View view) {

        Intent intentSendMailActivity = new Intent(this, SendMailActivity.class);

        intentSendMailActivity.putExtra("name", intent.getStringExtra("name").toString());

        startActivity(intentSendMailActivity);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);


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