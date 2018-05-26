package at.ac.univie.bachelorarbeit.ss18.calltranscriber.recorder;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.CallLog;
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

import at.ac.univie.bachelorarbeit.ss18.calltranscriber.activities.MainActivity;
import at.ac.univie.bachelorarbeit.ss18.calltranscriber.model.CallInfo;

/**
 * Handles the recording of new incoming and outgoing calls and creates the audio file. This class will also handle the updating of the callInfo file on the device, so the call can be seen in MainActivity.
 */
public class RecordService extends Service {

    /**
     * Directory where the callInfo file, and the audio and pdf files of all calls are stored.
     */
    public static final String AUDIO_STORAGE_DIRECTORY = "/calltranscriber/";

    /**
     * Needed to record the call.
     */
    private MediaRecorder recorder = new MediaRecorder();

    /**
     * Needed to check if the MediaRecorder is recording.
     */
    private boolean isRecording = false;

    /**
     * The audio file, the MediaRecorder is recording to.
     */
    private File audioFile = null;

    /**
     * Will be called when the user calls someone or is getting called. This method simply sets up the MediaRecorder and starts recording with it.
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {

            SharedPreferences sharedPreferences = getSharedPreferences("recordCalls", Context.MODE_PRIVATE);

            if(sharedPreferences.getBoolean("recordCalls", true)) {

                if (isRecording)
                    return START_NOT_STICKY;

                File directory = new File(Environment.getExternalStorageDirectory().getPath(), AUDIO_STORAGE_DIRECTORY);

                if (!directory.exists()) {
                    directory.mkdir();
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss");
                String formattedDate = simpleDateFormat.format(new Date());

                audioFile = new File(Environment.getExternalStorageDirectory().getPath() + AUDIO_STORAGE_DIRECTORY + formattedDate + ".m4a");

                recorder.reset();
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                recorder.setOutputFile(audioFile.getAbsolutePath());
                recorder.setAudioSamplingRate(16000);
                recorder.setAudioEncodingBitRate(256000);
                recorder.setAudioChannels(1);

                recorder.prepare();
                recorder.start();

                Toast.makeText(this, "Recording started", Toast.LENGTH_LONG).show();

                isRecording = true;

            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return Service.START_STICKY;
    }

    /**
     * Will be called when the call is being hung up by either side. This simply stop the recording and creates a new CallInfo object which is stored in the callInfo file.
     */
    @Override
    public void onDestroy() {
        try {

            super.onDestroy();

            SharedPreferences sharedPreferences = getSharedPreferences("recordCalls", Context.MODE_PRIVATE);

            if(sharedPreferences.getBoolean("recordCalls", true)) {

                if (recorder != null) {
                    isRecording = false;
                    recorder.release();
                }

                ArrayList<CallInfo> callInfoArrayList = new ArrayList<CallInfo>();

                File file = new File(Environment.getExternalStorageDirectory().getPath(), MainActivity.CALL_INFO_STORAGE_FILE);

                if (file.exists()) {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                    callInfoArrayList = (ArrayList<CallInfo>) ois.readObject();
                    ois.close();
                }

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Thread.sleep(1500);

                Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
                cursor.moveToFirst();

                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                String dateMillisString = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
                String fileName = audioFile.getName();

                long dateMillisLong = Long.parseLong(dateMillisString);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm");
                String date = simpleDateFormat.format(new Date(dateMillisLong)).split("-")[0];
                String time = simpleDateFormat.format(new Date(dateMillisLong)).split("-")[1];

                int highestAssignedId = 0;

                for (CallInfo callinfo : callInfoArrayList) {
                    if (highestAssignedId < callinfo.getId()) {
                        highestAssignedId = callinfo.getId();
                    }
                }

                CallInfo callInfo = new CallInfo(highestAssignedId + 1, name, number, date, time, fileName);

                callInfoArrayList.add(callInfo);

                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                oos.writeObject(callInfoArrayList);
                oos.close();

                Toast.makeText(this, "Recording stopped", Toast.LENGTH_LONG).show();

            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
