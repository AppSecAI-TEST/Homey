package layout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.project.homey.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.activities.AddTaskActivity;
import app.customcomponents.HomeyProgressDialog;
import app.enums.TaskStatus;
import app.logic.appcomponents.Group;
import app.logic.appcomponents.Task;
import app.logic.managers.DBManager;
import app.logic.managers.GroupManager;
import app.logic.managers.Services;
import app.logic.managers.SessionManager;
import callback.GroupsCallBack;
import callback.TaskCallBack;

import static android.app.Activity.RESULT_OK;

public class FragmentAddTask extends Fragment {

    private static final int RESULT_LOAD_IMAGE = 1;
    private Spinner dropdown;
    private boolean hasPicture = false;
    private HomeyProgressDialog pDialog;
    private byte[] choosedPicture;
    private ArrayList<Group> userGroups;
    private int selectedGroupId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_add_task, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        pDialog = new HomeyProgressDialog(this.getContext());
        dropdown = (Spinner) getView().findViewById(R.id.spinnerTaskGroups);

        Context context = this.getContext();
        List<String> items = new ArrayList<>();
        pDialog.showDialog();
        ((GroupManager) (Services.GetService(GroupManager.class))).GetUserGroups(new GroupsCallBack()
        {
            @Override
            public void onSuccess(ArrayList<Group> groups) {
                userGroups = groups;
                for (Group group : groups)
                {
                    items.add(group.GetName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropdown.setAdapter(adapter);
                dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        getSelectedGroupId((String) parent.getItemAtPosition(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });
                pDialog.hideDialog();
            }

            @Override
            public void onFailure(String error) {
                pDialog.hideDialog();
                //TODO handle connection error
            }
        });
    }

    private void getSelectedGroupId(String GroupName)
    {
        for (Group group : userGroups) {
            if (group.GetName().equals(GroupName)) {
                selectedGroupId = Integer.parseInt(group.GetId());
            }
        }
    }

    public void onAddTaskClick()
    {
        String name = ((EditText) getView().findViewById(R.id.editTextTaskName)).getText().toString();
        String description = ((EditText) getView().findViewById(R.id.editTextTaskDesc)).getText().toString();
        String userId = ((SessionManager) (Services.GetService(SessionManager.class))).getUser().GetUserId();
        int taskScore = 50;
        String location = ((EditText) getView().findViewById(R.id.editTextTaskLocation)).getText().toString();
        Date startDate = new Date(((EditText) getView().findViewById(R.id.editTextTaskStartDate)).getText().toString());
        Date endDate = new Date(((EditText) getView().findViewById(R.id.editTextTaskEndDate)).getText().toString());
        String status = TaskStatus.INCOMPLETE.value();

        if (!hasPicture)
        {
            choosedPicture = new byte[]{0};
        }

        ((DBManager) (Services.GetService(DBManager.class))).AddTask(
                name, description, userId, selectedGroupId, status, location, startDate, endDate,
                taskScore, choosedPicture,
                new TaskCallBack()
                {
                    @Override
                    public void onSuccess(Task result) {
                        Toast.makeText(getContext(), "Task added!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String result) {
                        Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onChooseImageClicked()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null)
        {
            Uri image = data.getData();
            choosedPicture = getBytes(image);
            hasPicture = true;
            ((ImageView)getView().findViewById(R.id.imageViewAddTask)).setImageURI(image);
        }
    }

    private byte[] getBytes(Uri image)
    {
        try
        {
            InputStream inputStream = getView().getContext().getContentResolver().openInputStream(image);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
