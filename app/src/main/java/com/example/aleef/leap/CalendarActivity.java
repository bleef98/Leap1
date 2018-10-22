package com.example.aleef.leap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {
    private CalendarView mCalendarView;
    private Button btnAdd;
    private Button logoutBtn;
    private String fileNameNew = "/storage/self/primary/testSavedEventswDB.txt";
    private String fileNameOld = "/storage/sdcard/testSavedEvents.txt";
    private Boolean fromEventCreate;
    private Boolean delete;
    private Date date;
    long millsDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private ArrayAdapter<String> adapter;
    private ArrayList<String> eventStringArrayList = new ArrayList<String>();
    ArrayList<Event> eventArrayList = new ArrayList<>();
    private ListView listView;
    private FirebaseAuth fbAuth;
    SaveEvents sEvents;
    FirebaseDatabase db;
    DatabaseReference databaseRef;
    FirebaseUser user;
    String uid;
    ValueEventListener listen;
    File file;
    Intent incomingIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null/*savedInstanceState*/);
        setContentView(R.layout.activity_calendar);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        logoutBtn = (Button)findViewById(R.id.logoutBtn);
        fbAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        user = fbAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        databaseRef = db.getReference().child(uid);
        try {
            date = sdf.parse(sdf.format(new Date()));
        }catch (ParseException e){
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 23) {
            file = new File(fileNameNew);
        }
        else{
            file = new File(fileNameOld);
        }

        listen = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int fields = 3;
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    if((int) snap.getChildrenCount()<fields){
                        fields =(int) snap.getChildrenCount();
                        break;
                    }
                }
                if(fields==3){
                    eventArrayList.clear();

                    for(DataSnapshot snap : dataSnapshot.getChildren()){
                        Event event = new Event(snap.child("name").getValue(String.class),
                                snap.child("time").getValue(String.class),
                                snap.child("date").getValue(String.class));
                        eventArrayList.add(event);
                    }

                    checkEventsOnDay();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        databaseRef.addValueEventListener(listen);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//
                logout();
            }
        });

        // gets incoming intent
        try{
            incomingIntent = getIntent();
        }catch(Exception e){
            e.printStackTrace();
        }


        // if coming from delete event, it deletes the event
        try{
            delete = incomingIntent.getExtras().getBoolean("delete");
            if(delete !=null && delete){
                String incomingName = incomingIntent.getStringExtra("eventDelete");
                loadFile(file);
                for(int i=0; i<eventArrayList.size(); i++){
                    if(eventArrayList.get(i).getName().equals(incomingName)){
                        eventArrayList.remove(i);
                    }
                }

                // saves the events to DB
                databaseRef.removeValue();
                for(Integer i=0; i< eventArrayList.size(); i++){
                    databaseRef.child(i.toString()).child("date").setValue(sdf.format(eventArrayList.get(i).getDate()));
                    databaseRef.child(i.toString()).child("name").setValue(eventArrayList.get(i).getName());
                    databaseRef.child(i.toString()).child("time").setValue(eventArrayList.get(i).getTime());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        // if coming from create event, this code creates an event
        try{
            fromEventCreate = incomingIntent.getExtras().getBoolean("create");
            if(fromEventCreate != null && fromEventCreate){
                loadFile(file);
                String name = incomingIntent.getStringExtra("name");
                String time = incomingIntent.getStringExtra("time");
                String strDate = incomingIntent.getStringExtra("strDate");

                Event event = new Event(name, time, strDate);
                eventArrayList.add(event);

                databaseRef.removeValue();

                // saves the events to DB
                for(Integer i=0; i< eventArrayList.size(); i++){
                    databaseRef.child(i.toString()).child("date").setValue(sdf.format(eventArrayList.get(i).getDate()));
                    databaseRef.child(i.toString()).child("name").setValue(eventArrayList.get(i).getName());
                    databaseRef.child(i.toString()).child("time").setValue(eventArrayList.get(i).getTime());
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        databaseRef.addValueEventListener(listen);

        listView = (ListView) findViewById(R.id.eventList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventStringArrayList);
        listView.setAdapter(adapter);

        // changes the date the calender is on to the day it was on before the user clicked
        // create event or clicked on an event.

        try{
            Boolean dateChange = incomingIntent.getExtras().getBoolean("dateChange");
            String strDate = incomingIntent.getStringExtra("strDate");

            if(dateChange != null && dateChange){
                try {
                    date = (sdf.parse(strDate));
                    millsDate = date.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(CalendarActivity.this, "Error going to day",
                            Toast.LENGTH_LONG).show();
                }
                setDate();
            }
        }catch(RuntimeException e){
            e.printStackTrace();
        }
        delete=false;
        fromEventCreate = false;

        // shows the events on the selected day
        checkEventsOnDay();

        // When the user clicks a day on the calendar this code is triggered
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eventClick(position);
            }
        });


    }
    public void setDate(){
        try{
            mCalendarView.setDate(millsDate);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // Called when the user clicks an event
    public void eventClick(int position){
        try{
            saveFile();
        }catch(IOException e){e.printStackTrace(); }

        // gets events all the events from createEventArrayList that are on the same day as the clicked
        // event, and puts them in eventDayArrayList
        ArrayList<Event> eventDayArrayList = new ArrayList<>();
        eventDayArrayList.clear();
        for(int i=0; i< eventArrayList.size();i++){
            if(eventArrayList.get(i).getDate().equals(date)){
                eventDayArrayList.add(eventArrayList.get(i));
            }
        }

        String strDate = sdf.format(date);

        Intent eventPopupActivity = new Intent(CalendarActivity.this, EventOptionsPopupActivity.class);
        eventPopupActivity.putExtra("eventName", eventDayArrayList.get(position).getName());
        eventPopupActivity.putExtra("date", strDate);

        startActivity(eventPopupActivity);
    }

    // Searches createEventArrayList for all events on the selected day and converts each event into 1
    // string and puts that string in eventStringArrayList. eventStringArrayList is then displayed
    // on screen
    public void checkEventsOnDay(){
        try{
            Collections.sort(eventArrayList);
            eventStringArrayList.clear();

            for(int i=0; i< eventArrayList.size();i++){
                if(eventArrayList.get(i).getDate().equals(date)){
                    eventStringArrayList.add(eventArrayList.get(i).toString());
                }else {
                    eventStringArrayList.remove(eventArrayList.get(i).toString());
                }
            }

            // updates the events in the listview so that the user can see them
            adapter.notifyDataSetChanged();
        }catch(NullPointerException e){
            e.printStackTrace();
        }


    }

    public void addBtn() {
        try{
            saveFile();
        }catch(IOException e){e.printStackTrace(); }

        Intent addBtnIntent = new Intent(CalendarActivity.this, CreateEventActivity.class);
        String strDate = sdf.format(date);
        addBtnIntent.putExtra("date", strDate);

        startActivity(addBtnIntent);
    }

    public void saveFile() throws IOException{
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
        Collections.sort(eventArrayList);
    }

    // This checks that the app has permission to Write to the device.
    // If it does not then the user is prompted to give the app permission
    public  boolean isWritePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }
    // This checks that the app has permission to Read from the device.
    // If it does not then the user is prompted to give the app permission
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

    //Menu on create
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.options_menu,menu);
        return true;
    }


    //handle click events on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutMenu:{
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
}