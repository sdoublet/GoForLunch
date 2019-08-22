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
import com.example.goforlunch.model.Api.Details.OpeningHours;
import com.example.goforlunch.model.Api.Details.Period;
import com.example.goforlunch.model.Api.Details.PlaceDetail;
import com.example.goforlunch.model.Api.Distance.DistanceMatrix;
import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.example.goforlunch.model.Api.Nearby.ResultNearbySearch;
import com.example.goforlunch.utils.ConvertDate;
import com.example.goforlunch.utils.DataHolder;
import com.example.goforlunch.utils.PlaceStreams;

import java.util.Calendar;
import java.util.Locale;

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
    private Disposable disposable;


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

        disposable = PlaceStreams.streamFetchPlaceDetails(restaurantDetail.getPlaceId(), BuildConfig.google_maps_api_key).subscribeWith(new DisposableObserver<PlaceDetail>() {
            @Override
            public void onNext(PlaceDetail placeDetail) {
                Log.e("placeDetail", placeDetail.getResult().getName());
                displayDetail(placeDetail);
                displayWormates(restaurantDetail.getPlaceId());
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
            glide.load(BASE_URL + "?maxwigth=" + MAX_WIDTH + "&maxheight=" + MAX_HEIGHT + "&photoreference=" + placeDetail.getResult().getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.google_maps_api_key).into(restoPhoto);
            Log.e("photo", placeDetail.getResult().getPhotos().get(0).getPhotoReference());

        } else {
            restoPhoto.setImageResource(R.drawable.serveur);
        }
        //---Distance---
        String lat = String.valueOf(placeDetail.getResult().getGeometry().getLocation().getLat());
        String lng = String.valueOf(placeDetail.getResult().getGeometry().getLocation().getLng());
        String destination = lat + "," + lng;

       disposable = PlaceStreams.streamfetchDistanceMatrix(DataHolder.getInstance().getCurrentPosiiton(), destination, BuildConfig.google_maps_api_key).subscribeWith(new DisposableObserver<DistanceMatrix>() {
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
            String restau = placeDetail.getResult().getName();// just for log
            OpeningHours openingHours = placeDetail.getResult().getOpeningHours();
            for (int i = 0; i < openingHours.getPeriods().size(); i++) {
                // Log.e("opening", restau + " " + placeDetail.getResult().getOpeningHours().getPeriods().get(i).getOpen().getDay() + " " +
                //         placeDetail.getResult().getOpeningHours().getPeriods().get(i).getOpen().getTime() + " " +
                //         placeDetail.getResult().getOpeningHours().getPeriods().get(i).getClose().getTime() + " " +
                //         placeDetail.getResult().getOpeningHours().getOpenNow());
                Period period = openingHours.getPeriods().get(i);
                if (period.getOpen() != null && period.getClose() != null) {
                    String closeHours = period.getClose().getTime();
                    String openHours = period.getOpen().getTime();

                    int openHour = Integer.parseInt(openHours);
                    int closeHour = Integer.parseInt(closeHours);

                    if (period.getOpen().getDay() == day) {
                        if (!openingHours.getOpenNow() && currentHour < openHour) {
                            Log.e("close", restau + " hour<openhour");
                            restoOpening.setText(Html.fromHtml("<font color=\"#ff0000\">" + "Close" + "</font>" + ", opening at " + ConvertDate.convertDate(String.valueOf(openHour), Locale.getDefault().getLanguage())));
                        } else if (!openingHours.getOpenNow() && currentHour > openHour && currentHour < closeHour) {
                            Log.e("close", restau + " hour>open<close");
                            restoOpening.setText(Html.fromHtml("<font color=\"#ff0000\">" + "Close" + "</font>" + ", opening at " + ConvertDate.convertDate(String.valueOf(openHour), Locale.getDefault().getLanguage())));
                        } else if (currentHour > closeHour) {
                            Log.e("close", restau + " ever close");
                            restoOpening.setText(Html.fromHtml("<font color=\"#ff0000\">" + "Close" + "</font>"));
                        } else if (openingHours.getOpenNow()) {
                            restoOpening.setText(Html.fromHtml("<b><font color=\"#008000\">" + "Open" + "</font></b>" + ", close at " + ConvertDate.convertDate(String.valueOf(closeHour), Locale.getDefault().getLanguage())));
                            Log.e("open", restau);
                        }

                    } else if (openingHours.getOpenNow() && currentHour < openHour) {
                        Log.e("new", restau);
                        try {
                            restoOpening.setText(Html.fromHtml("<b><font color=\"#008000\">" + "Open" + "</font></b>" + ", close at " + ConvertDate.convertDate(placeDetail.getResult().getOpeningHours().getPeriods().get(i - 1).getClose().getTime(), Locale.getDefault().getLanguage())));
                        } catch (ArrayIndexOutOfBoundsException e) {
                            //
                        }
                    } else Log.e("newclose", restau);

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


    private void displayWormates(String restoId) {
        UserHelper.getRestoId(restoId).addOnSuccessListener(queryDocumentSnapshots -> {
            String workmates = String.valueOf(queryDocumentSnapshots.size());
            numberOfPerson.setText("(" + workmates + ")");
        });
    }



}

