package com.project.homey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import db.SQLiteHandler;
import services.SessionManager;

public class SettingsActivity extends AppCompatActivity {
    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;

    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        if (!SessionManager.GetInstance().isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        SessionManager.GetInstance().setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
