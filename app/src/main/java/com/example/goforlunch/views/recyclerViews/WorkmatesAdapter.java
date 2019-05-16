package com.example.goforlunch.views.recyclerViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.goforlunch.R;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter <WorkmatesViewHolder>{

    private Context context;
    private List<String> mImageNames = new ArrayList<>();
    private List<Integer> mImages = new ArrayList<>();

    public WorkmatesAdapter(Context context, List<String> mImageNames, List<Integer> mImages) {
        this.context = context;
        this.mImageNames = mImageNames;
        this.mImages = mImages;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_workmates, parent, false);
        return new WorkmatesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {


        Glide.with(context)
                .asBitmap()
                .load(mImages.get(position))
                .apply(RequestOptions.circleCropTransform())
                .into(holder.photoWorkmates);

        holder.textViewWorkmates.setText(mImageNames.get(position));
    }




    @Override
    public int getItemCount() {
        return mImageNames.size();
    }
}
