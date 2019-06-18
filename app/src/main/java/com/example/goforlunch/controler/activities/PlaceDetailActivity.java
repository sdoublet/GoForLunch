package com.example.goforlunch.controler.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.goforlunch.BuildConfig;
import com.example.goforlunch.R;
import com.example.goforlunch.model.Api.Details.PlaceDetail;
import com.example.goforlunch.model.Api.Details.Result;
import com.example.goforlunch.model.Api.Firebase.RestaurantHelper;
import com.example.goforlunch.model.Booking;
import com.example.goforlunch.utils.DataHolder;
import com.example.goforlunch.utils.PlaceStreams;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class PlaceDetailActivity extends BaseActivity {

    public static final String PLACEDETAILRESTO = "resto_place_id";
    public static final String PHOTO = "photo";
    // @BindView(R.id.toolbar)
    // Toolbar toolbar;
    @BindView(R.id.floating_button)
    FloatingActionButton floatingButton;
    @BindView(R.id.detail_resto_name)
    TextView restoName;
    @BindView(R.id.detail_address)
    TextView restoAddress;
    @BindView(R.id.detail_image_resto)
    ImageView photoResto;
    @BindView(R.id.rating)
    RatingBar restoRating;
    @BindView(R.id.detail_recycler_view)
    RecyclerView userRecyclerView;
    @BindView(R.id.website_button)
    Button websiteButton;
    @BindView(R.id.progress_bar)
    ProgressBar progressbar;
    private String restoPlaceId;
    private Disposable disposable;
    private Result placeDetailResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_place_detail);
        ButterKnife.bind(this);
        configureStatusBar();
        restoPlaceId = getIntent().getStringExtra(PLACEDETAILRESTO);
        Log.e("test", restoPlaceId);
        executeHttpRequestWithRetrofit(restoPlaceId);

        // photo = getIntent().getStringExtra("photo");


    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_place_detail;
    }

    @OnClick(R.id.floating_button)
    public void OnclickFloatingActionButton() {
        if (!floatingButton.isActivated()) {
            Drawable drawable = getResources().getDrawable(R.drawable.check_circle).mutate();
            drawable.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(getBaseContext(), "You choose this restaurant", Toast.LENGTH_SHORT).show();
            floatingButton.setImageDrawable(drawable);
            floatingButton.setActivated(true);
            bookingRestaurant(getCurrentUser().getUid());
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.add_circle).mutate();
            Toast.makeText(getBaseContext(), "Make your choice", Toast.LENGTH_SHORT).show();
            floatingButton.setImageDrawable(drawable);
            floatingButton.setActivated(false);
            deleteBooking(getCurrentUser().getUid());
        }
    }


    //------------------
    //ACTION
    //------------------

    @OnClick(R.id.website_button)
    public void launchWebViewActivity() {
        Intent intent = new Intent(this, WebViewRestaurant.class);
        if (placeDetailResult.getWebsite() != null) {
            intent.putExtra("website", placeDetailResult.getWebsite());
            startActivity(intent);
        } else {
            Toast.makeText(this, "No website for this restaurant", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.image_phone)
    public void callRestaurant() {
        // TODO: 07/06/2019 get permission
        String phoneNumber = placeDetailResult.getFormattedPhoneNumber();
        Log.e("phone", phoneNumber);
//        Intent intentCall = new Intent(Intent.ACTION_CALL);
//        intentCall.setData(Uri.parse("tel:"+ phoneNumber));
//        startActivity(intentCall);
    }

    //-----------------------
    //HTTP REQUEST
    //-----------------------
    public void executeHttpRequestWithRetrofit(String placeId) {
        this.disposable = PlaceStreams.streamFetchPlaceDetails(placeId, BuildConfig.GOOGLE_MAPS_API_KEY).subscribeWith(newObserver());

    }

    private <T> DisposableObserver<T> newObserver() {
        return new DisposableObserver<T>() {
            @Override
            public void onNext(T t) {
                if (t instanceof PlaceDetail) {
                    placeDetailResult = ((PlaceDetail) t).getResult();
                    updateUI((PlaceDetail) t);
                } else
                    Log.e("TAG", "disposableObserver onNext" + t.getClass());
            }

            @Override
            public void onError(Throwable e) {
                onFailureListener();
            }

            @Override
            public void onComplete() {

            }
        };
    }

    //----------------------
    //REST REQUEST
    //----------------------
    private void bookingRestaurant(String userId) {
        RestaurantHelper.getBooking(userId, restoPlaceId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Booking booking = documentSnapshot.toObject(Booking.class);
                } else {
                    RestaurantHelper.createBookingRestaurant(userId,restoPlaceId, placeDetailResult.getName(), null);
                }
            }
        });

    }

    private void deleteBooking(String userId) {
        RestaurantHelper.deleteBooking(userId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

    //------------------
    //UPDATE UI
    //------------------
    private void updateUI(PlaceDetail results) {
        if (results.getResult() != null) {

            //-------------------------
            //Display restaurant info
            //-------------------------
            restoName.setText(results.getResult().getName());
            restoAddress.setText(results.getResult().getFormattedAddress());
            //-------------------------
            //Display restaurant photo
            //-------------------------
            if (results.getResult().getPhotos() != null) {
                String restoPhoto = results.getResult().getPhotos().get(0).getPhotoReference();
                String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + restoPhoto + "&key=" + BuildConfig.GOOGLE_MAPS_API_KEY;
                Glide.with(getBaseContext()).load(photoUrl).into(photoResto);
            } else {
                photoResto.setImageResource(R.drawable.serveur);
            }
            //--------------------------
            //Display restaurant rating
            //--------------------------
            if (results.getResult().getRating() != null) {
                double rating = results.getResult().getRating();
                double ratingResult = (rating / 5) * 3;
                restoRating.setRating((float) ratingResult);
                Log.e("rating", String.valueOf(rating));
                Log.e("rating", String.valueOf(ratingResult));
            } else restoRating.setVisibility(View.GONE);


            //--------------------------
            //Hide website button
            //--------------------------
            if (results.getResult().getWebsite() != null) {
                websiteButton.setVisibility(View.VISIBLE);
            } else {
                websiteButton.setVisibility(View.GONE);
            }

            //-------------------------
            //Configure Progressbar
            //-------------------------
            if (!photoResto.isShown()) {
                progressbar.setVisibility(View.VISIBLE);
            } else {
                progressbar.setVisibility(View.INVISIBLE);
            }



        } else {
            restoName.setText("No name");
        }

    }


    //--------------------
    //Display RecyclerView
    //--------------------


}
