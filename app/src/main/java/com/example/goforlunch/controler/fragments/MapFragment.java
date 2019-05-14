package com.example.goforlunch.controler.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.goforlunch.R;
import com.example.goforlunch.controler.activities.LoginActivity;
import com.firebase.ui.auth.AuthUI;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.button)
    public void onclick(){
        AuthUI.getInstance().signOut(getContext());
        Intent intent = new Intent(getContext() , LoginActivity.class);
        startActivity(intent);
    }
}
