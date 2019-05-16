package com.example.goforlunch.views.recyclerViews;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goforlunch.R;
import com.example.goforlunch.model.Resto;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerviewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.resto_name)
    TextView restoName;
    @BindView(R.id.resto_address)
    TextView restoAddress;
    @BindView(R.id.resto_opening_time)
    TextView restoOpeningTime;
    @BindView(R.id.resto_distance)
    TextView restoDistance;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.resto_photo)
    ImageView restoPhoto;

    public RecyclerviewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateRestoListFragment(Resto resto) {
        UpdateAllFragmentItem updateAllFragmentItem = new UpdateAllFragmentItem();
        restoName.setText(updateAllFragmentItem.setName(resto));
        restoAddress.setText(updateAllFragmentItem.address(resto));
    }
}
