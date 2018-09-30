package com.example.aleef.leap;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event implements Comparable<Event>{
    private String name;
    private String time;
    private Date date;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public Event(String name, String time){
        this.name = name;
        this.time = time;
        try{
            this.date = sdf.parse("16/09/2018");
        }catch(ParseException e){
        }
    }
    public Event(String name, String time, Date date){
        this.name = name;
        this.time = time;
        this.date = date;
    }
    public Event(String name, String time, String date){
        this.name = name;
        this.time = time;
        try {
            this.date = sdf.parse(date);
        }catch(ParseException e){
        }
    }
    @Override
    public int compareTo(Event compare) {
        int comp = getDate().compareTo(compare.getDate());
        if(comp != 0)
            return comp;
        return getTime().compareTo(compare.getTime());
    }

    public String toCsvString(){
        return getName()+","+getTime()+","+sdf.format(getDate());
    }

    @Override
    public String toString() {
        return String.format("%-14s", getName()) + getTime();
        //return getName() + " " + getTime() + " " + sdf.format(getDate());
    }

    public String getName(){
        return name;
    }
    public String getTime(){
        return time;
    }
    public Date getDate(){
        return date;
    }
    public void setName(String name){
        this.name = name;
    }


}