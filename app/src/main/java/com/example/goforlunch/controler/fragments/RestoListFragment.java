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

import com.example.goforlunch.BuildConfig;
import com.example.goforlunch.R;
import com.example.goforlunch.model.Api.Details.PlaceDetail;
import com.example.goforlunch.model.Api.Details.Result;
import com.example.goforlunch.model.Api.Nearby.ResultNearbySearch;
import com.example.goforlunch.utils.Divider;
import com.example.goforlunch.utils.ListResto;
import com.example.goforlunch.utils.PlaceServices;
import com.example.goforlunch.utils.PlaceStreams;
import com.example.goforlunch.views.recyclerViews.RestoListFragmentAdapter;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import retrofit2.Call;

public class RestoListFragment extends Fragment  {


    @BindView(R.id.recycler_resto)
    RecyclerView recyclerView;
    private Result result;
    private String idd;
    private List<Result> mRestoList = new ArrayList<>();
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
        idd = String.valueOf(ListResto.getInstance().getMyList());
        httpRequestWithRetrofit(idd);
        Log.e("idd",idd);
        ButterKnife.bind(this, view);
        this.configureRecyclerView();
        return view;
    }

    private void configureRecyclerView() {
        this.mRestoList = new ArrayList<>();
        adapter = new RestoListFragmentAdapter(getContext(), mRestoList);//Listresto.getinstance
        recyclerView.addItemDecoration(new Divider(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    //--------------------------------
    //HTTP REQUEST WITH RETROFIT
    //--------------------------------
    private void httpRequestWithRetrofit(String id) {
        this.disposable = PlaceStreams.streamFetchPlaceDetails(id, BuildConfig.GOOGLE_MAPS_API_KEY).subscribeWith(new DisposableObserver<PlaceDetail>() {
            @Override
            public void onNext(PlaceDetail placeDetail) {
                updateUI(mRestoList);
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



}
