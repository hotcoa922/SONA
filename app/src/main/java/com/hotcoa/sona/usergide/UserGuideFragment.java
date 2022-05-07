package com.hotcoa.sona.usergide;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.hotcoa.sona.R;


public class UserGuideFragment extends Fragment {

    int imageIndex = 0;

    ImageView iv1;
    ImageView iv2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        super.onCreate(savedInstanceState);

        iv1 = (ImageView) getView().findViewById(R.id.image1);
        iv2 = (ImageView) getView().findViewById(R.id.image2);

        return inflater.inflate(R.layout.fragment_user_guide, container, false);
    }

    public void onButtonLeftClicked(View v){
        changeImageLeft();
    }

    public void onButtonRightClicked(View v){
        changeImageRight();
    }

    private void changeImageLeft(){
        if(imageIndex ==0){
            //뷰 전환 X
        }
        else if (imageIndex >1){
            iv1.setVisibility(View.INVISIBLE);
            iv2.setVisibility(View.VISIBLE);
        }
    }

    private void changeImageRight(){
        if(imageIndex ==0){
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.INVISIBLE);
        }
        else if (imageIndex >1){
            //뷰 전환 X
        }
    }


}