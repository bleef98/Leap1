package com.example.aleef.leap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;
    String[] options;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



        Button btnUpdate = (Button) findViewById(R.id.button2);
        Button btnBack = (Button) findViewById(R.id.btnBack);
        Button logoutBtn = (Button)findViewById(R.id.logout_btn);

        ListView listView = (ListView) findViewById(R.id.listSettings);


        options = new String[] {"Friends", "Settings"};
        arrayList = new ArrayList<>(Arrays.asList(options));
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.list_item, arrayList);

        listView.setAdapter(adapter);

        /**logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });**/


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnBackClick();
            }
        });

        // testing how to update list
        // delete later
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                re();
            }
        });

    }

    // goes back to CalendarActivity
    public void onBtnBackClick(){
        Intent intent = new Intent(SettingsActivity.this, CalendarActivity.class);
        startActivity(intent);
    }

    public void re(){
        arrayList.add("Frds");
        arrayList.add("Seings");
        arrayList.add("wow");

        adapter.notifyDataSetChanged();
    }
}
