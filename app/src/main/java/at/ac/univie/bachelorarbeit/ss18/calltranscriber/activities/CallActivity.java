package at.ac.univie.bachelorarbeit.ss18.calltranscriber.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeakerLabelsResult;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResult;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ac.univie.bachelorarbeit.ss18.calltranscriber.model.CallInfo;
import at.ac.univie.bachelorarbeit.ss18.calltranscriber.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 */
public class CallActivity extends AppCompatActivity {

    /**
     * To play and pause the audio file.
     */
    private Button buttonPlayAndPause;

    /**
     * To seek to a specific point in the audio file.
     */
    private SeekBar seekBar;

    /**
     * To show the already elapsed time in the playback of the audio file to the user.
     */
    private TextView textViewElapsedTime;

    /**
     * To show the remaining time in the playback of the audio file to the user.
     */
    private TextView textViewRemainingTime;

    /**
     * To playback the audio file.
     */
    private MediaPlayer mediaPlayer;

    /**
     * Duration of the audio file.
     */
    private int audioDuration;

    /**
     * Used to update the seekbar, when the audio is being played back.
     */
    private Handler handler = new Handler();

    /**
     * To get the name and number of the called person and the date and time of the call from the MainActivity.
     */
    private Intent intent;

    /**
     * The audio file the call was recorded to.
     */
    private File audioFile;

    /**
     * The pdf file, which the transcript is written to.
     */
    private File pdfFile;

    /**
     * To create the transcript or to open the transcript if it already has been created.
     */
    private Button buttonCreateAndOpenTranscript;

    /**
     * To share the transcript via email, Whatsapp etc.
     */
    private Button buttonShare;

    /**
     * Will be shown, while the transcript is being created.
     */
    private ProgressBar progressBar;

    /**
     * Unique id of the specific call, which will be need for deleting it.
     */
    private int id;

    /**
     * Will be called, when the user clicks on a call in MainActivity. This method will setup the call information and initialize everything necessary for the playback of the audio file.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        buttonCreateAndOpenTranscript = findViewById(R.id.activity_call_create_and_open_transcript);
        buttonShare = findViewById(R.id.activity_call_send_email);

        progressBar = findViewById(R.id.activity_call_progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        TextView textViewNumber = findViewById(R.id.activity_call_number_value);
        TextView textViewDate = findViewById(R.id.activity_call_date_value);
        TextView textViewTime = findViewById(R.id.activity_call_time_value);

        intent = getIntent();

        id = intent.getIntExtra("id", -1);

        setTitle(intent.getStringExtra("name").toString());

        textViewNumber.setText(intent.getStringExtra("number").toString());
        textViewDate.setText(intent.getStringExtra("date").toString());
        textViewTime.setText(intent.getStringExtra("time").toString());

        buttonPlayAndPause = findViewById(R.id.activity_call_play_pause_button);
        textViewElapsedTime = findViewById(R.id.activity_call_elapsed_time);
        textViewRemainingTime = findViewById(R.id.activity_call_remaining_time);
        seekBar = findViewById(R.id.activity_call_seekBar);

        audioFile = new File(Environment.getExternalStorageDirectory().getPath() + "/calltranscriber/" + intent.getStringExtra("fileName").toString());
        pdfFile = new File(audioFile.getAbsolutePath().substring(0, audioFile.getAbsolutePath().lastIndexOf('.')) + ".pdf");

        if(pdfFile.exists()){
            buttonCreateAndOpenTranscript.setText("Open Transcript");
            buttonShare.setText("Share Audio & Transcript");
        } else {
            buttonCreateAndOpenTranscript.setText("Create Transcript");
            buttonShare.setText("Share Audio");
        }

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

    /**
     * Will create the time string for the elapsed and remaining time of the playback of the audio file.
     * @param time The current position in milliseconds
     * @return The time string for the elapsed or remaining time
     */
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

    /**
     * Will be called whenever the user hits the play/pause button.
     * @param view
     */
    public void onPlayAndPause(View view) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            buttonPlayAndPause.setBackgroundResource(R.drawable.play);
        } else {
            mediaPlayer.start();
            buttonPlayAndPause.setBackgroundResource(R.drawable.stop);
        }
    }

    /**
     * Will be called whenever the user hits the "Create Transcript" or "Open Transcript" button.
     * @param view
     */
    public void onCreateAndOpenTranscript(View view) {

        if(pdfFile.exists()){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } else {
            new TranscriptionTask().execute(audioFile);
        }

    }

    /**
     * Will be used to create the transcript. AsyncTask are needed whenever an app needs to make a long background operation.
     */
    private class TranscriptionTask extends AsyncTask<File, Void, Void> {

        /**
         * Will be called to make changes to the UI before starting the background operation, in which the transcript is created.
         * The button for creating the transcript will be disabled and the progressbar will be shown.
         */
        protected void onPreExecute() {

            buttonCreateAndOpenTranscript.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

        }

        /**
         * Creates the transcript. First the audiofile, which is saved as an .m4a file, will be converted to a .flac file with the help of an outside file conversion API.
         * This conversion needs to happen, because the IBM Watson Speech To Text API doesn't accept any file format created by the MediaRecorder.
         * The IBM Watson Speech To Text API then responds with the transcript, which needs to be parsed and saved to a pdf file.
         * @param audioFile
         * @return
         */
        protected Void doInBackground(File... audioFile) {

            try {

                InputStream inputStream = new FileInputStream(audioFile[0].getAbsolutePath());
                byte[] buffer = new byte[8192];
                int bytesRead;
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output64.write(buffer, 0, bytesRead);
                }

                output64.close();

                String base64EncodedAudio = output.toString();

                Map<String, Object> json1 = new HashMap<String, Object>();

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

                SpeechToText service = new SpeechToText();
                service.setUsernameAndPassword("01c5d2c9-dc25-4e04-8da7-79c72031f39d", "MmJvmy5GFzas");

                File audioFileFlac = new File(audioFile[0].getAbsolutePath() + ".flac");

                RecognizeOptions options = new RecognizeOptions.Builder()
                        .audio(new File(audioFileFlac.getAbsolutePath()))
                        .contentType(RecognizeOptions.ContentType.AUDIO_FLAC)
                        .model(RecognizeOptions.Model.EN_US_NARROWBANDMODEL)
                        .speakerLabels(true)
                        .build();

                SpeechRecognitionResults transcript = service.recognize(options).execute();

                List<SpeechRecognitionResult> speechRecognitionResults = transcript.getResults();
                List<SpeakerLabelsResult> speakerLabelsResult = transcript.getSpeakerLabels();

                String rawTranscript = "";

                JSONParser parser = new JSONParser();

                Object obj = parser.parse(speechRecognitionResults.toString());

                JSONArray jsonArray = (JSONArray) obj;

                for (int i = 0; i < jsonArray.size(); i++) {

                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                    JSONObject jsonObject2 = (JSONObject) ((JSONArray) jsonObject.get("alternatives")).get(0);

                    rawTranscript = rawTranscript + jsonObject2.get("transcript");

                }

                String[] words = rawTranscript.split(" ");

                Object obj2 = parser.parse(speakerLabelsResult.toString());

                JSONArray jsonArray2 = (JSONArray) obj2;

                int[] speakerLabels = new int[jsonArray2.size()];

                for (int i = 0; i < jsonArray2.size(); i++) {

                    JSONObject jsonObject = (JSONObject) jsonArray2.get(i);

                    speakerLabels[i] = Integer.parseInt(String.valueOf((jsonObject.get("speaker"))));

                }

                String pdfString = "Speaker " + speakerLabels[0] + ":";

                for (int i = 0; i < words.length; i++) {

                    pdfString = pdfString + " " + words[i];

                    if (i < speakerLabels.length - 1 && speakerLabels[i] != speakerLabels[i + 1]) {
                        pdfString = pdfString + "\nSpeaker " + speakerLabels[i + 1] + ":";
                    }

                }

                pdfString = pdfString.replaceAll("%HESITATION", "(Hesitation)");

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                document.open();
                document.add(new Paragraph(pdfString));
                document.close();

                audioFileFlac.delete();

            } catch(Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        /**
         * Will be called after the transcript has been created to make changes to the UI, signalling that the
         * background operation has been completed.
         * @param result
         */
        protected void onPostExecute(Void result) {

            buttonCreateAndOpenTranscript.setText("Open Transcript");
            buttonCreateAndOpenTranscript.setEnabled(true);
            buttonShare.setText("Share Audio & Transcript");
            Toast.makeText(getApplicationContext(), "Transcript created", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);

        }

    }

    /**
     * Will be called when the user presses the "Share Audio" or "Share Audio & Transcript" button.
     * The user can send the audio, or the audio and the transcript to anyone including himself via email, WhatsApp etc.
     * @param view
     */
    public void onShare(View view) {

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("text/plain");

        ArrayList<Parcelable> attachementList = new ArrayList<Parcelable>();

        if(audioFile.exists()) {
            Uri audioFileUri = Uri.fromFile(audioFile);
            attachementList.add(audioFileUri);
        }

        if(pdfFile.exists()) {
            Uri pdfFileUri = Uri.fromFile(pdfFile);
            attachementList.add(pdfFileUri);
        }

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachementList);

        intent.putExtra(Intent.EXTRA_SUBJECT, "CallTranscriber");
        startActivity(Intent.createChooser(intent , null));

    }

    /**
     * Will be called when the user presses the back button or closes the app.
     * This method has been overridden to make the transition look more smooth and because
     * the playback of the audio file needs to be stopped, if it is being played back.
     */
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    /**
     * To create the options menu, which will be needed to delete this call with its audio and pdf file.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    /**
     * Will be called, when the user presses the "Delete Call" item in the options menu. This method
     * will show the user an AlertDialog to confirm his action and then will delete the call information in the
     * callInfo file and its audio and pdf file, if they exist.
     * @param item
     */
    public void deleteCall(MenuItem item) {

        if(item.getItemId() == R.id.options_menu_delete_call) {

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("CallTranscriber");
            alertDialog.setMessage("Do you really want to delete this call and its audio (and transcript)?");

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            try {

                                ArrayList<CallInfo> callInfoArrayList = new ArrayList<CallInfo>();

                                File file = new File(Environment.getExternalStorageDirectory().getPath() + MainActivity.CALL_INFO_STORAGE_FILE);

                                if (file.exists()) {
                                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                                    callInfoArrayList = (ArrayList<CallInfo>) ois.readObject();
                                    ois.close();
                                }

                                for(int i = 0; i < callInfoArrayList.size(); i++) {
                                    if(id == callInfoArrayList.get(i).getId()) {
                                        callInfoArrayList.remove(i);
                                        break;
                                    }
                                }

                                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                                oos.writeObject(callInfoArrayList);
                                oos.close();

                                if(pdfFile.exists()) {
                                    pdfFile.delete();
                                }

                                if(audioFile.exists()) {
                                    audioFile.delete();
                                }

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                                startActivity(intent);
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                                dialog.dismiss();

                                Toast.makeText(getApplicationContext(), "Call deleted", Toast.LENGTH_LONG).show();

                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog.show();
        }

    }

}