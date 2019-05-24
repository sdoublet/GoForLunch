package com.example.goforlunch.views.recyclerViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.goforlunch.R;
import com.example.goforlunch.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

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
        workmatesViewHolder.textViewWorkmates.setText(String.format(context.getString(R.string.hasnt_decided_yet), user.getUsername()));
        if (user.getUrlPicture()!=null && !user.getUrlPicture().isEmpty()){
            String urlPhoto = user.getUrlPicture();
            glide.load(urlPhoto)
                    .apply(RequestOptions.circleCropTransform())
                    .into(workmatesViewHolder.photoWorkmates);
        }else {

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
