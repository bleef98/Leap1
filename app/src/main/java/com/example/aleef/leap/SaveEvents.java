package com.example.aleef.leap;

import com.google.firebase.database.DatabaseReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class is not currently used.
 * It was intended to be used for saving events
 */
public class SaveEvents {
    DatabaseReference databaseRef;
    ArrayList<Event> saveList;
    String uid;
    final String saveArrayName;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public SaveEvents(DatabaseReference ref, String uid, String arrayName){
        databaseRef = ref;
        this.uid = uid;
        saveArrayName = arrayName;
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

        return saveList;
    }
}
