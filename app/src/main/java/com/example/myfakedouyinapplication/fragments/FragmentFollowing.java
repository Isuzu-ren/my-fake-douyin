package com.example.myfakedouyinapplication.fragments;

import android.os.AsyncTask;
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

import java.util.Collections;
import java.util.List;

public class FragmentFollowing extends Fragment {
    private TextView followingCountText;
    private UserDataManager userDataManager;
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

        userDataManager = UserDataManager.getInstance(getContext());

        initView(view);

        loadFollowingData();

        return view;
    }

    private void initView(View view) {
        followingCountText = view.findViewById(R.id.following_count_text);
        userRecyclerView = view.findViewById(R.id.following_recycler_view);
    }

    private void loadFollowingData() {
        new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... voids) {
                return userDataManager.getFollowingUsers();
            }

            @Override
            protected void onPostExecute(List<User> users) {
                if (users != null) {
                    followingList = users;
                    setupRecyclerView();
                    updateFollowingCount();
                } else {
                    showToast("无法加载关注列表");
                }
            }
        }.execute();
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
                } else {
                    user.setFollowed(true);
                }
                userDataManager.updateUser(user);
                userAdapter.notifyItemChanged(position);
                updateFollowingCount();
            }

            @Override
            public void onItemClick(int position) {
                User user = followingList.get(position);
                String message = "已选中 " + user.getDisplayName();
                showToast(message);
            }
        });
    }

    private void updateFollowingCount() {
        if (followingCountText == null || followingList == null) {
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
        if (userDataManager != null) {
            userDataManager.refreshData();
            if (followingList != null) {
                followingList.clear();
                followingList.addAll(userDataManager.getFollowingUsers());
                if (userAdapter != null) {
                    userAdapter.notifyDataSetChanged();
                }
                updateFollowingCount();
            }
        }
    }

    private void showToast(final String message) {
        if (getContext() == null) {
            return;
        }
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
