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
import com.example.goforlunch.views.recyclerViews.Divider;
import com.example.goforlunch.views.recyclerViews.WorkmatesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesFragment extends Fragment {

    private List<String> mNames = new ArrayList<>();
    private List<Integer> mImageUrls = new ArrayList<Integer>();
    private WorkmatesAdapter adapter;
    @BindView(R.id.recycler_workers)
    RecyclerView recyclerView;

    public static Fragment newInstance() {
        return new WorkmatesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, view);
        initImageBitmap();
        return view;
    }

    private void initImageBitmap() {
        mImageUrls.add((R.drawable.photoseb));
        mNames.add("Seb");

        mImageUrls.add((R.drawable.photoseb));
        mNames.add("yoyoyoyoyyo");

        mImageUrls.add(R.drawable.photoseb);
        mNames.add("Salut a tous");


        mImageUrls.add((R.drawable.photoseb));
        mNames.add("Seb");

        mImageUrls.add((R.drawable.photoseb));
        mNames.add("yoyoyoyoyyo");

        mImageUrls.add(R.drawable.photoseb);
        mNames.add("Salut a tous");
        mImageUrls.add((R.drawable.photoseb));
        mNames.add("Seb");

        mImageUrls.add((R.drawable.photoseb));
        mNames.add("yoyoyoyoyyo");

        mImageUrls.add(R.drawable.photoseb);
        mNames.add("Salut a tous");


        mImageUrls.add((R.drawable.photoseb));
        mNames.add("Seb");

        mImageUrls.add((R.drawable.photoseb));
        mNames.add("yoyoyoyoyyo");

        mImageUrls.add(R.drawable.photoseb);
        mNames.add("Salut a tous");

        mImageUrls.add((R.drawable.photoseb));
        mNames.add("Seb");

        mImageUrls.add((R.drawable.photoseb));
        mNames.add("yoyoyoyoyyo");

        mImageUrls.add(R.drawable.photoseb);
        mNames.add("Salut a tous");

        //add last item to be invisible
        mImageUrls.add(null);
        mNames.add(null);

        initRecyclerView();


    }

    private void initRecyclerView() {
        adapter = new WorkmatesAdapter(getContext(), mNames, mImageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new Divider(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

}
