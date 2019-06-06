package com.example.goforlunch.views.recyclerViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goforlunch.R;
import com.example.goforlunch.model.Api.Details.PlaceDetail;
import com.example.goforlunch.model.Api.Details.Result;
import com.example.goforlunch.model.Api.Nearby.NearbyPlaces;
import com.example.goforlunch.model.Resto;

import java.util.List;

public class RestoListFragmentAdapter extends RecyclerView.Adapter<RestoListFragmentViewHolder> {

    private List<Result> restoList;

    public RestoListFragmentAdapter(Context context, List<Result> restoList) {
        this.restoList = restoList;
    }

    @NonNull
    @Override
    public RestoListFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_resto_list,parent, false );
        return new RestoListFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestoListFragmentViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return restoList.size();
    }
}


