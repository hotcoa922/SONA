package com.hotcoa.sona.usergide;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.view.ViewManager;
import android.widget.Button;
import android.widget.ImageView;
//import android.widget.SimpleAdapter;

import com.hotcoa.sona.R;
import com.hotcoa.sona.main.BaseFragment;

//import java.util.List;


public class UserGuideFragment extends BaseFragment {

    int imageIndex = 0;

    ImageView iv1;
    ImageView iv2;
    ImageView iv3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_user_guide, container, false);

        Button buttonL = rootView.findViewById(R.id.buttonLeft);
        buttonL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImageLeft();
            }
        });

        Button buttonR = rootView.findViewById((R.id.buttonRight));
        buttonR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImageRight();
            }
        });

        iv1 = rootView.findViewById(R.id.image1);
        iv2 = rootView.findViewById(R.id.image2);
        iv3 = rootView.findViewById(R.id.image3);

        return rootView;
    }



    private void changeImageLeft(){
        if(imageIndex == 0){
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.INVISIBLE);
        }
        else if (imageIndex > 0){
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.INVISIBLE);
            imageIndex --;
        }
    }

    private void changeImageRight(){
        if(imageIndex == 1){
            iv1.setVisibility(View.INVISIBLE);
            iv2.setVisibility(View.VISIBLE);
        }
        else if (imageIndex < 1){
            iv1.setVisibility(View.INVISIBLE);
            iv2.setVisibility(View.VISIBLE);
            imageIndex ++;
        }
    }


}