package at.ac.univie.bachelorarbeit.ss18.calltranscriber.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import at.ac.univie.bachelorarbeit.ss18.calltranscriber.CallInfo;
import at.ac.univie.bachelorarbeit.ss18.calltranscriber.CallListAdapter;
import at.ac.univie.bachelorarbeit.ss18.calltranscriber.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    public static final String CALL_INFO_STORAGE_FILE = "/calltranscriber/callInfo";
    private ArrayList<CallInfo> callInfoArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ListView listView = (ListView) findViewById(R.id.call_list);

        callInfoArrayList = new ArrayList<CallInfo>();

        File file = new File(Environment.getExternalStorageDirectory().getPath() + CALL_INFO_STORAGE_FILE);
        Log.i("Halllllllo", Build.CPU_ABI);
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

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        String name = callInfoArrayList.get(callInfoArrayList.size() - i - 1).getName();
        String number = callInfoArrayList.get(callInfoArrayList.size() - i - 1).getNumber();
        String date = callInfoArrayList.get(callInfoArrayList.size() - i - 1).getDate();
        String time = callInfoArrayList.get(callInfoArrayList.size() - i - 1).getTime();
        String fileName = callInfoArrayList.get(callInfoArrayList.size() - i - 1).getFileName();

        Intent intent = new Intent(this, CallActivity.class);

        if(name == null) {
            name = "";
        }

        intent.putExtra("name", name);
        intent.putExtra("number", number);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("fileName", fileName);

        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.activity_main_drawer_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START, false);
        return true;
    }
}