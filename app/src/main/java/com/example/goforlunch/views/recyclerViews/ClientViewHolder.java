package com.example.goforlunch.views.recyclerViews;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.goforlunch.R;
import com.example.goforlunch.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClientViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.photo_workmates)
    ImageView photoWorkmates;
    @BindView(R.id.text_view_workmates)
    TextView textView;


    public ClientViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void updateView(User user, RequestManager glide) {
        if (user.getUrlPicture()!=null){
            glide.load(user.getUrlPicture()).circleCrop().into(photoWorkmates);
        }

        if (user != null) {
            textView.setText(user.getUsername() + " is joining!");
            Log.e("holder", user.getUsername());
        }
    }
}
