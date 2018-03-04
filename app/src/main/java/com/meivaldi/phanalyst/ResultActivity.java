package com.meivaldi.phanalyst;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ResultActivity extends AppCompatActivity {

    private int[] layouts;
    private MyViewPagerAdapter myViewPagerAdapter;
    private Button map;

    TextView pHLabel;
    ViewPager suggestionPlant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        suggestionPlant = (ViewPager) findViewById(R.id.suggestionPlant);
        pHLabel = (TextView) findViewById(R.id.pHValue);
        map = (Button) findViewById(R.id.seeMap);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Map.class));
            }
        });

        String pHValue = pHLabel.getText().toString();
        float pH = Float.parseFloat(pHValue);

        if(pH == 7.0){
            layouts = new int[]{
                    R.layout.plant_cabbage,
                    R.layout.plant_banana,
                    R.layout.plant_broccoli
            };
        } else if(pH == 6.5){
            layouts = new int[]{
                    R.layout.plant_carrot,
                    R.layout.plant_melon,
                    R.layout.plant_mint
            };
        } else if(pH == 6.0){
            layouts = new int[]{
                    R.layout.plant_garlic,
                    R.layout.plant_pakcoy,
                    R.layout.plant_papaya
            };
        } else if(pH == 5.5){
            layouts = new int[]{
                    R.layout.plant_onion,
                    R.layout.plant_pineapple,
                    R.layout.plant_potato
            };
        } else if(pH == 5.0){
            layouts = new int[]{
                    R.layout.plant_watermelon,
                    R.layout.plant_spinach,
                    R.layout.plant_radish,
                    R.layout.plant_strawberry
            };
        } else {
            layouts = new int[]{ R.layout.plant_default };
        }

        myViewPagerAdapter = new MyViewPagerAdapter();
        suggestionPlant.setAdapter(myViewPagerAdapter);
        suggestionPlant.addOnPageChangeListener(viewPagerPageChangeListener);

    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
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
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
