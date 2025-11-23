package com.example.myfakedouyinapplication.models;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.myfakedouyinapplication.R;
import com.example.myfakedouyinapplication.repositorys.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UserDataManager {
    private static UserDataManager instance;
    private List<User> followingUsers;
    private UserRepository userRepository;
    private Context context;
    private boolean isInitialized = false;

    private UserDataManager(Context context) {
        this.context = context.getApplicationContext();
        followingUsers = new ArrayList<>();
        userRepository = new UserRepository(this.context);
        initializeData();
    }

    public static UserDataManager getInstance(Context context) {
        if (instance == null) {
            synchronized (UserDataManager.class) {
                if (instance == null) {
                    instance = new UserDataManager(context);
                }
            }
        }
        return instance;
    }

    private void initializeData() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    userRepository.open();
                    userRepository.initSampleData();
                    List<User> users = userRepository.getFollowedUsers();
                    if (users != null) {
                        followingUsers.clear();
                        followingUsers.addAll(users);
                    }
                    return true;
                } catch (Exception e) {
                    showToast("Failed to initialize user data: " + e.getMessage());
                    return false;
                } finally {
                    userRepository.close();
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                isInitialized = success;
            }
        }.execute();
    }

//    private void initSampleData() {
//        followingUsers.add(new User(
//                R.drawable.avator_1,
//                "Alice",
//                "alice123",
//                "A",
//                true,
//                false,
//                new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
//        ));
//
//        followingUsers.add(new User(
//                R.drawable.avator_2,
//                "Bob",
//                "bob456",
//                "B",
//                true,
//                true,
//                new Date(System.currentTimeMillis() - 48 * 60 * 60 * 1000)
//        ));
//
//        followingUsers.add(new User(
//                R.drawable.avator_3,
//                "Charlie",
//                "charlie789",
//                "",
//                true,
//                false,
//                new Date(System.currentTimeMillis() - 72 * 60 * 60 * 1000)
//        ));
//    }

    public List<User> getFollowingUsers() {
        if (!isInitialized) {
            showToast("User data is still initializing. Please wait.");
            new AsyncTask<Void, Void, List<User>>() {
                @Override
                protected List<User> doInBackground(Void... voids) {
                    try {
                        if (!userRepository.isOpen()) {
                            userRepository.open();
                        }
                        return userRepository.getFollowedUsers();
                    } catch (Exception e) {
                        showToast("Failed to fetch user data: " + e.getMessage());
                        return new ArrayList<>();
                    }
                }

                @Override
                protected void onPostExecute(List<User> users) {
                    if (users != null) {
                        followingUsers.clear();
                        followingUsers.addAll(users);
                        isInitialized = true;
                    }
                }
            }.execute();
        }
        return new ArrayList<>(followingUsers);
    }

    public int getFollowingCount() {
        int count = 0;
        for (User user : followingUsers) {
            if (user.isFollowed()) {
                count++;
            }
        }
        return count;
    }

    public void addUser(User user) {
        new AsyncTask<User, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(User... users) {
                try {
                    boolean success = userRepository.addUser(users[0]);
                    if (success) {
                        followingUsers.add(users[0]);
                    }
                    return success;
                } catch (Exception e) {
                    showToast("Failed to add user: " + e.getMessage());
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    showToast("User added successfully");
                } else {
                    showToast("Failed to add user");
                }
            }
        }.execute(user);
    }

    public void removeUser(String userId) {
        new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... userIds) {
                try {
                    User user = getUserById(userIds[0]);
                    if (user != null) {
                        user.setFollowed(false);
                        boolean success = userRepository.updateUser(user);
                        if (success) {
                            followingUsers.remove(user);
                        }
                        return success;
                    }
                    return false;
                } catch (Exception e) {
                    showToast("Failed to remove user: " + e.getMessage());
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    showToast("User removed successfully");
                } else {
                    showToast("Failed to remove user");
                }
            }
        }.execute(userId);
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
        new AsyncTask<User, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(User... users) {
                try {
                    boolean success = userRepository.updateUser(users[0]);
                    if (success) {
                        for (int i = 0; i < followingUsers.size(); i++) {
                            if (followingUsers.get(i).getUserId().equals(users[0].getUserId())) {
                                followingUsers.set(i, users[0]);
                                break;
                            }
                        }
                    }
                    return success;
                } catch (Exception e) {
                    showToast("Failed to update user: " + e.getMessage());
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    showToast("User updated successfully");
                } else {
                    showToast("Failed to update user");
                }
            }
        }.execute(updatedUser);
    }

    public void refreshData() {
        new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... voids) {
                try {
                    if (!userRepository.isOpen()) {
                        userRepository.open();
                    }
                    return userRepository.getFollowedUsers();
                } catch (Exception e) {
                    showToast("Failed to refresh user data: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<User> users) {
                if (users != null) {
                    followingUsers.clear();
                    followingUsers.addAll(users);
                    showToast("User data refreshed");
                } else {
                    showToast("Failed to refresh user data");
                }
            }
        }.execute();
    }

//    public void clearUsers() {
//        followingUsers.clear();
//    }

    private void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
