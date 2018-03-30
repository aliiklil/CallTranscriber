package at.ac.univie.bachelorarbeit.ss18.calltranscriber;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordService extends Service {

    public static final String STORAGE_DIRECTORY = "/sdcard/calltranscriber";

    private MediaRecorder recorder = new MediaRecorder();
    private boolean isRecording = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {

            if (isRecording)
                return START_NOT_STICKY;

            File directory = new File(STORAGE_DIRECTORY);

            if (!directory.exists()) {
                directory.mkdir();
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy_HH:mm");
            String formattedDate = simpleDateFormat.format(new Date());

            File recordingFile = null;

            recordingFile = File.createTempFile(formattedDate, ".mp4", directory);

            recorder.reset();
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(recordingFile.getAbsolutePath());
            recorder.setAudioEncodingBitRate(128000);
            recorder.setAudioSamplingRate(44100);

            recorder.prepare();
            recorder.start();
            Toast.makeText(this, "Recording started", Toast.LENGTH_LONG).show();

            isRecording = true;

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy called", Toast.LENGTH_LONG).show();

        if (recorder != null) {

            isRecording = false;
            recorder.release();

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
