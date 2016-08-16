package common.recordplay.helper;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.LinkedList;

import common.recordplay.widget.RecordButton;

/**
 * 用AudioRecorder来录音
 *
 * @author seven
 * @version V1.0
 * @since 16/8/16
 */
public class RecordHelper implements RecordButton.RecordListener {

    private static final String TAG = "RecordHelper";

    private AudioRecord mRecorder;

    private AudioTrack mPlayer;

    private LinkedList<byte[]> mBufList;

    private byte[] mRecordData;

    private volatile boolean isRecording;

    private volatile boolean isPlaying;

    public RecordHelper() {
    }

    private void init() {
        if (null == mRecorder) {
            synchronized (this) {
                if (null == mRecorder) {
                    initRecord();
                }
            }
        }
    }

    private void initRecord() {
        mBufList = new LinkedList<>();

        try {
//            int hz = 44100;
            int hz = 192000;
            int channel = AudioFormat.CHANNEL_IN_STEREO;
            int format = AudioFormat.ENCODING_PCM_16BIT;

            int bufSize = AudioRecord.getMinBufferSize(hz, channel, format);

            mRecordData = new byte[bufSize];

            Log.i(TAG, "buffer size " + bufSize);

            mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    hz,
                    channel,
                    format,
                    bufSize
            );

            mPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, hz, channel, format, bufSize, AudioTrack.MODE_STREAM);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread() {

            @Override
            public void run() {
                super.run();
                record();
            }
        }.start();

        new Thread() {

            @Override
            public void run() {
                super.run();
                play();
            }
        }.start();
    }

    public void stop() {
        onDestroy();
    }

    public void record() {
        init();
        isRecording = true;
        mRecorder.startRecording();

        while (isRecording) {
            mRecorder.read(mRecordData, 0, mRecordData.length);
            mBufList.add(mRecordData.clone());
            Log.i(TAG, "record: " + mBufList.size());
        }
    }

    public void play() {
        init();

        isPlaying = true;
        try {
            mPlayer.play();

            while (isPlaying) {
                if (mBufList.size() > 0) {
                    byte[] data = mBufList.removeFirst();
                    mPlayer.write(data, 0, data.length);
                    Log.i(TAG, "play: " + mBufList.size());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        isRecording = false;
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
        isPlaying = false;
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
    }

    @Override
    public void onRecord(boolean recording) {
        if (recording) {
            start();
        } else {
            stop();
        }
    }
}
