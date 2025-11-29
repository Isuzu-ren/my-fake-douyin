package com.example.myfakedouyinapplication.repositorys;

import android.content.Context;
import android.util.Log;

import com.example.myfakedouyinapplication.R;
import com.example.myfakedouyinapplication.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserRepository {
    private static final String TAG = "UserRepository";

    private Context context;
    private boolean hasContext;

    // 模拟数据配置
    private static final int TOTAL_USERS = 1000;
    private static final int PAGE_SIZE = 10;

    /**
     * 无参构造函数 - 用于模拟数据阶段
     */
    public UserRepository() {
        context = null;
        hasContext = false;
        Log.d(TAG, "UserRepository: 使用无参构造函数，模拟数据阶段");
    }

    /**
     * 有参构造函数 - 用于需要Context的阶段
     */
    public UserRepository(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
            hasContext = true;
            Log.d(TAG, "UserRepository: 使用有参构造函数，Context已设置");
        } else {
            this.context = null;
            hasContext = false;
            Log.d(TAG, "UserRepository: 使用有参构造函数，但Context为null");
        }
    }

    /**
     * 分页获取用户数据
     */
    public List<User> getUsersByPage(int page, int pageSize) {
        Log.d(TAG, "获取第 " + page + " 页用户数据，页大小 " + pageSize);

        List<User> users = new ArrayList<>();
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, TOTAL_USERS);

        if (startIndex >= TOTAL_USERS) {
            Log.w(TAG, "请求的页码超出范围：" + page);
            return users; // 返回空列表
        }

        for (int i = startIndex; i < endIndex; i++) {
            users.add(createMockUser(i));
        }

        Log.d(TAG, "成功获取 " + users.size() + " 条用户数据");
        return users;
    }

    /**
     * 创建模拟用户
     */
    private User createMockUser(int index) {
        User user = new User();
        user.setUserId("user_" + index);
        user.setUsername("User " + index);
        user.setAvatarResId(R.drawable.avator_1 + (index % 8)); // 循环使用已有头像资源
        user.setNote(Math.random() < 0.2 ? "Note " + index : ""); // 20% 概率有备注
        user.setFollowed(true);
        user.setSpecial(index % 5 == 0); // 每5个用户为特别关注
        user.setFollowDate(new Date(System.currentTimeMillis() - index * 60 * 60 * 1000L)); // 模拟关注时间
        return user;
    }

    /**
     * 获取总页数
     */
    public int getTotalPages(int pageSize) {
        return (int) Math.ceil((double) TOTAL_USERS / pageSize);
    }

    /**
     * 检查是否还有更多数据
     */
    public boolean hasMoreData(int currentPage, int pageSize) {
        return (currentPage + 1) * pageSize < TOTAL_USERS;
    }

    /**
     * 关注用户
     */
    public boolean followUser(String userId) {
        Log.d(TAG, "关注用户: " + userId);
        // 模拟数据库操作
        try {
            Thread.sleep(5); // 模拟网络延迟
            return true;
        } catch (InterruptedException e) {
            Log.e(TAG, "关注用户操作被中断", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 取消关注用户
     */
    public boolean unfollowUser(String userId) {
        Log.d(TAG, "取消关注用户: " + userId);
        // 模拟数据库操作
        try {
            Thread.sleep(5); // 模拟网络延迟
            return true;
        } catch (InterruptedException e) {
            Log.e(TAG, "取消关注用户操作被中断", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 更新用户信息
     */
    public boolean updateUser(User user) {
        Log.d(TAG, "更新用户信息: " + user.getUserId());
        // 模拟数据库操作
        try {
            Thread.sleep(5); // 模拟网络延迟
            return true;
        } catch (InterruptedException e) {
            Log.e(TAG, "更新用户信息操作被中断", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 获取数据库路径（用于调试）
     */
    public String getDatabasePath() {
        if (hasValidContext()) {
            return context.getDatabasePath("user.db").getAbsolutePath();
        }
        return "模拟数据模式，无数据库路径";
    }

    /**
     * 检查是否有有效的Context
     */
    public boolean hasValidContext() {
        return hasContext && context != null;
    }
}
