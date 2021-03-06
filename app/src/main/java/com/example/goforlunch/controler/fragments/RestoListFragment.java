package com.example.goforlunch.controler.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.goforlunch.BuildConfig;
import com.example.goforlunch.R;
import com.example.goforlunch.controler.activities.PlaceDetailActivity;
import com.example.goforlunch.model.Api.Nearby.NearbyPlaces;
import com.example.goforlunch.model.Api.Nearby.ResultNearbySearch;
import com.example.goforlunch.utils.DataHolder;
import com.example.goforlunch.utils.Divider;
import com.example.goforlunch.utils.ItemClickSupport;
import com.example.goforlunch.utils.PlaceStreams;
import com.example.goforlunch.views.recyclerViews.RestoListFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestoListFragment extends Fragment {


    @BindView(R.id.recycler_resto)
    RecyclerView recyclerView;


    //for test
    private List<ResultNearbySearch> searchList = new ArrayList<>();
    //----------
    private RestoListFragmentAdapter adapter;
    private Disposable disposable;


    public static Fragment newInstance() {
        return new RestoListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_resto_list, container, false);
        ButterKnife.bind(this, view);
        String myPosition = DataHolder.getInstance().getCurrentPosiiton();
        httpRequestWithRetrofit2(myPosition);
        configureRecyclerView();
        configureOnClickRecyclerView();


        return view;
    }




    //--------------------------------
    //HTTP REQUEST WITH RETROFIT
    //--------------------------------
    private void httpRequestWithRetrofit2(String location) {
        int rad = Integer.parseInt(DataHolder.getInstance().getRadius());
        this.disposable = PlaceStreams.streamFetchNearbySearch(location, rad, "restaurant", BuildConfig.google_maps_api_key).subscribeWith(new DisposableObserver<NearbyPlaces>() {
            @Override
            public void onNext(NearbyPlaces nearbyPlaces) {
                updateUII(nearbyPlaces.getResults());
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {

            }
        });

    }


    private void updateUII(List<ResultNearbySearch> resultNearbySearches) {
        searchList.clear();


        searchList.addAll(resultNearbySearches);
        if (searchList.size() != 0) {
            for (int i = 0; i < searchList.size(); i++) {
                DataHolder.getInstance().setPlaceId(searchList.get(i).getPlaceId());


            }
        } else {
            Toast.makeText(getContext(), "There is no restaurant with this parameters", Toast.LENGTH_SHORT).show();

        }

        adapter.notifyDataSetChanged();
    }

    //---------------------------
    // RECYCLER VIEW
    //--------------------------
    private void configureRecyclerView() {
        this.searchList = new ArrayList<>();
        adapter = new RestoListFragmentAdapter(getContext(), searchList, Glide.with(this));
        recyclerView.addItemDecoration(new Divider(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.row_resto_list)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    ResultNearbySearch result = adapter.getRestaurant(position);
                    Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
                    intent.putExtra("resto_place_id", result.getPlaceId());
                    startActivity(intent);
                });

    }

    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }
}
