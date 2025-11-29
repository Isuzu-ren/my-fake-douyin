package com.example.myfakedouyinapplication.datebases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.myfakedouyinapplication.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问对象（当前阶段暂未使用）
 * <p>
 * 注意：二阶段需求改为从服务端获取数据，本地数据库功能暂时停用。
 * 保留此类为未来可能的离线功能或缓存功能做准备。
 *
 * @deprecated 当前版本使用服务端数据，此类暂未激活
 */
@Deprecated
public class UserDao {
    private SQLiteDatabase database;
    private UserDatabaseHelper dbHelper;

    public UserDao(Context context) {
        dbHelper = new UserDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public boolean isOpen() {
        return database != null && database.isOpen();
    }

    public void close() {
        dbHelper.close();
    }

    public long addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(UserDatabaseHelper.COLUMN_AVATAR_RES_ID, user.getAvatarResId());
        values.put(UserDatabaseHelper.COLUMN_AVATAR_URL, user.getAvatarUrl());
        values.put(UserDatabaseHelper.COLUMN_USERNAME, user.getUsername());
        values.put(UserDatabaseHelper.COLUMN_USER_ID, user.getUserId());
        values.put(UserDatabaseHelper.COLUMN_NOTE, user.getNote());
        values.put(UserDatabaseHelper.COLUMN_IS_FOLLOWED, user.isFollowed() ? 1 : 0);
        values.put(UserDatabaseHelper.COLUMN_IS_SPECIAL, user.isSpecial() ? 1 : 0);
        values.put(UserDatabaseHelper.COLUMN_FOLLOW_TIME, user.getFollowTimeMillis());
        values.put(UserDatabaseHelper.COLUMN_CREATE_TIME, user.getCreateTime());
        values.put(UserDatabaseHelper.COLUMN_UPDATE_TIME, user.getUpdateTime());
        values.put(UserDatabaseHelper.COLUMN_SYNC_STATUS, user.getSyncStatus());

        return database.insert(UserDatabaseHelper.TABLE_USER, null, values);
    }

    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(UserDatabaseHelper.COLUMN_AVATAR_RES_ID, user.getAvatarResId());
        values.put(UserDatabaseHelper.COLUMN_AVATAR_URL, user.getAvatarUrl());
        values.put(UserDatabaseHelper.COLUMN_USERNAME, user.getUsername());
        values.put(UserDatabaseHelper.COLUMN_NOTE, user.getNote());
        values.put(UserDatabaseHelper.COLUMN_IS_FOLLOWED, user.isFollowed() ? 1 : 0);
        values.put(UserDatabaseHelper.COLUMN_IS_SPECIAL, user.isSpecial() ? 1 : 0);
        values.put(UserDatabaseHelper.COLUMN_FOLLOW_TIME, user.getFollowTimeMillis());
        values.put(UserDatabaseHelper.COLUMN_CREATE_TIME, user.getCreateTime());
        values.put(UserDatabaseHelper.COLUMN_UPDATE_TIME, System.currentTimeMillis());
        values.put(UserDatabaseHelper.COLUMN_SYNC_STATUS, user.getSyncStatus());

        return database.update(UserDatabaseHelper.TABLE_USER,
                values,
                UserDatabaseHelper.COLUMN_USER_ID + " = ?",
                new String[]{user.getUserId()});
    }

    public int deleteUser(String userId) {
        return database.delete(UserDatabaseHelper.TABLE_USER,
                UserDatabaseHelper.COLUMN_USER_ID + " = ?",
                new String[]{userId});
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        Cursor cursor = database.query(UserDatabaseHelper.TABLE_USER,
                null, null, null, null, null, UserDatabaseHelper.COLUMN_FOLLOW_TIME + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                User user = cursorToUser(cursor);
                users.add(user);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return users;
    }

    public User getUserById(String userId) {
        User user = null;
        Cursor cursor = database.query(UserDatabaseHelper.TABLE_USER,
                null,
                UserDatabaseHelper.COLUMN_USER_ID + " = ?",
                new String[]{userId},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        return user;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setAvatarResId(cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_AVATAR_RES_ID)));
        user.setAvatarUrl(cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_AVATAR_URL)));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_USERNAME)));
        user.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_USER_ID)));
        user.setNote(cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_NOTE)));
        user.setFollowed(cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_IS_FOLLOWED)) == 1);
        user.setSpecial(cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_IS_SPECIAL)) == 1);
        user.setFollowTimeMillis(cursor.getLong(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_FOLLOW_TIME)));
        user.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_CREATE_TIME)));
        user.setUpdateTime(cursor.getLong(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_UPDATE_TIME)));
        user.setSyncStatus(cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_SYNC_STATUS)));
        return user;
    }
}
