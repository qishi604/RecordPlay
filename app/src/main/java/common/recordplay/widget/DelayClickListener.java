package common.recordplay.widget;

import android.view.View;

/**
 * @author seven
 * @version V1.0
 * @date 15/11/3
 * @description 延迟click
 */
public abstract class DelayClickListener implements View.OnClickListener {

    private static final int DEFAULT_DELAY = 300;

    private int delay = DEFAULT_DELAY;

    private boolean canClick = true;

    public DelayClickListener() {
    }

    public DelayClickListener(int delay) {
        this.delay = delay;
    }

    @Override
    public void onClick(View v) {
        if (!canClick) {
            return;
        }
        canClick = false;
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                canClick = true;
            }
        }, delay);
        click(v);
    }

    public abstract void click(View v);
}
