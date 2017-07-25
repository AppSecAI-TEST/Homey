package app.activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.project.homey.R;

import app.customcomponents.HomeyProgressDialog;
import app.customcomponents.ScrollVerticalWithItems;


public class TaskActivity extends ActivityWithHeaderBase {

    private HomeyProgressDialog pDialog;
    private LinearLayout tasksHolderLayout;
    private ScrollVerticalWithItems scrollVerticalWithItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        pDialog = new HomeyProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadTaskInformation();
    }

    private void loadTaskInformation() {
        //pDialog.showDialog();
    }
}
