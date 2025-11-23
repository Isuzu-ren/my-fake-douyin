package com.example.myfakedouyinapplication.repositorys;

import android.content.Context;

import com.example.myfakedouyinapplication.R;
import com.example.myfakedouyinapplication.datebases.UserDao;
import com.example.myfakedouyinapplication.models.User;

import java.util.List;

public class UserRepository {
    private Context context;
    private UserDao userDao;

    public UserRepository(Context context) {
        this.context = context.getApplicationContext();
        userDao = new UserDao(this.context);
    }

    public void open() {
        try {
            userDao.open();
        } catch (Exception e) {
            showToast("Failed to open user database: " + e.getMessage());
        }
    }

    public boolean isOpen() {
        return userDao.isOpen();
    }

    public void close() {
        userDao.close();
    }

    public List<User> getFollowedUsers() {
        try {
            return userDao.getAllUsers();
        } catch (Exception e) {
            showToast("Failed to retrieve followed users: " + e.getMessage());
            return null;
        }
    }

    public boolean addUser(User user) {
        try {
            long result = userDao.addUser(user);
            if (result != -1) {
                return true;
            } else {
                showToast("Failed to add user: Insertion error");
                return false;
            }
        } catch (Exception e) {
            showToast("Failed to add user: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUser(User user) {
        try {
            int result = userDao.updateUser(user);
            if (result > 0) {
                return true;
            } else {
                showToast("Failed to update user");
                return false;
            }
        } catch (Exception e) {
            showToast("Failed to update user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        try {
            int result = userDao.deleteUser(userId);
            if (result > 0) {
                return true;
            } else {
                showToast("Failed to delete user");
                return false;
            }
        } catch (Exception e) {
            showToast("Failed to delete user: " + e.getMessage());
            return false;
        }
    }

    public void initSampleData() {
        if (!userDao.getAllUsers().isEmpty()) {
            return; // Data already exists
        }

        addUser(new User(
                R.drawable.avator_1,
                "Alice",
                "alice111",
                "A1",
                true,
                false,
                new java.util.Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
        ));

        addUser(new User(
                R.drawable.avator_2,
                "Bob",
                "bob222",
                "B2",
                true,
                true,
                new java.util.Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000)
        ));

        addUser(new User(
                R.drawable.avator_3,
                "Charlie",
                "charlie333",
                "",
                true,
                false,
                new java.util.Date(System.currentTimeMillis() - 3 * 60 * 60 * 1000)
        ));

        addUser(new User(
                R.drawable.avator_4,
                "Diana",
                "diana444",
                "",
                true,
                true,
                new java.util.Date(System.currentTimeMillis() - 5 * 60 * 60 * 1000)
        ));

        addUser(new User(
                R.drawable.avator_5,
                "Ethan",
                "ethan555",
                "E5",
                true,
                false,
                new java.util.Date(System.currentTimeMillis() - 10 * 60 * 60 * 1000)
        ));

        addUser(new User(
                R.drawable.avator_6,
                "Fiona",
                "fiona666",
                "",
                true,
                true,
                new java.util.Date(System.currentTimeMillis() - 15 * 60 * 60 * 1000)
        ));

        addUser(new User(
                R.drawable.avator_7,
                "George",
                "george777",
                "G7",
                true,
                false,
                new java.util.Date(System.currentTimeMillis() - 20 * 60 * 60 * 1000)
        ));

        addUser(new User(
                R.drawable.avator_8,
                "Hannah",
                "hannah888",
                "",
                true,
                true,
                new java.util.Date(System.currentTimeMillis() - 30 * 60 * 60 * 1000)
        ));
    }

    private void showToast(String message) {
    }
}
