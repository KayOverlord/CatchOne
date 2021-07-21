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

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ttz.kmystro.catchone.CommentsActivity;
import com.ttz.kmystro.catchone.Model.StoryModel;
import com.ttz.kmystro.catchone.R;

import java.util.Objects;

public class storyAdapter extends FirestoreRecyclerAdapter<StoryModel, storyAdapter.RecHolder> {

    public storyAdapter(@NonNull FirestoreRecyclerOptions<StoryModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecHolder recHolder, final int i, @NonNull StoryModel storyModel) {
        recHolder.story.setText(storyModel.getArticle());
        recHolder.title.setText(storyModel.getTitle());
        recHolder.comcount.setText(String.valueOf(storyModel.getCom()));

        if(storyModel.getPic() != null &&!storyModel.getPic().equals("0")){
            recHolder.imgpost.setVisibility(View.VISIBLE);
            Glide.with(recHolder.itemView.getContext()).load(storyModel.getPic()).into(recHolder.imgpost);
        }



        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        if(storyModel.getID().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())){
            recHolder.binbutton.setVisibility(View.VISIBLE);
            recHolder.binbutton.setOnClickListener(v -> {

                DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(i);
                String id = documentSnapshot.getId();

                firebaseFirestore.collection("Articles").document(id).delete();
            });
        }


        firebaseFirestore.collection("Users").document(storyModel.getID()).addSnapshotListener((documentSnapshot, e) -> {

            if (documentSnapshot != null && documentSnapshot.exists()) {
                recHolder.textname.setText(documentSnapshot.getString("UserName"));
              if(documentSnapshot.getString("Status") !=null && Objects.equals(documentSnapshot.getString("Status"), "business")){
                    recHolder.certification.setVisibility(View.VISIBLE);
              }
            }
        });

    }

    @NonNull
    @Override
    public RecHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_layout,parent,false);
        return new RecHolder(view);
    }

    class RecHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView story;
        TextView comcount;
        ImageButton commbutton;
        TextView textname;
        ImageView imgpost;
        ImageView certification;
        ImageButton binbutton;
        RecHolder(@NonNull final View itemView) {
            super(itemView);

            binbutton = itemView.findViewById(R.id.imageButtonbin);

           title = itemView.findViewById(R.id.textViewtitle);
            story = itemView.findViewById(R.id.textViewarticle);
            comcount = itemView.findViewById(R.id.textViewcommCount);
            commbutton = itemView.findViewById(R.id.imagebtncomments);
            textname = itemView.findViewById(R.id.textViewpostname);
            imgpost = itemView.findViewById(R.id.imageViewposter);
            certification = itemView.findViewById(R.id.imageViewcertification);


            commbutton.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION ){
                    DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
                    String id = documentSnapshot.getId();

                    Intent intent = new Intent(itemView.getContext(), CommentsActivity.class);
                    intent.putExtra("Comdata",id);
                    itemView.getContext().startActivity(intent);
                }
            });



        }



    }

}
