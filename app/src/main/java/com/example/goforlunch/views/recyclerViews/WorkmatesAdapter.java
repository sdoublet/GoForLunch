package com.example.goforlunch.views.recyclerViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.goforlunch.R;
import com.example.goforlunch.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesViewHolder> {

    private Context context;
    private RequestManager glide;


    public WorkmatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context, RequestManager glide) {
        super(options);
        this.context = context;
        this.glide = glide;
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmatesViewHolder workmatesViewHolder, int i, @NonNull User user) {


        if (user.getmRestaurantName() != null) {
            workmatesViewHolder.textViewWorkmates.setText(user.getUsername()+"\n" + context.getString(R.string.eating)+"\n" +"("+ user.getmRestaurantName()+")");
        } else {
            workmatesViewHolder.textViewWorkmates.setText(user.getUsername()+ " " + context.getString(R.string.hasnt_decided_yet));

        }


        if (user.getUrlPicture() != null && !user.getUrlPicture().isEmpty()) {
            String urlPhoto = user.getUrlPicture();
            glide.load(urlPhoto)
                    .apply(RequestOptions.circleCropTransform())
                    .into(workmatesViewHolder.photoWorkmates);
        } else {

            glide.load(R.drawable.serveur).apply(RequestOptions.circleCropTransform()).into(workmatesViewHolder.photoWorkmates);
        }
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_workmates, parent, false);
        return new WorkmatesViewHolder(view);
    }

}
