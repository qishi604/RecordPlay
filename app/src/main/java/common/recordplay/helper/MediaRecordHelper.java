package common.recordplay.helper;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import common.recordplay.widget.RecordButton;

/**
 *
 * 用MediaRecorder来录音
 * @author seven
 * @version V1.0
 * @since 16/8/16
 */
public class MediaRecordHelper implements RecordButton.RecordListener {

    private static final String TAG = "RecordHelper";

    private String mFileName;

    private MediaRecorder mRecorder;

    private MediaPlayer mPlayer;

    private Handler mHandler;

    public MediaRecordHelper() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        Log.e(TAG, "RecordHelper: file" + mFileName);
        mHandler = new Handler();
    }

    public void startRecord() {
        try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mRecorder.setOutputFile(mFileName);
//            mRecorder.setOutputFile(getFileDescriptor());
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.prepare();
            mRecorder.start();

            // Play the record after 1 second
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    play();
//                }
//            }, 4000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    FileDescriptor getFileDescriptor() throws IOException {
        File f = new File(mFileName);
        if (!f.exists()) {
            f.createNewFile();
        }
        FileInputStream fis = new FileInputStream(mFileName);
        return fis.getFD();
    }


    public void startPlay() {
        mPlayer = new MediaPlayer();

        try {
//            mPlayer.setDataSource(mFileName);
            mPlayer.setDataSource(getFileDescriptor());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        if (null != mRecorder) {
            try {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopPlay() {
        if (mPlayer != null) {
            try {
                mPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPlayer = null;
        }
    }

    public void onDestroy() {
        stopRecord();
        stopPlay();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    public void onRecord(boolean recording) {
        if (recording) {
            startRecord();
        } else {
            onDestroy();
        }
    }
}
