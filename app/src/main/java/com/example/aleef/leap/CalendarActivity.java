package com.example.aleef.leap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView mCalendarView;
    private Button btnAdd;
    private Button btnSettings;
    private String fileName = "/storage/self/primary/testSavedEvents.txt";
    Boolean fromEventCreate;
    Date date;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    ArrayAdapter<String> adapter;
    ArrayList<String> eventStringArrayList = new ArrayList<String>();
    ArrayList<Event> eventArrayList =new ArrayList<Event>();
    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        Button logoutBtn = (Button)findViewById(R.id.logout_btn);

        File file = new File(fileName);

        fbAuth = FirebaseAuth.getInstance();

        //Toast.makeText(CalendarActivity.this, fileName,Toast.LENGTH_LONG).show();
        loadFile(file);

        try {
            Intent incoming = getIntent();
            fromEventCreate = incoming.getExtras().getBoolean("create");
            if(fromEventCreate != null && fromEventCreate){
                String name = incoming.getStringExtra("name");
                String time = incoming.getStringExtra("time");
                String strDate = incoming.getStringExtra("strDate");
                Event event = new Event(name, time, strDate);
                eventArrayList.add(event);

                try{
                    saveFile(file);
                }catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(CalendarActivity.this, "Error Saving Events",
                            Toast.LENGTH_LONG).show();
                }
            }
            fromEventCreate = false;
        }catch(RuntimeException e){
            e.printStackTrace();
        }

        ListView listView = (ListView) findViewById(R.id.eventList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventStringArrayList);
        listView.setAdapter(adapter);

        try {
            date = sdf.parse(sdf.format(new Date()));
        }catch (ParseException e){
            e.printStackTrace();
        }
        checkEventsOnDay();

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String dateGet = dayOfMonth + "/" + (month + 1) + "/" + year;

                try {
                    date = (sdf.parse(dateGet));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                checkEventsOnDay();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBtn();
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsBtn();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    //Menu on create
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.optionsMenu:{
                logout();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //logout method
    private void logout(){
        fbAuth.signOut();
        finish();
        startActivity(new Intent(CalendarActivity.this, LoginActivity.class));
    }

    public void checkEventsOnDay(){

        Collections.sort(eventArrayList);

        eventStringArrayList.clear();

        for(Event event : eventArrayList){
            if(event.getDate().equals(date)){
                eventStringArrayList.add(event.toString());
            }else {
                eventStringArrayList.remove(event.toString());
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void addBtn() {
        Intent addBtnIntent = new Intent(CalendarActivity.this, CreateEventActivity.class);
        String strDate = sdf.format(date);
        addBtnIntent.putExtra("date", strDate);
        startActivity(addBtnIntent);
    }
    public void settingsBtn(){
        Intent settingsBtnIntent = new Intent(CalendarActivity.this, SettingsActivity.class);
        startActivity(settingsBtnIntent);
    }

    public void saveFile(File file) throws IOException{
        isWritePermissionGranted();

        FileOutputStream fos = new FileOutputStream(file);
        for(Event event: eventArrayList){
            fos.write(event.toCsvString().getBytes());
            fos.write("\n".getBytes());
        }
        fos.close();
    }
    public void loadFile(File file){
        isReadPermissionGranted();
        try{
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String inputLine;
            eventArrayList.clear();

            while((inputLine = br.readLine()) != null){
                String[] fields = inputLine.split(",");
                Event event = new Event(fields[0], fields[1], fields[2]);
                eventArrayList.add(event);
            }
            try{
                br.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }catch(IOException e){
            e.printStackTrace();
            Toast.makeText(CalendarActivity.this, "Error Loading Events", Toast.LENGTH_LONG).show();
        }

    }
    public  boolean isWritePermissionGranted() {
        //Toast.makeText(CalendarActivity.this, "asking permission", Toast.LENGTH_LONG).show();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(CalendarActivity.this, "permission granted", Toast.LENGTH_LONG).show();
                return true;
            } else {
                //Toast.makeText(CalendarActivity.this, "asking permission", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }
    public  boolean isReadPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }
}
