package com.example.myfakedouyinapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myfakedouyinapplication.R;
import com.example.myfakedouyinapplication.models.UserDataManager;

public class FragmentFollowing extends Fragment {
    private TextView followingCountText;
    private final UserDataManager userDataManager = UserDataManager.getInstance();

    public FragmentFollowing() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        initView(view);
        updateFollowingCount();
        return view;
    }

    private void initView(View view) {
        followingCountText = view.findViewById(R.id.following_count_text);
    }

    private void updateFollowingCount() {
        if (followingCountText == null) {
            return;
        }
        int followingCount = userDataManager.getFollowingCount();
        String text = "我的关注（" + followingCount + "）";
        followingCountText.setText(text);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFollowingCount();
    }
}
