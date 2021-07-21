package com.ttz.kmystro.catchone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;

public class UserGuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);
        // Make sure to use the correct VideoView import
        Button btn = findViewById(R.id.buttonfinished);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        });
        VideoView videoView = findViewById(R.id.videoView);
        String path = "android.resource://" + getPackageName() + "/"+R.raw.video_file ;
        videoView.setVideoURI(Uri.parse(path));
        videoView.requestFocus();
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
                }
        );
    }
}
