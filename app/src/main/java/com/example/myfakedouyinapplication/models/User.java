package com.example.myfakedouyinapplication.models;

import java.util.Date;

public class User {
    private int avatarResId;
    private String username;
    private String userId;
    private String note;
    private boolean isFollowed;
    private boolean isSpecial;
    private Date followDate;

    public User(int avatarResId, String username, String userId, String note, boolean isFollowed, boolean isSpecial, Date followDate) {
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

    public void setAvatarResId(int avatarResId) {
        this.avatarResId = avatarResId;
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
