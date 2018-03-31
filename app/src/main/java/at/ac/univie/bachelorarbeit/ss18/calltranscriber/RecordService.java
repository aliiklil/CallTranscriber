package at.ac.univie.bachelorarbeit.ss18.calltranscriber;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecordService extends Service {

    public static final String AUDIO_STORAGE_DIRECTORY = "/calltranscriber";
    public static final String CALL_INFO_STORAGE_FILE = "/calltranscriber/callInfo";

    private MediaRecorder recorder = new MediaRecorder();
    private boolean isRecording = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {

            if (isRecording)
                return START_NOT_STICKY;

            File directory = new File(Environment.getExternalStorageDirectory().getPath(), AUDIO_STORAGE_DIRECTORY);

            if (!directory.exists()) {
                directory.mkdir();
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm");
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
        try {

            super.onDestroy();

            if (recorder != null) {
                isRecording = false;
                recorder.release();
            }

            ArrayList<CallInfo> callInfoArrayList = new ArrayList<CallInfo>();

            File file = new File(Environment.getExternalStorageDirectory().getPath(), CALL_INFO_STORAGE_FILE);

            if (file.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                callInfoArrayList = (ArrayList<CallInfo>) ois.readObject();
                ois.close();
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Thread.sleep(1000);

            Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
            cursor.moveToFirst();

            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            String duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
            String dateMillisString = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));

            long dateMillisLong = Long.parseLong(dateMillisString);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm");
            String date = simpleDateFormat.format(new Date(dateMillisLong)).split("-")[0];
            String time = simpleDateFormat.format(new Date(dateMillisLong)).split("-")[1];

            CallInfo callInfo = new CallInfo(name, number, date, time, duration);

            callInfoArrayList.add(callInfo);

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(callInfoArrayList);
            oos.close();

            Toast.makeText(this, "onDestroy finished", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
