package at.ac.univie.bachelorarbeit.ss18.calltranscriber.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import at.ac.univie.bachelorarbeit.ss18.calltranscriber.model.CallInfo;
import at.ac.univie.bachelorarbeit.ss18.calltranscriber.util.CallListAdapter;
import at.ac.univie.bachelorarbeit.ss18.calltranscriber.R;

/**
 * Will show a list of all recorded calls to the user.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    /**
     * Path of the callInfo file, in which all information about every recorded call is saved.
     * This is done via serialization of an ArrayList with the type parameter CallInfo.
     */
    public static final String CALL_INFO_STORAGE_FILE = "/calltranscriber/callInfo";

    /**
     * ArrayList with the information of all recorded calls.
     */
    private ArrayList<CallInfo> callInfoArrayList;

    /**
     * Will be used to show the user his own phone number in the burger menu.
     */
    private TextView textViewPhoneNumber;

    /**
     * Will display the list of all calls and setup up the burger menu.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        textViewPhoneNumber = navigationView.getHeaderView(0).findViewById(R.id.nav_header_main_phone_number);

        ListView listView = findViewById(R.id.call_list);

        callInfoArrayList = new ArrayList<CallInfo>();

        File file = new File(Environment.getExternalStorageDirectory().getPath() + CALL_INFO_STORAGE_FILE);

        try {
            if (file.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                callInfoArrayList = (ArrayList<CallInfo>) ois.readObject();
                ois.close();
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        CallListAdapter callListAdapter = new CallListAdapter(this, callInfoArrayList);
        listView.setAdapter(callListAdapter);

        listView.setOnItemClickListener(this);

        setPhoneNumberText();

    }

    /**
     * Will be called when the user clicks on a call to launch the CallActivity. Every information of this call is being sent alongside.
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        int id = callInfoArrayList.get(callInfoArrayList.size() - i - 1).getId();
        String name = callInfoArrayList.get(callInfoArrayList.size() - i - 1).getName();
        String number = callInfoArrayList.get(callInfoArrayList.size() - i - 1).getNumber();
        String date = callInfoArrayList.get(callInfoArrayList.size() - i - 1).getDate();
        String time = callInfoArrayList.get(callInfoArrayList.size() - i - 1).getTime();
        String fileName = callInfoArrayList.get(callInfoArrayList.size() - i - 1).getFileName();

        Intent intent = new Intent(this, CallActivity.class);

        if(name == null) {
            name = "";
        }

        intent.putExtra("id", id);
        intent.putExtra("name", name);
        intent.putExtra("number", number);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("fileName", fileName);

        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

    }

    /**
     * Will set the phone number in the burger menu.
     */
    private void setPhoneNumberText() {

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String phoneNumber = telephonyManager.getLine1Number();

        if(phoneNumber != null && !phoneNumber.isEmpty()) {
            textViewPhoneNumber.setText(phoneNumber);
        }

    }

    /**
     * Will be called when the user presses the back button or closes the app.
     * This method has been overridden to make the transition look more smooth.
     */
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    /**
     * If the burger menu is open it will be closed.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Will be called when an item in the burger menu is pressed. Settings is the only item in the burger menu.
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.activity_main_drawer_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START, false);
        return true;
    }

}