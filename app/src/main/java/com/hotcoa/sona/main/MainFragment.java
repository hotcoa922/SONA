package com.hotcoa.sona.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hotcoa.sona.R;
import com.hotcoa.sona.writediary.WriteDiaryFragment;


public class MainFragment extends Fragment {

    MainActivity mainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    Button diarybt;
    WriteDiaryFragment writeDiaryFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        diarybt = (Button)rootView.findViewById(R.id.diary_bt);
        writeDiaryFragment = new WriteDiaryFragment();

        diarybt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //getFragmentManager().beginTransaction().replace(R.layout.fragment_write_diary, writeDiaryFragment);
                //getFragmentManager().beginTransaction().addToBackStack(null);
                //getFragmentManager().beginTransaction().commit();
                mainActivity.onChangeFragmentMainFragToWriteFrag(1);

            }
        });





        return rootView;
    }
}