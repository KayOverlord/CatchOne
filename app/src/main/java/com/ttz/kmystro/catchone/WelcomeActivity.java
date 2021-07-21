package com.ttz.kmystro.catchone;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

/**
 * Created by Akshay Raj on 7/28/2016.
 * Snow Corporation Inc.
 * www.snowcorp.org
 */
public class WelcomeActivity extends AppCompatActivity {


    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        session = new Session(this);
        if (!session.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        Button btnNext = findViewById(R.id.buttonNEXT);


        btnNext.setOnClickListener(v -> {
                    Intent intent = new Intent(this,UserGuideActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
        );
    }



    private void launchHomeScreen() {
        session.setFirstTimeLaunch(false);
        Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



}