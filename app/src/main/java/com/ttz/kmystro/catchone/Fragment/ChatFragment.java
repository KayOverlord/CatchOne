package com.ttz.kmystro.catchone.Fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ttz.kmystro.catchone.Adapter.storyAdapter;
import com.ttz.kmystro.catchone.GlobalActivity;
import com.ttz.kmystro.catchone.Model.StoryModel;
import com.ttz.kmystro.catchone.R;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    private FirebaseStorage firebaseStorage;
    private String downloaduri;
    private TextView textpercent;
    private ProgressBar progressBar;
    private UploadTask uploadTask;
    private Button upload;

    public ChatFragment() {
        // Required empty public constructor
    }

    private EditText mtitle;
    private EditText mstory;
    private Button mcancel;
    private Button msend;
    private static final int RESULT_LOAD_IMAGE = 11;
    private String datename;
    private ImageView uploadimg;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        firebaseStorage = FirebaseStorage.getInstance();
        RecyclerView recyclerView = view.findViewById(R.id.recview);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);

        final String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("Articles");

        Query query = collectionReference.orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<StoryModel> options = new FirestoreRecyclerOptions.Builder<StoryModel>()
                .setQuery(query, StoryModel.class)
                .build();


        storyAdapter adapter = new storyAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

        Button postbtn = view.findViewById(R.id.buttonpost);
        postbtn.setOnClickListener(v -> Opendialogbox());

        return view;
    }

    private void Opendialogbox() {

        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext())){
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                dismiss();
            }
        };
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.chatdialoglayout);
        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();



        mcancel = dialog.findViewById(R.id.buttoncancel);
        msend = dialog.findViewById(R.id.buttonsend);
        mtitle = dialog.findViewById(R.id.editTextTitle);
        mstory = dialog.findViewById(R.id.editTextStory);
        uploadimg = dialog.findViewById(R.id.imageViewuploadedimg);

        upload = dialog.findViewById(R.id.buttonuploadimg);
        String status = ((GlobalActivity) Objects.requireNonNull(getActivity()).getApplication()).getStatus();
        if(status.equals("business")){
            upload.setVisibility(View.VISIBLE);
        }else {
            upload.setVisibility(View.INVISIBLE);
        }
        progressBar = dialog.findViewById(R.id.progressBar4);
        textpercent = dialog.findViewById(R.id.Textprogress);
        upload.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
            upload.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            textpercent.setVisibility(View.VISIBLE);
            datename = DateTime.now().toString();
            mcancel.setVisibility(View.INVISIBLE);
            msend.setVisibility(View.INVISIBLE);


        });

        mcancel.setOnClickListener(v -> {
            if( uploadTask !=null &&uploadTask.isInProgress()) {
                uploadTask.cancel();
            }
            dialog.dismiss();
                }
        );

        msend.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(dialog.getContext());
            builder1.setMessage("Are you sure you want to Post this?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    (dialogA, id) -> {
                        String mT = mtitle.getText().toString().trim();
                        final String mS = mstory.getText().toString().trim();

                        if(!mT.isEmpty()&& !mS.isEmpty() && mS.length()>10){
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            FirebaseAuth auth = FirebaseAuth.getInstance();

                            Map<String,Object> story = new HashMap<>();
                            story.put("Article",mS);
                            story.put("Title",mT);
                            story.put("ID", Objects.requireNonNull(auth.getCurrentUser()).getUid());
                            story.put("com",0);
                            story.put("timestamp", FieldValue.serverTimestamp());
                            if(downloaduri != null) {
                                story.put("pic",downloaduri);
                            }else {
                                story.put("pic", "0");
                            }

                            db.collection("Articles").document().set(story);
                            dialogA.cancel();
                            dialog.dismiss();
                        }else {
                            mtitle.setError("make sure the title is not empty");
                            mstory.setError("make sure the post is not empty and it's longer then 10 characters");
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    (dialogA, id) -> dialogA.cancel());

            AlertDialog alert11 = builder1.create();
            alert11.show();

        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK && data != null){
            Uri imgurl = data.getData();
            uploadDATA(datename, imgurl);
            Glide.with(Objects.requireNonNull(getActivity()).getApplication()).load(imgurl).into(uploadimg);
            uploadimg.setVisibility(View.VISIBLE);
        }else {
            msend.setVisibility(View.VISIBLE);
            mcancel.setVisibility(View.VISIBLE);
            upload.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
    @SuppressLint("SetTextI18n")
    private void uploadDATA(String datetime, Uri imgurl) {
        final StorageReference DATAeRef = firebaseStorage.getReference();
        final StorageReference profilepicRef = DATAeRef.child("promoImage/"+datetime+".jpg");

        uploadTask = profilepicRef.putFile(imgurl);

        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100* taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            textpercent.setText("please wait..."+""+progress +"%");
            progressBar.setProgress((int) progress);
            if(progressBar.getProgress() == 100){
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            // Continue with the task to get the download URL
            return profilepicRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {

            if(task.isSuccessful()){

                downloaduri = Objects.requireNonNull(task.getResult()).toString();
                textpercent.setText("click send");
                mcancel.setVisibility(View.VISIBLE);
                msend.setVisibility(View.VISIBLE);
            }

        });
    }

}
