package common.recordplay.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 *
 * Record Button
 *
 * @author seven
 * @version V1.0
 * @since 16/8/16
 */
public class RecordButton extends Button {

    private static final String START_RECORD = "Start";

    private static final String STOP_RECORD = "Stop";

    private boolean mIsRecording;

    private RecordListener mListener;

    public RecordButton(Context context) {
        super(context);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RecordButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        updateText();
        setOnClickListener(mClickListener);
    }

    public void setRecordListener(RecordListener listener) {
        mListener = listener;
    }

    private void updateText() {
        setText(mIsRecording ? STOP_RECORD : START_RECORD);
    }

    private DelayClickListener mClickListener = new DelayClickListener() {
        @Override
        public void click(View v) {
            mIsRecording = !mIsRecording;
            updateText();
            if (null != mListener) {
                mListener.onRecord(mIsRecording);
            }
        }
    };

    public interface RecordListener {

        void onRecord(boolean recording);
    }
}
