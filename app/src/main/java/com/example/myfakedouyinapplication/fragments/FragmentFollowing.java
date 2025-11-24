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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myfakedouyinapplication.R;
import com.example.myfakedouyinapplication.adapters.UserAdapter;
import com.example.myfakedouyinapplication.dialogs.UserActionsDialog;
import com.example.myfakedouyinapplication.models.User;
import com.example.myfakedouyinapplication.models.UserDataManager;

import java.util.List;

public class FragmentFollowing extends Fragment {
    private TextView followingCountText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView userRecyclerView;
    private UserDataManager userDataManager;
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

        initViews(view);

        setupSwipeRefreshLayout();

        setupRecyclerView();

        loadFollowingData(true);

        return view;
    }

    private void initViews(View view) {
        followingCountText = view.findViewById(R.id.following_count_text);
        userRecyclerView = view.findViewById(R.id.following_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.following_swipe_refresh);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refrashData();
            }
        });
    }

    private void refrashData() {
        swipeRefreshLayout.setRefreshing(true);
        new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... voids) {
                userDataManager.refreshData();
                return userDataManager.getFollowingUsers();
            }

            @Override
            protected void onPostExecute(List<User> users) {
                swipeRefreshLayout.setRefreshing(false);
                if (users != null) {
                    followingList = users;
                    if (userAdapter != null) {
                        userAdapter.updateData(followingList);
                    }
                    updateFollowingCount();
                } else {
                    showToast("无法刷新关注列表");
                }
            }
        }.execute();
    }

    private void loadFollowingData(final boolean showRefreshing) {
        if (showRefreshing) {
            swipeRefreshLayout.setRefreshing(true);
        }

        new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... voids) {
                return userDataManager.getFollowingUsers();
            }

            @Override
            protected void onPostExecute(List<User> users) {
                if (users != null) {
                    followingList = users;
                    if (userAdapter != null) {
                        userAdapter.updateData(followingList);
                    }
                    updateFollowingCount();
                } else {
                    showToast("无法加载关注列表");
                }
                swipeRefreshLayout.setRefreshing(false);
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
                    user.setSpecial(false);
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

            @Override
            public void onMoreClick(int position) {
                showUserActionsDialog(position);
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
//        if (userDataManager != null) {
//            loadFollowingData(false);
//        }
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

    private void showUserActionsDialog(int position) {
        if (position < 0 || position >= followingList.size()) {
            return;
        }
        User user = followingList.get(position);
        UserActionsDialog dialog = new UserActionsDialog(getContext(), user,
                new UserActionsDialog.OnUserActionListener() {
                    @Override
                    public void onSpecialChanged(String userId, boolean isSpecial) {
                        updateUserSpecial(userId, isSpecial);
                    }

                    @Override
                    public void onNoteChanged(String userId, String note) {
                        updateUserNote(userId, note);
                    }

                    @Override
                    public void onUnfollow(String userId) {
                        updateUserUnfollow(userId);
                    }
                }
        );

        dialog.show();
    }

    private void updateUserSpecial(String userId, boolean isSpecial) {
        for (int i = 0; i < followingList.size(); i++) {
            User user = followingList.get(i);
            if (user.getUserId().equals(userId)) {
                user.setSpecial(isSpecial);
                userDataManager.updateUser(user);
                if (userAdapter != null) {
                    userAdapter.notifyItemChanged(i);
                }
                break;
            }
        }
    }

    private void updateUserNote(String userId, String note) {
        for (int i = 0; i < followingList.size(); i++) {
            User user = followingList.get(i);
            if (user.getUserId().equals(userId)) {
                user.setNote(note);
                userDataManager.updateUser(user);
                if (userAdapter != null) {
                    userAdapter.notifyItemChanged(i);
                }
                break;
            }
        }
    }

    private void updateUserUnfollow(String userId) {
        for (int i = 0; i < followingList.size(); i++) {
            User user = followingList.get(i);
            if (user.getUserId().equals(userId)) {
                user.setFollowed(false);
                user.setSpecial(false);
                userDataManager.updateUser(user);
                if (userAdapter != null) {
                    userAdapter.notifyItemChanged(i);
                }
                updateFollowingCount();
                break;
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
