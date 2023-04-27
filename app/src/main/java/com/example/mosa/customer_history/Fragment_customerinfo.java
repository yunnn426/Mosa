package com.example.mosa.customer_history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mosa.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class Fragment_customerinfo extends Fragment {
    TextView customer_info_1;
    TextView customer_info_2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState){
        View view=inflater.inflate(R.layout.customer_information,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        customer_info_1= view.findViewById(R.id.customer_info_1);
        customer_info_2=view.findViewById(R.id.customer_info_2);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        long creatTime = user.getMetadata().getCreationTimestamp();
        long lastTime=user.getMetadata().getLastSignInTimestamp();
        Date date_1 = new Date(creatTime);
        Date date_2 = new Date(lastTime);

        customer_info_1.setText(date_1.toString());
        customer_info_2.setText(date_2.toString());
    }
}
