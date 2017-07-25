package app.customcomponents;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;

import app.activities.interfaces.IHasText;
import app.logic.appcomponents.Task;
import callback.GoToTaskPageCallBack;


/**
 * TODO: document your custom view class.
 */
public class ScrollVerticalWithItems extends ScrollView {

    LinearLayout linearLayout;

    public ScrollVerticalWithItems(Context context) {
        super(context);
        linearLayout = new LinearLayout(context);
        this.addView(linearLayout);
    }

    public ScrollVerticalWithItems(Context context, AttributeSet attrs) {
        super(context, attrs);

        linearLayout = new LinearLayout(context);
        this.addView(linearLayout);
    }

    public ScrollVerticalWithItems(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        linearLayout = new LinearLayout(context);
        this.addView(linearLayout);
    }


    public <T extends IHasText> void SetTasks(ArrayList<T> tasks, GoToTaskPageCallBack callBack) {

        linearLayout.removeAllViews();
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        //TODO: change the for to foreach with stream.
        linearLayout.addView(new Separator(getContext()));
        for (int i = 0; i < tasks.size(); i++) {
            TaskLayout taskLayout = new TaskLayout(this.getContext());
            taskLayout.setDescription(tasks.get(i).GetDescription());
            taskLayout.SetOnClick(callBack, (Task) tasks.get(i));
            linearLayout.addView(taskLayout);
            linearLayout.addView(new Separator(getContext()));
        }
    }
}
