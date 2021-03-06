package layout;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.project.homey.R;

import app.activities.GroupPageActivity;
import app.customcomponents.CircleImageButton;
import app.logic.appcomponents.Group;
import app.logic.managers.EnvironmentManager;
import app.logic.managers.GroupManager;
import app.logic.managers.Services;
import callback.GroupCallBack;

import static android.app.Activity.RESULT_OK;


public class FragmentAddGroup extends Fragment {

    private static final int RESULT_LOAD_IMAGE = 1;
    private boolean hasPicture = false;
    private byte[] choosedPicture;
    private CircleImageButton groupImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_add_group, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        groupImage = (CircleImageButton)getView().findViewById(R.id.chosenGroupImage);

        if (!hasPicture)
        {
            groupImage.setImage(R.mipmap.ic_group_default);
            groupImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    public void onCreateGroup()
    {
        GroupManager groupManager = (GroupManager) Services.GetService(GroupManager.class);
        String name = ((EditText) getView().findViewById(R.id.editTextGroupName)).getText().toString();
        if (!hasPicture)
        {
            choosedPicture = new byte[]{0};
        }

        groupManager.AddNewGroup(name, choosedPicture, new GroupCallBack() {
            @Override
            public void onSuccess(Group group)
            {
                Intent i = new Intent(getContext(), GroupPageActivity.class);
                Bundle b = new Bundle();
                ((EnvironmentManager) (Services.GetService(EnvironmentManager.class))).SetScreenName(group.GetName());
                b.putParcelable("group", group);
                i.putExtras(b);
                startActivity(i);
            }

            @Override
            public void onFailure(String error) {
                ((EditText) getView().findViewById(R.id.editTextGroupName)).setText(error);
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
            choosedPicture = Services.GetBytes(image, getContext());
            hasPicture = true;
            groupImage.setImageBytes(choosedPicture, R.mipmap.ic_group_default);
        }
    }
}
