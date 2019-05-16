package com.example.goforlunch.views.recyclerViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goforlunch.R;
import com.example.goforlunch.model.Resto;

import java.util.List;

public class RestoListFragmentAdapter extends RecyclerView.Adapter<RecyclerviewHolder> {

    private List<Resto> restoList;
    private Context context;

    public RestoListFragmentAdapter(Context context, List<Resto> restoList) {
        this.restoList = restoList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_resto_list, parent, false);
        return new RecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, int position) {
holder.updateRestoListFragment(restoList.get(position));
    }

    @Override
    public int getItemCount() {
     return restoList.size();
}}
