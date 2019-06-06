package com.example.goforlunch.views.recyclerViews;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.goforlunch.R;
import com.example.goforlunch.model.Api.Details.Result;

import butterknife.BindView;

public class RestoListFragmentViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.resto_name)
    TextView restoName;
    @BindView(R.id.resto_address)
    TextView restoAddress;
    @BindView(R.id.resto_opening_time)
    TextView restoOpening;
    @BindView(R.id.resto_distance)
    TextView restoDistance;
    @BindView(R.id.number_of_person)
    TextView numberOfPerson;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.resto_photo)
    ImageView restoPhoto;

    public RestoListFragmentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    private void updateView(Result restaurantDetail, RequestManager glide) {

        //Name
        //-------------
        restoName.setText(restaurantDetail.getName());
        //Address
        //-------------
        restoAddress.setText(restaurantDetail.getFormattedAddress());
        //Rating
        //------------
        double ratingGoogle = restaurantDetail.getRating();
        double ratingResult = (ratingGoogle/5)*3;
        rating.setRating((float) ratingResult);

    }
}
