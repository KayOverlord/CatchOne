package com.ttz.kmystro.catchone.Adapter;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ttz.kmystro.catchone.Model.CommModel;
import com.ttz.kmystro.catchone.R;
import java.util.Objects;


public class comAdapter extends FirestoreRecyclerAdapter<CommModel,comAdapter.RecHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public comAdapter(@NonNull FirestoreRecyclerOptions<CommModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final RecHolder holder, final int position, @NonNull final CommModel model) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Users").document(model.getAuthor()).addSnapshotListener((documentSnapshot, e) -> {
            assert documentSnapshot != null;
            if(documentSnapshot.exists()){
                holder.txtname.setText(documentSnapshot.getString("UserName"));
                String userstatus = documentSnapshot.getString("Status");
                if(userstatus != null && !userstatus.equals("0")){
                   holder.certific.setVisibility(View.VISIBLE);

                }
            }
        });


        holder.comment.setText(model.getComment());
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String wedidit = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        if (String.valueOf(model.getAuthor()).equals(wedidit)){
            holder.deletebtn.setVisibility(View.VISIBLE);
            holder.deletebtn.setOnClickListener(view -> {

                if (position != RecyclerView.NO_POSITION) {
                    DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
                    final String id = documentSnapshot.getId();
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("Comments").document(model.getId()).collection(model.getId()).document(id).delete();

                    final DocumentReference sfDocRef = db.collection("Articles").document(model.getId());
                    db.runTransaction(transaction -> {

                        DocumentSnapshot snapshot = transaction.get(sfDocRef);
                        double newPopulation = snapshot.getDouble("com") - 1;
                        transaction.update(sfDocRef, "com", newPopulation);
                        return newPopulation;

                    });

                }

            });

        }
    }

    @NonNull
    @Override
    public RecHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_layout,viewGroup,false);
        return new RecHolder(view);
    }

    static class RecHolder extends RecyclerView.ViewHolder{

        TextView comment;
        ImageButton deletebtn;
        TextView txtname;
        ImageView certific;

        RecHolder(@NonNull final View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.textViewcomments);
            deletebtn = itemView.findViewById(R.id.deleteimageButton);
            txtname = itemView.findViewById(R.id.textViewname);
            certific = itemView.findViewById(R.id.imageViewcommcertifi);
        }
    }
}
