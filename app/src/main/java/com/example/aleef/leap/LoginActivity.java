package com.example.aleef.leap;

import android.content.Intent;
import android.icu.text.IDNA;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail;
    private EditText loginPassword;
    private TextView loginCount;
    private Button loginBtn;
    private int count = 5;
    private TextView registerLink;
    private FirebaseAuth fbAuth;



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

        fbAuth = FirebaseAuth.getInstance();

        //object to check if a user is already logged in
        FirebaseUser fbUser = fbAuth.getCurrentUser();

        /**if(fbUser != null){
            finish();
            startActivity(new Intent(LoginActivity.this,CalendarActivity.class));
        }**/


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                validateUser(email,password);
            }
        });
    }

    //Validate whether user has filled in the necessary fields
    private boolean validateFill(){
        boolean result = false;

        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Please enter a valid Email", Toast.LENGTH_SHORT).show();
        }

        if(email.isEmpty() && password.isEmpty()){
            Toast.makeText(this,"Please enter all the details", Toast.LENGTH_SHORT).show();
        }else{
            result = true;
        }
        return result;
    }

    private void validateUser(String email, String password){
        fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, CalendarActivity.class));
                }else{
                    Toast.makeText(LoginActivity.this,"Login Failure",Toast.LENGTH_SHORT).show();
                    count--;
                    if(count == 0){
                        loginCount.setText("No of attempts remaining: "+count);
                        loginBtn.setEnabled(false);
                    }
                }
            }
        });

    }



}
