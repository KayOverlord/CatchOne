package com.ttz.kmystro.catchone;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ttz.kmystro.catchone.Fragment.ChatFragment;
import com.ttz.kmystro.catchone.Fragment.MapFragment;
import com.ttz.kmystro.catchone.Fragment.ProfileFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.action_map:
                        switchToMapFragment();
                        break;
                    case R.id.action_forum:
                        switchToChatFragment();
                        break;
                    case R.id.action_profile:
                        switchToProfileFragment();
                        break;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (savedInstanceState == null) {
            navView.setSelectedItemId(R.id.action_map);// change to whichever id should be default
        }

        FirebaseMessaging.getInstance().subscribeToTopic("pushnotif");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userID = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Users").document(userID).addSnapshotListener((documentSnapshot, e) -> {

            if(documentSnapshot != null&&documentSnapshot.get("Status")!=null){
                ((GlobalActivity) this.getApplication()).setStatus("business");
            }else {
                ((GlobalActivity) this.getApplication()).setStatus("normal");
            }
        });

    }
    public void switchToChatFragment() {

        ChatFragment mainF = new ChatFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, mainF, "fragmant");
        ft.commit();
    }

    public void switchToProfileFragment() {
        ProfileFragment profileragment = new ProfileFragment();
        FragmentTransaction Profileft = getSupportFragmentManager().beginTransaction();
        Profileft.replace(R.id.fragment_container, profileragment, "fragmant");
        Profileft.commit();
    }

    public void switchToMapFragment() {
        MapFragment mapsFragment = new MapFragment();
        FragmentTransaction Bookingft = getSupportFragmentManager().beginTransaction();
        Bookingft.replace(R.id.fragment_container, mapsFragment, "fragmant");
        Bookingft.commit();
    }
}
