package com.hotcoa.sona.contents;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotcoa.sona.R;
import com.hotcoa.sona.main.BaseFragment;

public class ContentsEvaluationFragment extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contents_evaluation, container, false);



        return rootView;
    }
}