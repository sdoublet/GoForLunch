package com.example.goforlunch.controler.fragments;

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
import com.example.goforlunch.model.Api.Details.PlaceDetail;
import com.example.goforlunch.model.Api.Details.Result;
import com.example.goforlunch.model.Api.Distance.DistanceMatrix;
import com.example.goforlunch.model.Api.Nearby.NearbyPlaces;
import com.example.goforlunch.model.Api.Nearby.ResultNearbySearch;
import com.example.goforlunch.utils.DataHolder;
import com.example.goforlunch.utils.Divider;
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
    private Result result;
    private DistanceMatrix distanceMatrix;
    private String idd;
    private List<Result> mRestoList ;
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





        httpRequestDistancematrix(DataHolder.getInstance().getCurrentPosiiton(), String.valueOf(DataHolder.getInstance().getStringList()));


        String myPosition = DataHolder.getInstance().getCurrentPosiiton();
        Log.e("data", DataHolder.getInstance().getCurrentPosiiton());
        httpRequestWithRetrofit2(myPosition);
        httpRequestDistancematrix(myPosition, String.valueOf(DataHolder.getInstance().getStringList()));
        configureRecyclerView2();



        // this.configureRecyclerView();
        return view;
    }

//    private void configureRecyclerView() {
//        this.mRestoList = new ArrayList<>();
//        adapter = new RestoListFragmentAdapter(getContext(), mRestoList);//Listresto.getinstance
//        recyclerView.addItemDecoration(new Divider(getContext(), LinearLayoutManager.VERTICAL));
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//    }



    private void httpRequestDistancematrix(String origins, String destinations) {
        this.disposable = PlaceStreams.streamfetchDistanceMatrix(origins, destinations, BuildConfig.GOOGLE_MAPS_API_KEY).subscribeWith(new DisposableObserver<DistanceMatrix>() {
            @Override
            public void onNext(DistanceMatrix distanceMatrix) {
                Log.e("request", distanceMatrix.getStatus());
                Log.e("request", distanceMatrix.getOriginAddresses().toString());
                Log.e("request", distanceMatrix.getDestinationAddresses().toString());
                //Log.e("request", String.valueOf(distanceMatrix.getRows().get(0).getElements().get(0).getDistance().getValue()));


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    private void updateUI(List<Result> restoList) {
        mRestoList.clear();
        if (restoList.size() > 0) {
            mRestoList.addAll(restoList);
        } else {
            Toast.makeText(getContext(), "There is no restaurant with this parameters", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }
    //-------------------------------------------------------------------------------------------------------------------------
    //for test
    //--------------

    //--------------------------------
    //HTTP REQUEST WITH RETROFIT
    //--------------------------------
    private void httpRequestWithRetrofit2(String location) {

        this.disposable = PlaceStreams.streamFetchNearbySearch(location, 10000, "restaurant", BuildConfig.GOOGLE_MAPS_API_KEY).subscribeWith(new DisposableObserver<NearbyPlaces>() {
            @Override
            public void onNext(NearbyPlaces nearbyPlaces) {
                //takeMyList(nearbyPlaces.getResults());
                updateUII(nearbyPlaces.getResults());

Log.e("iddd", DataHolder.getInstance().getPlaceId());
                //getinstance.setresto nerabyplace.getresult



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
        if (searchList.size()!=0){
            for (int i=0; i<searchList.size(); i++){
                DataHolder.getInstance().setPlaceId(searchList.get(i).getPlaceId());
                idd = DataHolder.getInstance().getPlaceId();
                Log.e("yoyo", idd);
            }
        }

        Log.e("rv", String.valueOf(searchList.size()));
        adapter.notifyDataSetChanged();
    }

    private void configureRecyclerView2() {
        this.searchList = new ArrayList<>();
        adapter = new RestoListFragmentAdapter(getContext(), searchList, Glide.with(this));//Listresto.getinstance
        Log.e("rv", String.valueOf(searchList.size()));
        recyclerView.addItemDecoration(new Divider(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }
}
