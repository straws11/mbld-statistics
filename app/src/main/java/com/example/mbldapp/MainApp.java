package com.example.mbldapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
        //toolbar setup
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_main);
        myToolbar.setTitle("Multiblind Statistics");
        setSupportActionBar(myToolbar);

        viewPager = findViewById(R.id.viewpager);

        //setting up adapter
        viewPagerAdapter = new ViewPagerAdapter(this);

        //add fragments
        viewPagerAdapter.add(new AttemptingFragment(),"Attempt");
        viewPagerAdapter.add(new HistoryFragment(),"History");
        viewPagerAdapter.add(new GraphFragment(),"Graph");

        //set adapter
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 1) {//if History tab selected then update the attempt list
                    FragmentManager fm = getSupportFragmentManager();
                    HistoryFragment histFrag = (HistoryFragment) fm.findFragmentById(R.id.HistFragment);//returns null!!! WHY!!?!?!?!?
                    if (histFrag != null) {
                        Toast.makeText(MainApp.this, "fr", Toast.LENGTH_SHORT).show();
                        histFrag.refreshAdapter();
                    }
                    System.out.println("Registered callback");
                }
            }
        });

        //creating the tabs using an array to get their names
        tabLayout = findViewById(R.id.tab_layout);
        String [] tabTitles = {"Attempt","History","Graph"};
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
                ).attach();
    }

}