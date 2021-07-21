package com.ttz.kmystro.catchone.Fragment;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ttz.kmystro.catchone.LoginActivity;
import com.ttz.kmystro.catchone.R;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }


    private EditText namechange;
    private Button updatebtn;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        namechange = view.findViewById(R.id.Usertextname);

        updatebtn= view.findViewById(R.id.updateinfo);
        updatebtn.setOnClickListener(view1 -> updateThedata());
        Button logoutbtn = view.findViewById(R.id.buttonlogout);
        logoutbtn.setOnClickListener(view12 -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Objects.requireNonNull(getActivity()).finish();
                }
        );

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){

                String dataname = documentSnapshot.getString("UserName");
                namechange.setText(dataname);

            }
        }).addOnFailureListener(e -> {

        });

        Button sharebtn = view.findViewById(R.id.buttonsharebtn);
        sharebtn.setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.ttz.kmystro.catchone");
            sendIntent.putExtra(Intent.EXTRA_TITLE, "CatchOne app");
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(sendIntent, null));
        });

        return  view;
    }



    private void updateThedata() {

        String Username = namechange.getText().toString();
        final String userid = firebaseAuth.getUid();
        DocumentReference nameDoc = null;
        if (userid != null) {
            nameDoc = firebaseFirestore.collection("Users").document(userid);
        }
        if(!TextUtils.isEmpty(Username)){

            if(!TextUtils.isEmpty(Username)){
                Objects.requireNonNull(nameDoc).update("UserName",Username).addOnSuccessListener(aVoid -> {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),"Done",Toast.LENGTH_LONG).show();
                });
            }
        }else {

            namechange.setError(" check Text if its not empty");
            namechange.requestFocus();
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}
