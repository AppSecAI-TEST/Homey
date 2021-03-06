package app.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.homey.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import app.activities.interfaces.IHasImage;
import app.activities.interfaces.IHasText;
import app.customcomponents.CircleImageButton;
import app.customcomponents.HomeyProgressDialog;
import app.customcomponents.ScrollHorizontalWithItems;
import app.customcomponents.ScrollVerticalWithItems;
import app.enums.TaskProperty;
import app.enums.TaskStatus;
import app.logic.appcomponents.Group;
import app.logic.appcomponents.Task;
import app.logic.appcomponents.User;
import app.logic.managers.ActivityChangeManager;
import app.logic.managers.DBManager;
import app.logic.managers.Services;
import app.logic.managers.SessionManager;
import callback.GotoGroupPageCallBack;
import callback.GroupCallBack;
import callback.UpdateCallBack;
import callback.UpdateTask;
import callback.UpdateTaskUsersByTaskIdCallBack;
import callback.UserCallBack;
import callback.UsersCallBack;


public class TaskActivity extends ActivityWithHeaderBase {

    private HomeyProgressDialog pDialog;
    private LinearLayout tasksHolderLayout;
    private ScrollVerticalWithItems scrollVerticalWithItems;
    private Task myTask;
    private Group taskGroup;
    private User taskCreator;
    private ScrollHorizontalWithItems taskAssignees;
    private List<User> taskAssigneesList;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        myTask = getIntent().getExtras().getParcelable("task");
        dbManager = (DBManager) Services.GetService(DBManager.class);
        pDialog = new HomeyProgressDialog(this);

        setActivityComponents();
        setTaskInfo();
    }

    private void setActivityComponents()
    {
        Context context = this;

        //Get task's creator user.
        dbManager.GetUser(myTask.GetCreatorId(), new UserCallBack() {
            @Override
            public void onSuccess(User user) {
                taskCreator = user;
                ((TextView) findViewById(R.id.taskActivityCreatorLabel)).setText(taskCreator.GetName());
                CircleImageButton creatorImage = (CircleImageButton) findViewById(R.id.taskActivityCreatorImage);
                creatorImage.setOnClickListener(view->
                {
                    ((ActivityChangeManager) Services.GetService(ActivityChangeManager.class)).SetProfileActivity(context, taskCreator);
                });

                //CREATOR IMAGE
                setButtonImage(creatorImage, taskCreator.GetImage(), R.mipmap.ic_profile_default);
            }

            @Override
            public void onFailure(String error)
            {
                Toast.makeText(TaskActivity.this, "Creator id " + error, Toast.LENGTH_SHORT).show();
            }
        });

        //Get task's Group.
        dbManager.GetGroup(myTask.GetGroupId(), new GroupCallBack() {
            @Override
            public void onSuccess(Group group) {
                taskGroup = group;
                ((TextView) findViewById(R.id.taskActivityGroupLabel)).setText(taskGroup.GetName());
                CircleImageButton groupImage = (CircleImageButton) findViewById(R.id.taskActivityGroupImage);

                groupImage.setOnClickListener(view ->
                {
                    ((ActivityChangeManager) Services.GetService(ActivityChangeManager.class)).SetGroupActivity(context, taskGroup);
                });

                //GROUP IMAGE
                setButtonImage(groupImage, taskGroup.GetImage(), R.mipmap.ic_task_default);
            }

            @Override
            public void onFailure(String error)
            {
                ((TextView) findViewById(R.id.taskActivityGroupLabel)).setText("error");
                Toast.makeText(TaskActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setButtonImage(CircleImageButton buttonImage, byte[] image, int defaultImgId)
    {
        buttonImage.setImageBytes(image, defaultImgId);
    }

    private void setTaskInfo() {
        setScreenName(myTask.GetName());
        Context context = this;

        if (myTask.GetStartTime() != null) {
            ((TextView) findViewById(R.id.taskActivityStartDate)).setText(myTask.GetStartTime().toString());
        } else {
            ((TextView) findViewById(R.id.taskActivityStartDate)).setText("-");
        }

        if (myTask.GetEndTime() != null) {
            ((TextView) findViewById(R.id.taskActivityEndDate)).setText(myTask.GetEndTime().toString());
        } else {
            ((TextView) findViewById(R.id.taskActivityEndDate)).setText("-");
        }

        ((TextView) findViewById(R.id.taskActivityDescription)).setText(myTask.GetDescription());

        //TASK IMAGE:
        CircleImageButton taskImageButton = (CircleImageButton) findViewById(R.id.taskActivityTaskImage);
        setButtonImage(taskImageButton, myTask.GetImg(), R.mipmap.ic_task_default);

        //Task Participants:
        dbManager.GetTaskUsersByTaskId(Integer.parseInt(myTask.GetTaskId()), new UsersCallBack() {
            @Override
            public void onSuccess(ArrayList<User> users)
            {
                taskAssigneesList = users;
                taskAssignees = (ScrollHorizontalWithItems) findViewById(R.id.taskActivityTaskAssignee);
                taskAssignees.SetScrollerItems(users, LinearLayoutCompat.HORIZONTAL, new GotoGroupPageCallBack() {
                    @Override
                    public <T extends IHasImage & IHasText> void onSuccess(T user) {
                        ((ActivityChangeManager)Services.GetService(ActivityChangeManager.class)).SetProfileActivity(context, (User) user);
                    }

                    @Override
                    public void onFailure(String error) {

                    }
                });
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    public void buttonCompleteOnClicked(View view) {
        UpdateCallBack updateCallBack = new UpdateTask(this.getBaseContext(), 1);

        myTask.setStatus(TaskStatus.COMPLETED);
        dbManager.UpdateTask(myTask.GetTaskId(), TaskProperty.STATUS, TaskStatus.COMPLETED, updateCallBack);

    }

    public void buttonTakeOnClicked(View view) {
        String userId = ((SessionManager) (Services.GetService(SessionManager.class))).getUser().GetUserId();
        Stream<User> taskAssigneesStream = taskAssigneesList.stream().filter(user -> user.GetUserId().equals(userId));

        if (taskAssigneesStream.count() > 0) {
            dbManager.UpdateTaskUsersByTaskId(myTask.GetTaskId(), new UpdateTaskUsersByTaskIdCallBack() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(String errorMessage) {

                }
            });
        }
    }

    public void buttonDeleteTaskOnClicked(View view) {
        String userId = ((SessionManager) (Services.GetService(SessionManager.class))).getUser().GetUserId();
        dbManager.LeaveTask(myTask.GetTaskId().toString(), userId, new UpdateCallBack() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }
}
