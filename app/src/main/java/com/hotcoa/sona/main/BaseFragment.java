package com.hotcoa.sona.main;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.hotcoa.sona.R;

public class BaseFragment extends Fragment implements BackpressListener {


    public MainActivity mainActivity;
    private boolean isAddToBackStack = true;

    @Override
    public void onBackpress(){
        Log.d("","테스트 ");
        isAddToBackStack = false;
        if (mainActivity.fragmentStack.empty()) {
            mainActivity.backpressListener = null;
            return;
        }
        ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_x,mainActivity.fragmentStack.pop()).commit();
        //((MainActivity)getActivity()).getSupportFragmentManager().popBackStack();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)getActivity();
        mainActivity.backpressListener=this;

    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (isAddToBackStack) {
            mainActivity.fragmentStack.push(this);
        }
        isAddToBackStack = true;
        mainActivity = null;
    }


}
