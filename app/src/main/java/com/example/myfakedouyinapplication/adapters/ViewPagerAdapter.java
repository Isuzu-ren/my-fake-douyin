package com.example.myfakedouyinapplication.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myfakedouyinapplication.fragments.FragmentFollower;
import com.example.myfakedouyinapplication.fragments.FragmentFollowing;
import com.example.myfakedouyinapplication.fragments.FragmentFriend;
import com.example.myfakedouyinapplication.fragments.FragmentMutuals;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FragmentMutuals();
            case 1:
                return new FragmentFollowing();
            case 2:
                return new FragmentFollower();
            case 3:
                return new FragmentFriend();
            default:
                return new FragmentFollowing();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
