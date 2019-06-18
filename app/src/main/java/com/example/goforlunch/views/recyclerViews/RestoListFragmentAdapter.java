package com.example.goforlunch.views.recyclerViews;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.goforlunch.R;
import com.example.goforlunch.model.Api.Nearby.ResultNearbySearch;

import java.util.List;

public class RestoListFragmentAdapter extends RecyclerView.Adapter<RestoListFragmentViewHolder> {

    private List<ResultNearbySearch> restoList;
    RequestManager glide;


    public RestoListFragmentAdapter(Context context, List<ResultNearbySearch> restoList, RequestManager glide) {
        this.restoList = restoList;
        this.glide = glide;
    }


    @NonNull
    @Override
    public RestoListFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_resto_list, parent, false);
        return new RestoListFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestoListFragmentViewHolder holder, int position) {
        holder.updateView(restoList.get(position), glide);
    }

    @Override
    public int getItemCount() {
        Log.e("size", String.valueOf(restoList.size()));
        return restoList.size();

    }
}


