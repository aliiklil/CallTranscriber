package at.ac.univie.bachelorarbeit.ss18.calltranscriber.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import at.ac.univie.bachelorarbeit.ss18.calltranscriber.R;

public class SendMailActivity extends AppCompatActivity {

    private EditText emailAdressEditText;
    private File audioFile;
    private File pdfFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);

        Intent intent = getIntent();
        setTitle(intent.getStringExtra("name").toString());

        emailAdressEditText = (EditText) findViewById(R.id.activity_send_mail_email_adress);
        audioFile = new File(intent.getStringExtra("audioFilePath"));
        pdfFile = new File(intent.getStringExtra("pdfFilePath"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }

        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void onSend(View view) {

        String emailAdress = emailAdressEditText.getText().toString();

        if(TextUtils.isEmpty(emailAdress) || !Patterns.EMAIL_ADDRESS.matcher(emailAdress).matches()) {
            Toast.makeText(getApplicationContext(), "Wrong Email Adress", Toast.LENGTH_LONG).show();
            return;
        }

        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");
        emailIntent .putExtra(Intent.EXTRA_EMAIL, new String[]{emailAdress});

        ArrayList<Parcelable> attachementList = new ArrayList<Parcelable>();

        Uri audioFileUri = Uri.fromFile(audioFile);
        Uri pdfFileUri = Uri.fromFile(pdfFile);

        attachementList.add(audioFileUri);
        attachementList.add(pdfFileUri);

        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachementList);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "CallTranscriber Audio And Transcript");
        startActivity(Intent.createChooser(emailIntent , null));

    }
}