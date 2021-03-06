package app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.project.homey.R;

import app.database.SQLiteHandler;
import app.logic.managers.Services;
import app.logic.managers.SessionManager;

public class LogoutActivity extends ActivityBase {
    private Button btnLogout;

    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        btnLogout = (Button) findViewById(R.id.buttonLogout);

        // SqLite app.database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        if (!((SessionManager) (Services.GetService(SessionManager.class))).isLoggedIn()) {
            logoutUser();
        }

        // Logout button click event
        btnLogout.setOnClickListener(v -> logoutUser());
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        ((SessionManager) (Services.GetService(SessionManager.class))).setLogin(false);

        ((SessionManager) (Services.GetService(SessionManager.class))).LogOut();
        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
