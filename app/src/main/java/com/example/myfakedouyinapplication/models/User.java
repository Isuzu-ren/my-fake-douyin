package com.example.myfakedouyinapplication.models;

import android.util.Log;

import java.util.Date;

public class User {
    private int avatarResId;
    private String avatarUrl;
    private String username;
    private String userId;
    private String note;
    private boolean isFollowed;
    private boolean isSpecial;
    private Date followDate;
    private Long createTime;
    private Long updateTime;
    private int syncStatus;

    public User() {
        long currentTime = System.currentTimeMillis();
        this.createTime = currentTime;
        this.updateTime = currentTime;
        this.syncStatus = 0; // Default sync status
    }

    public User(int avatarResId, String username, String userId, String note, boolean isFollowed, boolean isSpecial, Date followDate) {
        this();
        this.avatarResId = avatarResId;
        this.username = username;
        this.userId = userId;
        this.note = note;
        this.isFollowed = isFollowed;
        this.isSpecial = isSpecial;
        this.followDate = followDate;
    }

    public User(int avatarResId, String username, String userId, String note, boolean isSpecial) {
        this(avatarResId, username, userId, note, true, isSpecial, new Date());
    }

    public User(int avatarResId, String username, String userId) {
        this(avatarResId, username, userId, "", true, false, new Date());
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getNote() {
        return note;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public Date getFollowDate() {
        return followDate;
    }

    public long getFollowTimeMillis() {
        if (followDate != null) {
            return followDate.getTime();
        } else {
            Log.w("User", "Follow date is null for user: " + username);
            return 0;
        }
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setAvatarResId(int avatarResId) {
        this.avatarResId = avatarResId;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public void setSpecial(boolean special) {
        isSpecial = special;
    }

    public void setFollowDate(Date followDate) {
        this.followDate = followDate;
    }

    public void setFollowTimeMillis(long timeMillis) {
        this.followDate = new Date(timeMillis);
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getDisplayName() {
        if (note != null && !note.isEmpty()) {
            return note;
        }
        return username;
    }

    @Override
    public String toString() {
        return "User{" +
                "avatarResId=" + avatarResId +
                ", username='" + username + '\'' +
                ", userId='" + userId + '\'' +
                ", note='" + note + '\'' +
                ", isFollowed=" + isFollowed +
                ", isSpecial=" + isSpecial +
                ", followDate=" + followDate +
                '}';
    }
}
