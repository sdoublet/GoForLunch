package com.example.goforlunch.views.recyclerViews;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.goforlunch.BuildConfig;
import com.example.goforlunch.R;
import com.example.goforlunch.model.Api.Details.PlaceDetail;
import com.example.goforlunch.model.Api.Distance.DistanceMatrix;
import com.example.goforlunch.model.Api.Nearby.ResultNearbySearch;
import com.example.goforlunch.utils.DataHolder;
import com.example.goforlunch.utils.PlaceStreams;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

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
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/photo";
    private static final int MAX_WIDTH = 75;
    private static final int MAX_HEIGHT = 75;


    RestoListFragmentViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void updateView(ResultNearbySearch restaurantDetail, RequestManager glide) {


        if (restaurantDetail.getRating() != null) {
            double ratingGoogle = restaurantDetail.getRating();
            double ratingResult = (ratingGoogle / 5) * 3;
            rating.setRating((float) ratingResult);
        } else rating.setVisibility(View.INVISIBLE);

       PlaceStreams.streamFetchPlaceDetails(restaurantDetail.getPlaceId(), BuildConfig.GOOGLE_MAPS_API_KEY).subscribeWith(new DisposableObserver<PlaceDetail>() {
            @Override
            public void onNext(PlaceDetail placeDetail) {
                Log.e("placeDetail", placeDetail.getResult().getName());
                displayDetail(placeDetail);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    //Display Detail
    private void displayDetail(PlaceDetail placeDetail) {
        RequestManager glide = Glide.with(itemView);
        //---Address---
        restoAddress.setText(placeDetail.getResult().getFormattedAddress());
        //---Name---
        restoName.setText(placeDetail.getResult().getName());
        //---Photo---
        if (placeDetail.getResult().getPhotos() != null && !placeDetail.getResult().getPhotos().isEmpty()) {
            glide.load(BASE_URL+"?maxwigth="+MAX_WIDTH+"&maxheight="+MAX_HEIGHT+"&photoreference="+placeDetail.getResult().getPhotos().get(0).getPhotoReference()+"&key="+BuildConfig.GOOGLE_MAPS_API_KEY).into(restoPhoto);
            Log.e("photo", placeDetail.getResult().getPhotos().get(0).getPhotoReference());

        }else {
            restoPhoto.setImageResource(R.drawable.serveur);
        }
        //---Distance---
        String lat = String.valueOf(placeDetail.getResult().getGeometry().getLocation().getLat());
        String lng = String.valueOf(placeDetail.getResult().getGeometry().getLocation().getLng());
        String destination = lat + "," + lng;

        PlaceStreams.streamfetchDistanceMatrix(DataHolder.getInstance().getCurrentPosiiton(), destination, BuildConfig.GOOGLE_MAPS_API_KEY).subscribeWith(new DisposableObserver<DistanceMatrix>() {
            @Override
            public void onNext(DistanceMatrix distanceMatrix) {

                int distance=  distanceMatrix.getRows().get(0).getElements().get(0).getDistance().getValue();
                if (distance<1000) {
                    restoDistance.setText(distance + " m");
                }else {
                    float distanceKm = distance/1000f ;
                    double doubleDistance = distanceKm;
                    doubleDistance *=100.0;
                    doubleDistance = Math.floor(doubleDistance+0.5);
                    doubleDistance /= 100.0;
                    restoDistance.setText(doubleDistance + " km");
                }



            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


    }

}

