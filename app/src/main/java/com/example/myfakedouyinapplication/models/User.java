package com.example.myfakedouyinapplication.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Date;

public class User implements Parcelable {
    private int avatarResId; // 头像资源ID
    private String avatarUrl; // 头像URL（暂时未使用）
    private String username; // 用户名
    private String userId; // 用户ID（唯一标识符）
    private String note; // 备注
    private boolean isFollowed; // 是否已关注
    private boolean isSpecial; // 是否为特别关注
    private Date followDate; // 关注日期
    private Long createTime; // 创建时间
    private Long updateTime; // 更新时间
    private int syncStatus; // 同步状态（暂时未使用）

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

    protected User(Parcel in) {
        avatarResId = in.readInt();
        avatarUrl = in.readString();
        username = in.readString();
        userId = in.readString();
        note = in.readString();
        isFollowed = in.readByte() != 0;
        isSpecial = in.readByte() != 0;
        long tmpDate = in.readLong();
        followDate = tmpDate != -1 ? new Date(tmpDate) : null;
        createTime = in.readLong();
        updateTime = in.readLong();
        syncStatus = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(avatarResId);
        dest.writeString(avatarUrl);
        dest.writeString(username);
        dest.writeString(userId);
        dest.writeString(note);
        dest.writeByte((byte) (isFollowed ? 1 : 0));
        dest.writeByte((byte) (isSpecial ? 1 : 0));
        dest.writeLong(followDate != null ? followDate.getTime() : -1);
        dest.writeLong(createTime);
        dest.writeLong(updateTime);
        dest.writeInt(syncStatus);
    }
}
