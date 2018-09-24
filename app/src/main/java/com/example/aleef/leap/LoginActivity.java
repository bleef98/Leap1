package com.example.aleef.leap;

import android.content.Intent;
import android.icu.text.IDNA;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail;
    private EditText loginPassword;
    private TextView loginCount;
    private Button loginBtn;
    private int count = 5;
    private TextView registerLink;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupUIview();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFill()){
                    //Check data with database whether it exists and is correct
                }
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }

    //Set up the UI when the activity is created
    private void setupUIview(){
        loginEmail = (EditText)findViewById(R.id.login_email);
        loginPassword = (EditText)findViewById(R.id.login_password);
        loginCount = (TextView)findViewById(R.id.login_count_txt);
        loginBtn = (Button)findViewById(R.id.login_btn);
        registerLink = (TextView)findViewById(R.id.register_link);

        loginCount.setText("No of attempts remaining: 5");
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePw(loginEmail.getText().toString(), loginPassword.getText().toString());
            }
        });
    }

    //Validate whether user has filled in the necessary fields
    private boolean validateFill(){
        boolean result = false;

        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        if(email.isEmpty() && password.isEmpty()){
            Toast.makeText(this,"Please enter all the details", Toast.LENGTH_SHORT).show();
        }else{
            result = true;
        }
        return result;
    }

    private void validatePw(String email, String password){
        if((email == "Admin") && (password == "1234")){
            //Log in stub. Will fix this to be dynamically linked to database
           // Intent intent = new Intent(LoginActivity.this, SecondActivity.class);
           // startActivity(intent);
        }else{
            count--;

            loginCount.setText("No of attempts remaining: "+ String.valueOf(count));
            if(count == 0){
                loginBtn.setEnabled(false);
            }

        }
    }



}
