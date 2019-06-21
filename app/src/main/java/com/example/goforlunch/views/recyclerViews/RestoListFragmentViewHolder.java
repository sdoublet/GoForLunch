package com.example.goforlunch.views.recyclerViews;

import android.text.Html;
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

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
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
            glide.load(BASE_URL + "?maxwigth=" + MAX_WIDTH + "&maxheight=" + MAX_HEIGHT + "&photoreference=" + placeDetail.getResult().getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.GOOGLE_MAPS_API_KEY).into(restoPhoto);
            Log.e("photo", placeDetail.getResult().getPhotos().get(0).getPhotoReference());

        } else {
            restoPhoto.setImageResource(R.drawable.serveur);
        }
        //---Distance---
        String lat = String.valueOf(placeDetail.getResult().getGeometry().getLocation().getLat());
        String lng = String.valueOf(placeDetail.getResult().getGeometry().getLocation().getLng());
        String destination = lat + "," + lng;

        PlaceStreams.streamfetchDistanceMatrix(DataHolder.getInstance().getCurrentPosiiton(), destination, BuildConfig.GOOGLE_MAPS_API_KEY).subscribeWith(new DisposableObserver<DistanceMatrix>() {
            @Override
            public void onNext(DistanceMatrix distanceMatrix) {
                distance(distanceMatrix);


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        //OpeningHours
        int[] daysPeriods = {0, 1, 2, 3, 4, 5, 6};
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        if (min < 10) {
            min = '0' + min;
        }
        String currentTime = hours + "" + min;
        int currentHour = Integer.parseInt(currentTime);
        Log.e("date", day + " " + hours + ":" + min);
        Log.e("time", currentTime);


        if (placeDetail.getResult().getOpeningHours() != null) {
            for (int i = 0; i < placeDetail.getResult().getOpeningHours().getPeriods().size(); i++) {
                if (placeDetail.getResult().getOpeningHours().getPeriods().get(i).getOpen().getDay() == daysPeriods[day]) {
                    String hour = placeDetail.getResult().getOpeningHours().getPeriods().get(i).getClose().getTime();
                    String open = placeDetail.getResult().getOpeningHours().getPeriods().get(i).getOpen().getTime();
                    int openHour = Integer.parseInt(open);
                    String restau = placeDetail.getResult().getName();// just for log
                    Log.e("hour", restau + " " + open + " " + hour);
                    if (openHour > currentHour && openHour < 2400) {
                        openHour = Integer.parseInt(open);
                        Log.e("open", restau + " " + open);
                        Log.e("openining", restau + openHour);
                        restoOpening.setText(Html.fromHtml("<font color=\"#ff0000\">"+"Close"+"</font>"+", opening at " + convertDate(String.valueOf(openHour), Locale.getDefault().getLanguage())));
                    } else if (placeDetail.getResult().getOpeningHours().getOpenNow()) {
                        restoOpening.setText(Html.fromHtml("<b><font color=\"#008000\">"+"Open"+"</font></b>"+", close at " + convertDate(hour, Locale.getDefault().getLanguage())));
                    }

                }
            }

        } else restoOpening.setText("no information");
    }


    //Calculation of distance
    public void distance(DistanceMatrix distanceMatrix) {
        int distance = distanceMatrix.getRows().get(0).getElements().get(0).getDistance().getValue();
        if (distance < 1000) {
            restoDistance.setText(distance + " m");
        } else {
            double doubleDistance = distance / 1000f;
            doubleDistance *= 100.0;
            doubleDistance = Math.floor(doubleDistance + 0.5);
            doubleDistance /= 100.0;
            restoDistance.setText(doubleDistance + " km");
        }
    }


    //Convert Date by language
    public String convertDate(String date, String language) {
        int hour = Integer.parseInt(date.substring(0, 2));
        String mn = date.substring(2);
        if (language.equals("English")) {
            if (hour > 12) {
                return (hour - 12) + "." + mn + "pm";
            } else if (hour == 12) {
                return "12" + "." + mn + "pm";
            } else if (hour == 0) {
                return "12" + "." + mn + "am";
            } else {
                return hour + "." + mn + "am";
            }
        } else
            return hour + "h" + mn;
    }
}

