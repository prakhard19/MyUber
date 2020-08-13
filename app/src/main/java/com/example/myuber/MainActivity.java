package com.example.myuber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity {
    EditText mEditText, mEditText1, mEditText2;
    Button mButton, mButton1;
    RadioButton driverRadioButton, PassengerRadioButton;
    private State state;

    enum State {
        SIGNUP, LOGIN
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = findViewById(R.id.edtUsername);
        mEditText1 = findViewById(R.id.edtPassword);
        mEditText2 = findViewById(R.id.edtan);
        mButton = findViewById(R.id.btnsignup);
        mButton1 = findViewById(R.id.btnone);
        driverRadioButton = findViewById(R.id.radioButton);
        PassengerRadioButton = findViewById(R.id.radioButton2);
        state = State.SIGNUP;
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (state == State.SIGNUP) {

                    if (driverRadioButton.isChecked() == false && PassengerRadioButton.isChecked() == false) {
                        Toast.makeText(MainActivity.this, "Are you a driver or a passenger?", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ParseUser appUser = new ParseUser();
                    appUser.setUsername(mEditText.getText().toString());
                    appUser.setPassword(mEditText1.getText().toString());
                    if (driverRadioButton.isChecked()) {
                        appUser.put("as", "Driver");

                    } else if (PassengerRadioButton.isChecked()) {
                        appUser.put("as", "Passenger");
                    }
                    appUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                Toast.makeText(getApplicationContext(),"signed up",Toast.LENGTH_SHORT).show();
                                transitionToPassengerActivity();
                            }
                            else {

                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                            }

                        }
                    });

                }

               else if(state== State.LOGIN){



                    ParseUser.logInInBackground(mEditText.getText().toString(),mEditText1.getText().toString() , new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (parseUser != null) {
                                Toast.makeText(getApplicationContext(),"User logged in",Toast.LENGTH_SHORT).show();
                                transitionToPassengerActivity();
                                transitionToDriverActivity();
                            }
                        }
                    });
                }


            }
        });









mButton1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        if (mEditText2.getText().toString().equals("Driver") || mEditText2.getText().toString().equals("Passenger")) {

            if (ParseUser.getCurrentUser() == null) {
                ParseAnonymousUtils.logIn(new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null && e == null) {
                            Toast.makeText(MainActivity.this, "We have an anonymous user", Toast.LENGTH_SHORT).show();


                            user.put("as", mEditText2.getText().toString());
user.saveInBackground();
                        }
                    }
                });
            }
        }
    }
});

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);


    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.item1:

                if (state == State.SIGNUP) {
                    state = State.LOGIN;
                    item.setTitle("Sign Up");
                  mButton.setText("Log In");
                } else if (state == State.LOGIN) {

                    state = State.SIGNUP;
                    item.setTitle("Log In");
                 mButton.setText("Sign Up");
                }


                break;
        }



        return super.onOptionsItemSelected(item);
    }

    private void transitionToPassengerActivity() {

        if (ParseUser.getCurrentUser() != null) {

            if (ParseUser.getCurrentUser().get("as").equals("Passenger")) {

                Intent intent = new Intent(MainActivity.this, passengerActivity.class);
                startActivity(intent);
            }

        }

    }
    void  transitionToDriverActivity(){

        if (ParseUser.getCurrentUser() != null) {

            if (ParseUser.getCurrentUser().get("as").equals("Driver")) {

                Intent intent = new Intent(MainActivity.this, DriverRequestList.class);
                startActivity(intent);
            }

        }


    }

}