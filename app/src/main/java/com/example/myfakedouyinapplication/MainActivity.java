package com.example.myfakedouyinapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myfakedouyinapplication.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter adapter;
    private ImageButton backButton;

    private static final String TAG = "MainActivity";

    private String[] tabTitles = {"互关", "关注", "粉丝", "朋友"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
//        Log.d(TAG, "onCreate: initViews done");
        setupViewPager();
//        Log.d(TAG, "onCreate: setupViewPager done");
        setupTabLayout();
//        Log.d(TAG, "onCreate: setupTabLayout done");
        setupBackButton();
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        backButton = findViewById(R.id.btn_back);
    }

    private void setupViewPager() {
        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
    }

    private void setupTabLayout() {
        // 使用TabLayoutMediator将TabLayout与ViewPager2关联
        new TabLayoutMediator(tabLayout , viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();
//        for (String title : tabTitles) {
//            tabLayout.addTab(tabLayout.newTab().setText(title));
//        }
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {}
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {}
//        });
//
//        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                tabLayout.selectTab(tabLayout.getTabAt(position));
//            }
//        });
    }

    private void setupBackButton() {
        // 按下时提示暂不支持该功能
        backButton.setOnClickListener(v -> {;
            Toast.makeText(MainActivity.this, "暂不支持该功能", Toast.LENGTH_SHORT).show();
        });
    }
}
