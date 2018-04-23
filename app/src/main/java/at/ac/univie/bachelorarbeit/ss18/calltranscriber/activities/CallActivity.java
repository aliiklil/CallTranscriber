package at.ac.univie.bachelorarbeit.ss18.calltranscriber.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import at.ac.univie.bachelorarbeit.ss18.calltranscriber.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CallActivity extends AppCompatActivity {

    private Button buttonPlayAndPause;
    private SeekBar seekBar;
    private TextView textViewElapsedTime;
    private TextView textViewRemainingTime;
    private MediaPlayer mediaPlayer;
    private int audioDuration;
    private Handler handler = new Handler();

    private Intent intent;

    private File audioFile;

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

        audioFile = new File(Environment.getExternalStorageDirectory().getPath() + "/calltranscriber/" + intent.getStringExtra("fileName").toString());

        //Uri uri = Uri.parse(audioFile.getAbsolutePath());
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

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
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        CallActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mediaPlayer != null) {
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

    public void onCreateTranscript(View view) {

        new TranscribeTask().execute(audioFile);

    }

    public void onSendEmail(View view) {

        Intent intentSendMailActivity = new Intent(this, SendMailActivity.class);

        intentSendMailActivity.putExtra("name", intent.getStringExtra("name").toString());

        startActivity(intentSendMailActivity);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);


    }

    private class TranscribeTask extends AsyncTask<File, Void, Void> {

        protected Void doInBackground(File... audioFile) {

            try {

                InputStream inputStream = new FileInputStream(audioFile[0].getAbsolutePath());
                byte[] buffer = new byte[8192];
                int bytesRead;
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);
                try {
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output64.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                output64.close();

                String base64EncodedAudio = output.toString();

                Map<String,Object> json1 = new HashMap<String,Object>();

                json1.put("apikey", "2AuF91XehuALjm7DSfootd210CTGp51XuGc2-5a0Sgswe2NGu7ta9s-r3jIamnrmGEDamlcVzvBY9HNq6WmGOA");
                json1.put("inputformat", "m4a");
                json1.put("outputformat", "flac");
                json1.put("input", "base64");
                json1.put("file", base64EncodedAudio);
                json1.put("filename", "1.m4a");
                json1.put("wait", true);
                json1.put("download", true);

                String jsonBody = (new JSONObject(json1)).toJSONString();


                OkHttpClient client = new OkHttpClient();

                MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");

                RequestBody requestBody = RequestBody.create(jsonMediaType, jsonBody);

                Request request = new Request.Builder()
                        .url("https://api.cloudconvert.com/convert")
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();


                if (!response.isSuccessful()) {
                    throw new IOException("Failed to download file: " + response);
                }

                FileOutputStream fos = new FileOutputStream(audioFile[0].getAbsolutePath() + ".flac");
                fos.write(response.body().bytes());
                fos.close();





            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String transcription) {

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

}