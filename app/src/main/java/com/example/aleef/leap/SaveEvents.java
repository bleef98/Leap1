package com.example.aleef.leap;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaveEvents {
    DatabaseReference databaseRef;
    ArrayList<Event> saveList;
    String uid;
    final String saveArrayName;
    GenericTypeIndicator<ArrayList<Event>> type;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public SaveEvents(DatabaseReference ref, String uid, String arrayName){
        databaseRef = ref;
        this.uid = uid;
        saveArrayName = arrayName;
        type = new GenericTypeIndicator<ArrayList<Event>>() {};
    }
    public void save(ArrayList list){
        saveList = list;
        Date date = new Date();
        try{
            date = sdf.parse("16/10/2018");
        }catch(ParseException e){}

        Event testEvent = new Event("broken", "24:00", date);
        Event testEvent1 = new Event("broken1", "23:00", date);
        Event testEvent2 = new Event("broken2", "22:00", date);
        saveList.add(testEvent1);
        saveList.add(testEvent2);
        saveList.add(testEvent);

        databaseRef.child(uid).child(saveArrayName).setValue(saveList);
    }
    public ArrayList<Event> load(){
        saveList = new ArrayList<Event>();
        Date date = new Date();
        try{
            date = sdf.parse("16/10/2018");
        }catch(ParseException e){}

        Event testEvent = new Event("broken", "24:00", date);
        saveList.add(testEvent);
/*
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //saveList = (ArrayList<Event>) dataSnapshot.child(uid).child(saveArrayName).getValue(type);

                for(DataSnapshot snap : dataSnapshot.child(uid).child(saveArrayName).getChildren()){
                    Event event = new Event(snap.child(uid).child(saveArrayName).child("name").getValue(String.class),
                            snap.child(uid).child(saveArrayName).child("time").getValue(String.class),
                            snap.child(uid).child(saveArrayName).child("date").getValue(Date.class));
                    saveList.add(event);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        return saveList;
    }
}
