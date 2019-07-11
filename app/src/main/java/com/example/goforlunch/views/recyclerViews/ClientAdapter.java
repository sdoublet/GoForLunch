package com.example.goforlunch.views.recyclerViews;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.goforlunch.R;
import com.example.goforlunch.model.User;

import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientViewHolder> {

    private List<User> users;
    RequestManager glide;

    public ClientAdapter(List<User> users, RequestManager glide) {
        this.users = users;
        this.glide = glide;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_workmates, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        holder.updateView(users.get(position), glide);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
