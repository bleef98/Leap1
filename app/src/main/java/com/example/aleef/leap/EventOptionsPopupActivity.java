package com.example.aleef.leap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class EventOptionsPopupActivity extends AppCompatActivity {
    TextView tvEventName;
    Button btnDelete;
    String eventName = new String();
    String date = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This stuff is what makes this activity a small box
        try{
            getSupportActionBar().hide();
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        setContentView(R.layout.activity_event_options_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.75), (int)(height*.4));

        Intent incomingIntent = getIntent();
        eventName = incomingIntent.getStringExtra("eventName");
        date = incomingIntent.getStringExtra("date");

        // Shows the name of the event at the top of the box
        tvEventName = (TextView) findViewById(R.id.tVEventName);
        tvEventName.setText(eventName);

        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent delete = new Intent(EventOptionsPopupActivity.this, CalendarActivity.class);
                delete.putExtra("delete", true);
                delete.putExtra("eventDelete", eventName);
                delete.putExtra("date", date);

                startActivity(delete);
            }
        });
    }
}
