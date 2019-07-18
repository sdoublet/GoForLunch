package com.example.goforlunch.controler.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.goforlunch.BuildConfig;
import com.example.goforlunch.R;
import com.example.goforlunch.model.Api.Details.PlaceDetail;
import com.example.goforlunch.model.Api.Details.Result;
import com.example.goforlunch.model.Api.Firebase.LikeHelper;
import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.example.goforlunch.model.User;
import com.example.goforlunch.utils.DataHolder;
import com.example.goforlunch.utils.Divider;
import com.example.goforlunch.utils.PlaceStreams;
import com.example.goforlunch.views.recyclerViews.ClientAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.EasyPermissions;

public class PlaceDetailActivity extends BaseActivity {

    public static final String PLACEDETAILRESTO = "resto_place_id";
    //public static final String PHOTO = "photo";
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
    @BindView(R.id.star)
    Button like;
    @BindView(R.id.noWorker)
    TextView noWorker;
    private String restoPlaceId;
    private Disposable disposable;
    private Result placeDetailResult;
    private ClientAdapter adapter;
    private User user;
    private long likes;
    public static final String[] perms = {Manifest.permission.CALL_PHONE};
    public static final int REQUEST_PERMISSION_CODE = 100;
    public static final String PREF_BOOKING = "myBooking";
    public static final String PREF_LIKE = "likes";
    public static final String API_KEY = BuildConfig.GOOGLE_MAPS_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        configureStatusBar();
        restoPlaceId = getIntent().getStringExtra(PLACEDETAILRESTO);
        Log.e("test", restoPlaceId);
       // restorePrefLike();
        executeHttpRequestWithRetrofit(restoPlaceId);
        configureRecyclerView();
        displayLikes();
        restorePrefBooking();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
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
            updateRestaurantName(placeDetailResult.getName());
            updateRestaurantId(restoPlaceId);
            SharedPreferences preferences = getSharedPreferences(PREF_BOOKING, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("booked", restoPlaceId);
            editor.apply();
            Log.e("pref", preferences.getString("booked", null));

            configureRecyclerView();


        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.add_circle).mutate();
            Toast.makeText(getBaseContext(), "Make your choice", Toast.LENGTH_SHORT).show();
            floatingButton.setImageDrawable(drawable);
            floatingButton.setActivated(false);
            updateRestaurantName(null);
            updateRestaurantId(null);
            SharedPreferences sharedPreferences = getSharedPreferences(PREF_BOOKING, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Log.e("pref", "null");

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
        // TODO: 07/06/2019 get permission with easypermission
        if (EasyPermissions.hasPermissions(getApplicationContext(), perms)) {
            if (placeDetailResult.getFormattedPhoneNumber() != null) {
                String phoneNumber = placeDetailResult.getFormattedPhoneNumber();
                Log.e("phone", phoneNumber);
                Intent intentCall = new Intent(Intent.ACTION_CALL);
                intentCall.setData(Uri.parse("tel:" + "0622611305"));
                startActivity(intentCall);
            }
        } else {
            EasyPermissions.requestPermissions(this, "This app need your permission to work", REQUEST_PERMISSION_CODE, perms);
        }
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.star)
    public void addLike() {
        if (like.getText().length() < 10) {
            Log.e("lenght", String.valueOf(like.getText().length()));
            LikeHelper.getRestoLiked(placeDetailResult.getPlaceId()).addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Log.e("like", "deja présent " + placeDetailResult.getName());
                    LikeHelper.getRestoLiked(placeDetailResult.getPlaceId()).addOnSuccessListener(documentSnapshot1 -> {

                        like.setText(getString(R.string.LIKE) + "(" + documentSnapshot1.get("mLike") + ")");
                       // SharedPreferences sharedPreferences = getSharedPreferences(PREF_LIKE, 0);
                       // SharedPreferences.Editor editor = sharedPreferences.edit();
                       // editor.putString("liked", getString(R.string.LIKE) + "(" + documentSnapshot1.get("mLike") + ")");
                       // editor.apply();
                        Log.e("like", String.valueOf(likes));

                    });
                    likes = (long) documentSnapshot.get("mLike");
                    int newLike = (int) (likes + 1);
                    Log.e("like", " ca fait " + likes);
                    LikeHelper.updateLike(placeDetailResult.getPlaceId(), newLike);

                } else {
                    Log.e("like", "pas présent " + placeDetailResult.getName());
                    LikeHelper.createLike(placeDetailResult.getPlaceId(), 1);
                    like.setText(getString(R.string.LIKE) + "(1)");
                   //SharedPreferences sharedPreferences = getSharedPreferences(PREF_LIKE, 0);
                   //SharedPreferences.Editor editor = sharedPreferences.edit();
                   //editor.putString("liked", getString(R.string.LIKE) + "(1)");
                   //editor.apply();

                }
            }).addOnFailureListener(e -> {

            });


        }
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.star)
    public void dislike() {
        if (like.getText().length() > 10) {

            LikeHelper.getRestoLiked(placeDetailResult.getPlaceId()).addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    likes = (long) documentSnapshot.get("mLike");
                    int newLike = (int) (likes - 1);
                    LikeHelper.updateLike(placeDetailResult.getPlaceId(), newLike);
                    like.setText(getString(R.string.ToLike) + "(" + newLike + ")");
//                    SharedPreferences sharedPreferences = getSharedPreferences(PREF_LIKE, 0);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("liked", getString(R.string.ToLike) + "(" + newLike + ")");
//                    editor.apply();
                }
            });
        }
    }

    //-----------------------
    //HTTP REQUEST
    //-----------------------
    public void executeHttpRequestWithRetrofit(String placeId) {
        this.disposable = PlaceStreams.streamFetchPlaceDetails(placeId, API_KEY).subscribeWith(newObserver());

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


    //  Dispose subscription
    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }
    //----------------------
    //REST REQUEST
    //----------------------


    public void updateRestaurantName(String restoName) {
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {

            user = documentSnapshot.toObject(User.class);
            UserHelper.updateRestaurantName(getCurrentUser().getUid(), restoName);


        });
    }

    public void updateRestaurantId(String restoId) {
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            user = documentSnapshot.toObject(User.class);
            UserHelper.updateRestaurantId(getCurrentUser().getUid(), restoId);
            DataHolder.getInstance().setRestaurantId(restoPlaceId);

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
                String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + restoPhoto + "&key=" + API_KEY;
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
            restoName.setText(getString(R.string.NoName));
        }


    }


    //--------------------
    //Display RecyclerView
    //--------------------
    private void configureRecyclerView( ) {

        UserHelper.getRestoId(restoPlaceId).addOnSuccessListener(queryDocumentSnapshots -> {
            List<User> users = new ArrayList<>();
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    users.add(snapshot.toObject(User.class));
                    adapter = new ClientAdapter(users, Glide.with(getApplicationContext()));
                    Log.e("rvDetail", String.valueOf(users.size()) );
                    userRecyclerView.addItemDecoration(new Divider(getBaseContext(), LinearLayout.VERTICAL));
                    userRecyclerView.setAdapter(adapter);
                    userRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
                    noWorker.setVisibility(View.GONE);
                }
            }
        });

    }

    //---------------------
    //Display Likes
    //---------------------
    @SuppressLint("SetTextI18n")
    private void displayLikes() {
        LikeHelper.getRestoLiked(restoPlaceId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.get("mLike") != null ) {
                like.setText(getString(R.string.ToLike) + "(" + documentSnapshot.get("mLike") + ")");
            } else {
                like.setText(getString(R.string.ToLike) + "(0)");
            }
        });

    }

    //------------------------
    //RESTORE PREFERENCES
    //------------------------
    private void restorePrefBooking() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_BOOKING, 0);
        String booked = sharedPreferences.getString("booked", null);
        if (booked != null && booked.equals(restoPlaceId)) {
            Log.e("pref", booked + " yes" + restoPlaceId + " " + booked);
            Drawable drawable = getResources().getDrawable(R.drawable.check_circle).mutate();
            drawable.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
            floatingButton.setImageDrawable(drawable);
            floatingButton.setActivated(true);
        }
    }

    private void restorePrefLike() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_LIKE, 0);
        String liked = sharedPreferences.getString("liked", null);
        if (liked != null) {
            like.setText(liked);
            Log.e("liked", liked);
        } else {
            Log.e("liked", "nothing");
        }
    }


}
