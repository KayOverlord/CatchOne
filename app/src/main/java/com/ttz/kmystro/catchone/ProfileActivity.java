package com.ttz.kmystro.catchone;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class ProfileActivity extends Activity {

    private Button subButton;
    private EditText username;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageRef;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        username = findViewById(R.id.usernametext);



        subButton = findViewById(R.id.submitbutton);

        subButton.setOnClickListener(view -> {
         final String getusername = username.getText().toString();
            if(!TextUtils.isEmpty(getusername) ){

                    String userid = firebaseAuth.getCurrentUser().getUid();
                    String userEm = firebaseAuth.getCurrentUser().getEmail();

                HashMap<String,Object>userdata = new HashMap<>();
                userdata.put("UserName",getusername);
                userdata.put("Email",userEm);

                firebaseFirestore.collection("Users").document(userid).set(userdata).addOnCompleteListener(task1 -> {
                    Toast.makeText(getApplicationContext(),"DONE",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(),WelcomeActivity.class);
                    startActivity(intent);
                }).addOnFailureListener(e -> Log.d("ERROR",e.getMessage()));


            }else{
                username.setError(" check Text if its not empty or too short");
                username.requestFocus();
            }
        });
    }

    }

