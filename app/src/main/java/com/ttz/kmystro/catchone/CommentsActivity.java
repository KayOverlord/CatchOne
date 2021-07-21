package com.ttz.kmystro.catchone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ttz.kmystro.catchone.Adapter.comAdapter;
import com.ttz.kmystro.catchone.Model.CommModel;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class CommentsActivity extends AppCompatActivity {
    private EditText comment;
    private String TAG = "ERROR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_comments);
        Intent intent = getIntent();
        final String id = intent.getStringExtra("Comdata");

        //((App)this.getApplication()).setPostId(id);


        RecyclerView recyclerView = findViewById(R.id.recyclerViewCom);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        Query query = firebaseFirestore.collection("Comments").document(Objects.requireNonNull(id)).collection(id);

        FirestoreRecyclerOptions<CommModel> options = new FirestoreRecyclerOptions.Builder<CommModel>()
                .setQuery(query, CommModel.class)
                .build();

        comAdapter adapter = new comAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        comment = findViewById(R.id.editTextComment);
        ImageButton sendBtn = findViewById(R.id.sendCommentButton);

        sendBtn.setOnClickListener(view -> {

           final String mycomm = comment.getText().toString();
           if(!mycomm.isEmpty()) {
               final FirebaseFirestore db = FirebaseFirestore.getInstance();
               final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


               final Map<String, Object> user = new HashMap<>();
               user.put("Author", Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
               user.put("Comment",mycomm);
               user.put("Id",id);


               db.collection("Comments").document(id).collection(id).document().set(user).addOnSuccessListener(aVoid -> {
                   comment.getText().clear();
                   Map<String, Object> comm = new HashMap<>();
                   comm.put("Id",id);


                   final DocumentReference sfDocRef = db.collection("Articles").document(id);
                   db.runTransaction(transaction -> {

                       DocumentSnapshot snapshot = transaction.get(sfDocRef);
                       double newPopulation = snapshot.getDouble("com") + 1;

                       transaction.update(sfDocRef, "com", newPopulation);
                       return newPopulation;

                   });
               }).addOnFailureListener(e -> Log.d(TAG, "DocumentSnapshot added with ID: "));
           }else {
               comment.setError("Enter your comment first");
               comment.requestFocus();
           }
        });

    }

}
