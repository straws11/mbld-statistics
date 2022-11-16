package com.example.mbldapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainApp extends AppCompatActivity {

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_app);

        viewPager = findViewById(R.id.viewpager);

        //setting up adapter
        viewPagerAdapter = new ViewPagerAdapter(this);

        //add fragments
        viewPagerAdapter.add(new AttemptingFragment(),"Attempt");
        viewPagerAdapter.add(new AttemptListFragment(),"All Attempts");

        //set adapter
        viewPager.setAdapter(viewPagerAdapter);

        // The Page (fragment) titles will be displayed in the
        // tabLayout hence we need to  set the page viewer
        // we use the setupWithViewPager().
        tabLayout = findViewById(R.id.tab_layout);
        String [] tabTitles = {"Attempt","All Attempts"};
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
                ).attach();
    }
}