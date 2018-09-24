package com.example.aleef.leap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class RegisterActivity extends AppCompatActivity {

    private EditText registerEmail;
    private EditText registerFirstName;
    private EditText registerLasttName;
    private EditText registerPassword;
    private Button registerBtn;
    private TextView loginLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupUIview();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    //Upload data to database

                }
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    //Set up the register view
    private void setupUIview(){
        registerFirstName = (EditText)findViewById(R.id.first_name_register);
        registerLasttName = (EditText)findViewById(R.id.last_name_register);
        registerEmail = (EditText)findViewById(R.id.register_email);
        registerPassword = (EditText)findViewById(R.id.password_register);
        registerBtn = (Button)findViewById(R.id.register_btn);
        loginLink = (TextView)findViewById(R.id.log_in_link);
    }

    /**private void registerUser(){
        String email = registerEmail.getText().toString().trim();
        String password = registerPassword.getText().toString();

        if(email.isEmpty() && password.isEmpty()){
            registerEmail.setError();
        }
    }**/


    private boolean validate(){
        boolean result = false;

        String firstName = registerFirstName.getText().toString();
        String lastName = registerLasttName.getText().toString();
        String email = registerEmail.getText().toString();
        String password = registerPassword.getText().toString();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Please enter a valid Email", Toast.LENGTH_SHORT).show();
        }

        if(firstName.isEmpty() && lastName.isEmpty() && email.isEmpty() && password.isEmpty()){
            Toast.makeText(this,"Please enter all the details", Toast.LENGTH_SHORT).show();
        }else{
            result = true;
        }

        return result;
    }
}
