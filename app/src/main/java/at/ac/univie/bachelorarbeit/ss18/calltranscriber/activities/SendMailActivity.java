package at.ac.univie.bachelorarbeit.ss18.calltranscriber.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
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

        emailAdressEditText = findViewById(R.id.activity_send_mail_email_adress);
        audioFile = new File(intent.getStringExtra("audioFilePath"));
        pdfFile = new File(intent.getStringExtra("pdfFilePath"));

        SharedPreferences sharedPreferences = getSharedPreferences("emailAdress", Context.MODE_PRIVATE);
        String emailAdress = sharedPreferences.getString("emailAdress", "");
        emailAdressEditText.setText(emailAdress);

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

        SharedPreferences sharedPreferences = getSharedPreferences("emailAdress", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("emailAdress", emailAdress);
        editor.apply();

        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");
        emailIntent .putExtra(Intent.EXTRA_EMAIL, new String[]{emailAdress});

        ArrayList<Parcelable> attachementList = new ArrayList<Parcelable>();

        if(audioFile.exists()) {
            Uri audioFileUri = Uri.fromFile(audioFile);
            attachementList.add(audioFileUri);
        }

        if(pdfFile.exists()) {
            Uri pdfFileUri = Uri.fromFile(pdfFile);
            attachementList.add(pdfFileUri);
        }

        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachementList);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "CallTranscriber");
        startActivity(Intent.createChooser(emailIntent , null));

    }
}