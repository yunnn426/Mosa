package com.example.mosa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private ImageButton btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutorial);
        getSupportActionBar().hide();

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);

//변화될 레이아웃들 주소
        layouts = new int[]{
                R.layout.start_page1,
                R.layout.start_page2,
                R.layout.start_page3
        };

        addBottomDots(0);

        pagerAdapter = new PagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btn_start = findViewById(R.id.startApp);
        btn_start.setVisibility(View.GONE);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TutorialActivity.this, Signup.class);
                startActivity(intent);
            }
        });

    }

        private void addBottomDots(int currentPage) {
            dots = new TextView[layouts.length];

            int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
            int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

            dotsLayout.removeAllViews();

            for (int i = 0; i < dots.length; i++) {
                dots[i] = new TextView(this);
                dots[i].setText(Html.fromHtml("&#8226;"));
                dots[i].setTextSize(35);
                dots[i].setTextColor(colorsInactive[currentPage]);
                dotsLayout.addView(dots[i]);
            }

            if (dots.length > 0) {
                dots[currentPage].setTextColor(colorsActive[currentPage]);
            }
        }

        private int getItem(int i){
            return viewPager.getCurrentItem()+i;
        }

        private void moveMainPage() {
            startActivity(new Intent(TutorialActivity.this, Start.class));
            finish();
        }

        ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);

                if (position == layouts.length - 1) {
                    //btnNext.setText(getString(R.string.start));
                    btn_start.setVisibility(View.VISIBLE);
                    dotsLayout.setVisibility(View.GONE);
                } else {
                    //btnNext.setText(getString(R.string.next));
                    btn_start.setVisibility(View.GONE);
                    dotsLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        };

        public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter{
            private LayoutInflater layoutInflater;

            public PagerAdapter() {

            }

            @Override
            public Object instantiateItem(ViewGroup container, int position){
                layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View view = layoutInflater.inflate(layouts[position], container, false);
                container.addView(view);

                return view;
            }

            @Override
            public int getCount() {
                return layouts.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object obj) {
                return view == obj;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object){
                View view = (View) object;
                container.removeView(view);
            }
        }
}