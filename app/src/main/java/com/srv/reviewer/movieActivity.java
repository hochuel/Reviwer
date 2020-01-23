package com.srv.reviewer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class movieActivity extends Activity {

    String pathUrl = "";
    ProgressDialog progressDialog;

    private BackPressCloseHandler backPressCloseHandler;

    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        backPressCloseHandler = new BackPressCloseHandler(this);

        Intent intent = getIntent();
        pathUrl = intent.getExtras().getString("moveUrl");

        videoView = findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);

        videoView.setVideoPath(pathUrl);
        videoView.requestFocus();
        videoView.start();

        progressDialog =  ProgressDialog.show(this, "Please wait....", "Retrieving data......", true);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
        //backPressCloseHandler.onBackPressed();
        progressDialog.cancel();
        videoView.stopPlayback();
        this.finish();
    }

}
