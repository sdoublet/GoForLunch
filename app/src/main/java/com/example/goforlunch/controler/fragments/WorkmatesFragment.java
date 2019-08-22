package com.example.goforlunch.controler.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.goforlunch.R;
import com.example.goforlunch.controler.activities.PlaceDetailActivity;
import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.example.goforlunch.model.User;
import com.example.goforlunch.utils.Divider;
import com.example.goforlunch.utils.ItemClickSupport;
import com.example.goforlunch.views.recyclerViews.WorkmatesAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesFragment extends Fragment {


    private WorkmatesAdapter adapter;
    @BindView(R.id.recycler_workers)
    RecyclerView recyclerView;

    public static Fragment newInstance() {
        return new WorkmatesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, view);
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();



        return view;
    }

    //Hide search button item
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item=menu.findItem(R.id.search_button);
        if (item!=null){
            item.setVisible(false);
        }
    }

    private void configureRecyclerView() {

        Query allUsers = UserHelper.getAllUsers().orderBy("mRestaurantId", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(allUsers, User.class)
                .build();
        adapter = new WorkmatesAdapter(options, getContext(), Glide.with(recyclerView));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new Divider(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }


    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.row_workmates)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    User user = adapter.getItem(position);
                    if (user.getmRestaurantId() != null) {
                        Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
                        intent.putExtra("resto_place_id", user.getmRestaurantId());
                        startActivity(intent);
                    }else {
                        Toast.makeText(getContext(), user.getUsername()+ " " + this.getString(R.string.hasnt_decided_yet),Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
