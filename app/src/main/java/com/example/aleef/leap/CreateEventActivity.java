package com.example.aleef.leap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateEventActivity  extends AppCompatActivity {
    private EditText name;
    private EditText time;
    private String strDate;
    private TextView timeFormatWarning;
    private TextView missingInputWarning;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Intent incoming = getIntent();
        strDate = incoming.getStringExtra("date");
        Toast.makeText(CreateEventActivity.this, strDate, Toast.LENGTH_LONG).show();


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
                //createBtn(name.getText().toString(), time.getText().toString());
                if(isInputEmpty(name.getText().toString(), time.getText().toString())){
                    missingInputWarning.setVisibility(View.VISIBLE);
                }
                else if(isInvalidTime(time.getText().toString())){
                    timeFormatWarning.setVisibility(View.VISIBLE);
                }
                else{
                    Intent intent = new Intent(CreateEventActivity.this, CalendarActivity.class);
                    intent.putExtra("create", true);
                    intent.putExtra("name", name.getText().toString());
                    intent.putExtra("time", time.getText().toString());
                    intent.putExtra("strDate", strDate);

                    startActivity(intent);
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });
    }
    public boolean isInputEmpty(String nameInput, String timeInput){
        if(nameInput.equals("") || timeInput.equals("")){
            return true;
        }
        else{
            return false;
        }
    }
    public boolean isInvalidTime(String timeInput){
        if(timeInput.matches("\\d{2}:\\d{2}")){
            return false;
        }
        else{
            return true;
        }
    }

    public int createBtn(String nameInput, String timeInput){

        if(nameInput.equals("") || timeInput.equals("")){
            missingInputWarning.setVisibility(View.VISIBLE);
            return 0;
        }
        else if(!(timeInput.matches("\\d{2}:\\d{2}"))){
            timeFormatWarning.setVisibility(View.VISIBLE);
            return 1;
        }
        else{
            Intent intent = new Intent(CreateEventActivity.this, CalendarActivity.class);
            intent.putExtra("create", true);
            intent.putExtra("name", nameInput);
            intent.putExtra("time", timeInput);
            intent.putExtra("strDate", strDate);

            startActivity(intent);
            return 2;
        }
    }
}
