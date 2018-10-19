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
    //private Button btnSettings;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null/*savedInstanceState*/);
        setContentView(R.layout.activity_calendar);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        logoutBtn = (Button)findViewById(R.id.logoutBtn);
        //btnSettings = (Button) findViewById(R.id.btnSettings);
        fbAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        user = fbAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        final String saveArrayName = "EventsList";

        databaseRef = db.getReference().child(uid);//.child(saveArrayName);
        //databaseRef.
        // instantiates date
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
        // testing saving
        //sEvents = new SaveEvents(databaseRef, uid, saveArrayName);

        // testing loading
        /*Date date2 = new Date();
        try{
            date2 = sdf.parse("20/10/1899");
        }catch(ParseException e){}
        Event testEvent = new Event("notnull", "01:11", date2);
        eventArrayList.add(testEvent);
        Event testEvent2 = new Event("notnull2", "01:12", date2);
        //eventArrayList.add(testEvent2);
*/
        //databaseRef.child(uid).child("EventsList").setValue(createEventArrayList);

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
                    //createEventArrayList = (ArrayList<Event>) dataSnapshot.child(uid).child(saveArrayName).getValue(type);
                    System.out.println("This reading DATABASE");

                    //Integer i=1;
                    Integer index=0;
                    for(DataSnapshot snap : dataSnapshot.getChildren()){
                        //try {
                    /*Event event = new Event(snap.child("name").getValue(String.class),
                            snap.child("time").getValue(String.class),
                            sdf.format(snap.child("date").getValue(Date.class)));
                    eventArrayList.add(event);*/
                        //}catch(ParseException e){e.printStackTrace();}

                        Event event = new Event(snap.child("name").getValue(String.class),
                                snap.child("time").getValue(String.class),
                                snap.child("date").getValue(String.class));
                        eventArrayList.add(event);
                        index++;
                        //btnAdd.setText(i.toString());
                        //i++;
                        //Date getDate = new Date();
                        //getDate = snap.child(uid).child(saveArrayName).child("date").getValue(Date.class);
                        //getDate.
                    }

                    checkEventsOnDay();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        databaseRef.addValueEventListener(listen);

        //testing
        //Event testEvent3 = new Event("notnull3", "01:13", date2);
        //createEventArrayList.add(testEvent3);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//
                logout();
            }
        });

        //Toast.makeText(CalendarActivity.this, fileNameNew,Toast.LENGTH_LONG).show();
        //loadFile(file);

        // if coming from delete event, it deletes the event
        try{
            Intent incomingIntent = getIntent();
            delete = incomingIntent.getExtras().getBoolean("delete");
            String incomingName = incomingIntent.getStringExtra("eventDelete");
            String strDate = incomingIntent.getStringExtra("date");
            if(delete !=null && delete){
                loadFile(file);
                for(int i=0; i<eventArrayList.size(); i++){
                    if(eventArrayList.get(i).getName().equals(incomingName)){
                        eventArrayList.remove(i);
                    }
                }
                // sets the date to the date it was before deleting event
                try {
                    date = (sdf.parse(strDate));
                    millsDate = date.getTime();

                }catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(CalendarActivity.this, "Error going to day",
                            Toast.LENGTH_LONG).show();
                }

                // saves the events to DB
                databaseRef.removeValue();
                for(Integer i=0; i< eventArrayList.size(); i++){
                    databaseRef.child(i.toString()).child("date").setValue(sdf.format(eventArrayList.get(i).getDate()));
                    databaseRef.child(i.toString()).child("name").setValue(eventArrayList.get(i).getName());
                    databaseRef.child(i.toString()).child("time").setValue(eventArrayList.get(i).getTime());
                }
                /*try{
                    saveFile();
                }catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(CalendarActivity.this, "Error Saving Events",
                            Toast.LENGTH_LONG).show();
                }*/

            }
        }catch(RuntimeException e){
            e.printStackTrace();
        }

        // if coming from create event, this code creates an event
        try{
            Intent incoming = getIntent();
            fromEventCreate = incoming.getExtras().getBoolean("create");
            if(fromEventCreate != null && fromEventCreate){
                loadFile(file);
                String name = incoming.getStringExtra("name");
                String time = incoming.getStringExtra("time");
                String strDate = incoming.getStringExtra("strDate");
                try {
                    date = (sdf.parse(strDate));
                    millsDate = date.getTime();

                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(CalendarActivity.this, "Error going to day",
                            Toast.LENGTH_LONG).show();
                }
                /*Toast.makeText(CalendarActivity.this, "working",
                        Toast.LENGTH_LONG).show();*/
                Event event = new Event(name, time, strDate);
                eventArrayList.add(event);

                databaseRef.removeValue();
                // saves the events to DB
                for(Integer i=0; i< eventArrayList.size(); i++){
                    databaseRef.child(i.toString()).child("date").setValue(sdf.format(eventArrayList.get(i).getDate()));
                    databaseRef.child(i.toString()).child("name").setValue(eventArrayList.get(i).getName());
                    databaseRef.child(i.toString()).child("time").setValue(eventArrayList.get(i).getTime());
                }
                //new
                //createEventArrayList = incoming.getExtras().getParcelableArrayList("parcel");
                //Bundle args = incoming.getBundleExtra("args");
                //eventArrayList = (ArrayList<Event>) args.getSerializable("arrraylist");

                // saves the events

            }

        }catch(RuntimeException e){
            e.printStackTrace();
        }
        databaseRef.addValueEventListener(listen);

        listView = (ListView) findViewById(R.id.eventList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventStringArrayList);
        listView.setAdapter(adapter);

        // changes the date the calender is on to the day it was on before the user clicked
        // create event.  In the  future it will do the same for when the user clicks delete event
        try{
            if(fromEventCreate || delete){
                try{
                    mCalendarView.setDate(millsDate);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }catch(NullPointerException e){
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
            //Toast.makeText(CalendarActivity.this, eventStringArrayList.get(0), Toast.LENGTH_LONG).show();

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

        //addBtnIntent.putParcelableArrayListExtra((ArrayList<Event extends Parcelable>) createEventArrayList);
        //args.putSerializable("Events", (Serializable)createEventArrayList);

        //if(createEventArrayList.size()>0){
        //    Bundle args = new Bundle();
        //    args.putSerializable("arraylist", (Serializable)eventArrayList);
        //addBtnIntent.putExtras(args);
        //}

        startActivity(addBtnIntent);
    }
    /**public void settingsBtn(){
        Intent settingsBtnIntent = new Intent(CalendarActivity.this, SettingsActivity.class);
        startActivity(settingsBtnIntent);
    }**/


    public void saveFile() throws IOException{
        //
        //databaseRef.setValue(eventArrayList);
        //databaseRef.removeEventListener(listen);
        /*
        for(Integer i=0; i< eventArrayList.size(); i++){
            databaseRef.child(i.toString()).child("date").setValue(sdf.format(eventArrayList.get(i).getDate()));
            databaseRef.child(i.toString()).child("name").setValue(eventArrayList.get(i).getName());
            databaseRef.child(i.toString()).child("time").setValue(eventArrayList.get(i).getTime());
        }*/

        //sEvents.save(createEventArrayList);

        isWritePermissionGranted();
        FileOutputStream fos = new FileOutputStream(file);
        for(Event event: eventArrayList){
            fos.write(event.toCsvString().getBytes());
            fos.write("\n".getBytes());
        }
        fos.close();
    }
    public void loadFile(File file){
        //createEventArrayList.clear();
        //createEventArrayList = sEvents.load();


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
            //Toast.makeText(CalendarActivity.this, "Error Loading Events", Toast.LENGTH_LONG).show();
        }
        Collections.sort(eventArrayList);
    }

    // This checks that the app has permission to Write to the device.
    // If it does not then the user is prompted to give the app permission
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