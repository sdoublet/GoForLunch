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
import com.example.goforlunch.model.Api.Details.Result;
import com.example.goforlunch.utils.Divider;
import com.example.goforlunch.views.recyclerViews.RestoListFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class RestoListFragment extends Fragment {


    @BindView(R.id.recycler_resto)
    RecyclerView recyclerView;
    private List<Result> restoList = new ArrayList<>();
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

        return view;
    }

    public void configureRecyclerView() {

        adapter = new RestoListFragmentAdapter(getContext(), restoList);
        recyclerView.addItemDecoration(new Divider(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }


}
