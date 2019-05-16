package com.example.goforlunch.controler.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goforlunch.R;
import com.example.goforlunch.model.Resto;
import com.example.goforlunch.views.recyclerViews.Divider;
import com.example.goforlunch.views.recyclerViews.RestoListFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestoListFragment extends Fragment {


    @BindView(R.id.recycler_resto)
    RecyclerView recyclerView;
    private List<Resto> restoList = new ArrayList<>();
    private RestoListFragmentAdapter adapter;


    public static Fragment newInstance() {
        return new RestoListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_resto_list, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    public void configureRecyclerView() {

        adapter = new RestoListFragmentAdapter(getContext(), restoList);
        recyclerView.addItemDecoration(new Divider(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    private void init() {
        restoList.add(new Resto("Les radeliers", "19 rue du bois CHAMBLAY", "Bouchon", "19h00", 150, 2, 3));
        restoList.add(new Resto("Petit blanc", "72 rue du stade Salins", "Jura", "20h00", 3500, 0, 1, 1));
        restoList.add(new Resto("Pizza Pino", "165 rue de Lyon LYON", "Pizzeria", "22h00", 13500, R.drawable.avatar2, 5, 2));
        configureRecyclerView();
    }
}
