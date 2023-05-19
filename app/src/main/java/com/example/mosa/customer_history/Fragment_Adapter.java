package com.example.mosa.customer_history;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class Fragment_Adapter extends FragmentStatePagerAdapter {
    int num;
    public Fragment_Adapter(FragmentManager fm, int select){
        super(fm);
        this.num=select;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Fragment_history tab1=new Fragment_history();
                return tab1;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return num;
    }
}
