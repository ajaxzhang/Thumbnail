package com.aliyun.thumbnail;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.qupai.editor.AliyunIThumbnailFetcher;
import com.aliyun.qupai.editor.AliyunThumbnailFetcherFactory;
import com.aliyun.struct.common.ScaleMode;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "thumbnailDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        findViewById(R.id.select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choiceVideo();

            }
        });
        findViewById(R.id.thumbnailbtn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                long time = Long.valueOf(((EditText)findViewById(R.id.time)).getText().toString());
                long times[] = {time};
                Log.v(TAG, "" + time);
                getThumbNail(times);
            }
        });
    }

    private void choiceVideo() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 66);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 66 && resultCode == RESULT_OK && null != data) {
            Uri selectedVideo = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedVideo,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex);
            long duration = Long.valueOf(getVideoDuring(path));
            cursor.close();
            String videoInfo = "path = " + path + "\n" + "duration = " + duration;

            ((TextView) findViewById(R.id.path)).setText(path);
            ((TextView) findViewById(R.id.duration)).setText(String.valueOf(duration));

        }
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
    }

    public static String getVideoDuring(String path){
        String duration = "0";
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();

        try {
            if (path != null) {
                mmr.setDataSource(path);
            }
            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        return duration;
    }

    private void getThumbNail(long time[]) {
        AliyunIThumbnailFetcher mThumbnailFetcher = AliyunThumbnailFetcherFactory.createThumbnailFetcher();
        long duration = Long.valueOf(((TextView) findViewById(R.id.duration)).getText().toString());
        String path = ((TextView) findViewById(R.id.path)).getText().toString();
        Log.e(TAG, "getThumbNail path = " + path + " total duration = " + duration);
        if (!path.equalsIgnoreCase("")) {
            mThumbnailFetcher.addVideoSource(path, 0, duration);
        }
        int ret = mThumbnailFetcher.setParameters(360, 180, AliyunIThumbnailFetcher.CropMode.Mediate, ScaleMode.LB, 300);
        Log.d(TAG, "setParams retCode = " + ret);
        mThumbnailFetcher.requestThumbnailImage(time,
                new AliyunIThumbnailFetcher.OnThumbnailCompletion() {
                    @Override
                    public void onThumbnailReady(com.aliyun.common.media.ShareableBitmap shareableBitmap, long l) {
                        Log.v(TAG, "getThumbNail callback time = " + l);
                        ImageView thumbnail = (ImageView) findViewById(R.id.thumbnail);
                        thumbnail.setImageBitmap(shareableBitmap.getData());

                    }
                    @Override
                    public void onError(int code) {
                        Log.e(TAG, "getThumbNail error code = " + code);

                    }
                });
        //mThumbnailFetcher.release();
    }
}
