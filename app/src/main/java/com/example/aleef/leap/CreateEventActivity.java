package com.example.aleef.leap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * When the user clicks create event this class is called.
 * The date the user had selected when they clicked create event is passed in through an Intent.
 * When the user clicks create the name and time they entered is validated. If they are valid
 * inputs then then date, name and time are passed to CalendarActivity through an intent where
 * they are turned into an event and saved.
 *
 */
public class CreateEventActivity  extends AppCompatActivity {
    private EditText name;
    private EditText time;
    private String strDate;
    private TextView timeFormatWarning;
    private TextView missingInputWarning;
    ArrayList<Event> createEventArrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Intent incoming = getIntent();
        strDate = incoming.getStringExtra("date");

        //new
        //Bundle args = incoming.getBundleExtra("args");
        //createEventArrayList =(ArrayList<Event>) args.getSerializable("arraylist");

        missingInputWarning = (TextView) findViewById(R.id.missingInputWarningText);
        timeFormatWarning = (TextView) findViewById(R.id.badInputTypeText);
        timeFormatWarning.setVisibility(View.INVISIBLE);
        missingInputWarning.setVisibility(View.INVISIBLE);
        Button btnCreate = (Button) findViewById(R.id.btnCreate);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        name = (EditText) findViewById(R.id.editTextName);
        time = (EditText) findViewById(R.id.editTextTime);


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validates input and tells the user if and why the input was not valid
                if(isInputEmpty(name.getText().toString(), time.getText().toString())){
                    missingInputWarning.setVisibility(View.VISIBLE);
                }
                else if(isInvalidTime(time.getText().toString())){
                    timeFormatWarning.setVisibility(View.VISIBLE);
                }
                else{
                    /**
                     * if the input is valid then the input and the date are passed back to
                     * CalendarActivity. Information is passed as Strings because Event objects
                     * cannot be passed through an Intent. Create is set to true to tell
                     * CalendarActivity that the information to create an Event has been passed in.
                      */
                    Intent intent = new Intent(CreateEventActivity.this, CalendarActivity.class);
                    intent.putExtra("create", true);
                    intent.putExtra("name", name.getText().toString());
                    intent.putExtra("time", time.getText().toString());
                    intent.putExtra("dateChange", true);
                    intent.putExtra("strDate", strDate);

                    startActivity(intent);
                }
            }
        });
        /**
         * If the user clicks cancel then the application goes back to CalendarActivity without
         * passing any data.
         */
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventActivity.this, CalendarActivity.class);
                intent.putExtra("dateChange", true);
                intent.putExtra("strDate", strDate);
                startActivity(intent);
            }
        });
    }

    // Ensures that no input fields are blank
    public boolean isInputEmpty(String nameInput, String timeInput){
        if(nameInput.equals("") || timeInput.equals("")){
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * Checks the format of the inputed time. For example 12:10.
     * Must be 2 numbers then ":" then 2 numbers.
     * If the input is invalid it returns true.
      */
    public boolean isInvalidTime(String timeInput){
        if(timeInput.matches("\\d{2}:\\d{2}")){
            return false;
        }
        else{
            return true;
        }
    }
}