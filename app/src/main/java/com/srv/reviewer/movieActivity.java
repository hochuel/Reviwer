package com.srv.reviewer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class movieActivity extends Activity {

    String pathUrl = "";
    CustomDialog progressDialog;

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
        final MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);

        videoView.setVideoPath(pathUrl);
        videoView.requestFocus();
        //videoView.start();

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                Toast.makeText(getApplicationContext(), "재생 할 수 없는 동영상 입니다.", Toast.LENGTH_LONG).show();
                //System.out.println("MovieError :: not play......");
                onBackPressed();
                return false;
            }
        });

        progressDialog = new CustomDialog(this);
        progressDialog.show();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //backPressCloseHandler.onBackPressed();
        progressDialog.dismiss();
        this.finish();
    }

}
