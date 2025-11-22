package com.example.myfakedouyinapplication.models;

import com.example.myfakedouyinapplication.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDataManager {
    private static UserDataManager instance;
    private List<User> followingUsers;

    private UserDataManager() {
        // Initialize followingUsers list here or load from a data source
        followingUsers = new ArrayList<>();
        initSampleData();
    }

    public static UserDataManager getInstance() {
        if (instance == null) {
            instance = new UserDataManager();
        }
        return instance;
    }

    private void initSampleData() {
        followingUsers.add(new User(
                R.drawable.avator_1,
                "Alice",
                "alice123",
                "A",
                true,
                false,
                new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
        ));

        followingUsers.add(new User(
                R.drawable.avator_2,
                "Bob",
                "bob456",
                "B",
                true,
                true,
                new Date(System.currentTimeMillis() - 48 * 60 * 60 * 1000)
        ));

        followingUsers.add(new User(
                R.drawable.avator_3,
                "Charlie",
                "charlie789",
                "C",
                true,
                false,
                new Date(System.currentTimeMillis() - 72 * 60 * 60 * 1000)
        ));
    }

    public List<User> getFollowingUsers() {
        return new ArrayList<>(followingUsers);
    }

    public int getFollowingCount() {
        return followingUsers.size();
    }

    public void addUser(User user) {
        followingUsers.add(user);
    }

    public void removeUser(String userId) {
        followingUsers.removeIf(user -> user.getUserId().equals(userId));
    }

    public User getUserById(String userId) {
        for (User user : followingUsers) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public void updateUser(User updatedUser) {
        for (int i = 0; i < followingUsers.size(); i++) {
            if (followingUsers.get(i).getUserId().equals(updatedUser.getUserId())) {
                followingUsers.set(i, updatedUser);
                return;
            }
        }
    }

    public void clearUsers() {
        followingUsers.clear();
    }
}
