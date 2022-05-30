package com.hotcoa.sona.mindcheck;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotcoa.sona.R;
import com.hotcoa.sona.main.BaseFragment;


public class MindCheckFragment extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mind_check, container, false);



        return rootView;
    }
}