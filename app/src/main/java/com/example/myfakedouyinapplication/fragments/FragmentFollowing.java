package com.example.myfakedouyinapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfakedouyinapplication.R;
import com.example.myfakedouyinapplication.adapters.UserAdapter;
import com.example.myfakedouyinapplication.models.User;
import com.example.myfakedouyinapplication.models.UserDataManager;

import java.util.List;

public class FragmentFollowing extends Fragment {
    private TextView followingCountText;
    private final UserDataManager userDataManager = UserDataManager.getInstance();
    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private List<User> followingList;

    public FragmentFollowing() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        followingList = userDataManager.getFollowingUsers();

        initView(view);

        setupRecyclerView();

        updateFollowingCount();

        return view;
    }

    private void initView(View view) {
        followingCountText = view.findViewById(R.id.following_count_text);
        userRecyclerView = view.findViewById(R.id.following_recycler_view);
    }

    private void setupRecyclerView() {
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userAdapter = new UserAdapter(followingList);

        userRecyclerView.setAdapter(userAdapter);

        userAdapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onUnfollowClick(int position) {
                if (position < 0 || position >= followingList.size()) {
                    return;
                }
                User user = followingList.get(position);
                if (user.isFollowed()) {
                    user.setFollowed(false);
                    userAdapter.notifyItemChanged(position);

                    //

                    updateFollowingCount();
                } else {
                    user.setFollowed(true);
                    userAdapter.notifyItemChanged(position);
                    updateFollowingCount();
                }
            }

            @Override
            public void onItemClick(int position) {
                User user = followingList.get(position);
                String message = "已选中 " + user.getDisplayName();
                if (getContext() != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateFollowingCount() {
        if (followingCountText == null) {
            return;
        }
        int followingCount = 0;
        for (User user : followingList) {
            if (user.isFollowed()) {
                followingCount++;
            }
        }
        String text = "我的关注（" + followingCount + "）";
        followingCountText.setText(text);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFollowingCount();
    }
}
