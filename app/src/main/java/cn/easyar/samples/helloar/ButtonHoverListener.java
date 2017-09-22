package cn.easyar.samples.helloar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by bmolnar on 21.09.2017.
 */

public class ButtonHoverListener implements View.OnHoverListener {

    private final Button button;
    private final Context context;

    public ButtonHoverListener(Button button, Context context) {
        this.button = button;
        this.context = context;
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        Drawable img = context.getResources().getDrawable(R.drawable.microphone_normal);
        if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
            img = context.getResources().getDrawable(R.drawable.microphone_blue);
        }
        if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT || event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
            img.setBounds(0, 0, 24, 24);
            button.setCompoundDrawables(img, null, null, null);
//                    event.getAction();
        }
        return false;
    }
}
