package com.example.mosa.customer_history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.mosa.R;

public class Fragment_customerinfo extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState){
        View view=inflater.inflate(R.layout.customer_information,container,false);
        return view;
    }
}
