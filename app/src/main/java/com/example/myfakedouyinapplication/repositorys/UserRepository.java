package com.example.myfakedouyinapplication.repositorys;

import android.content.Context;
import android.util.Log;

import com.example.myfakedouyinapplication.R;
import com.example.myfakedouyinapplication.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static final String TAG = "UserRepository";

    // 模拟数据配置
    private static final int PAGE_SIZE = 10;
    private static final int TOTAL_USERS = 1000;

    // 全局数据缓存
    private List<User> allUsers = new ArrayList<>(); // 所有1000个用户
    private Map<String, User> userMap = new HashMap<>(); // 用户ID到用户对象的映射
    private int followedCount = 0; // 已关注用户数

    // 数据版本控制（用于刷新同步）
    private int dataVersion = 0;

    private Context context;
    private boolean hasContext;

    /**
     * 无参构造函数 - 用于模拟数据阶段
     */
    public UserRepository() {
        context = null;
        hasContext = false;
        Log.d(TAG, "UserRepository: 使用无参构造函数，模拟数据阶段");
        initializeData();
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
        initializeData();
    }

    /**
     * 初始化1000个用户数据
     */
    private synchronized void initializeData() {
        Log.d(TAG, "开始初始化" + TOTAL_USERS + "个用户数据");
        allUsers.clear();
        userMap.clear();

        for (int i = 0; i < TOTAL_USERS; i++) {
            User user = createMockUser(i);
            allUsers.add(user);
            userMap.put(user.getUserId(), user);
            if (user.isFollowed()) {
                followedCount++;
            }
        }

        Log.d(TAG, "数据初始化完成，总用户数: " + allUsers.size() +
                ", 已关注: " + followedCount);
    }

    /**
     * 获取总关注数（应该是1000，但会根据操作变化）
     */
    public synchronized int getTotalFollowedCount() {
        return followedCount;
    }

    /**
     * 获取总用户数（固定1000）
     */
    public synchronized int getTotalUserCount() {
        return allUsers.size();
    }

    /**
     * 分页获取用户数据
     */
    public synchronized List<User> getUsersByPage(int page, int pageSize) {
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, getTotalUserCount());

        if (startIndex >= getTotalUserCount() || startIndex < 0) {
            Log.w(TAG, "请求的页码超出范围：" + page);
            return new ArrayList<>(); // 返回空列表
        }

        List<User> result = new ArrayList<>(allUsers.subList(startIndex, endIndex));
        Log.d(TAG, "获取第 " + page + " 页用户数据，页大小 " + pageSize +
                "，实际返回 " + result.size() + " 条数据");
        return result;
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
        user.setSpecial(Math.random() < 0.2); // 20% 概率为特别关注
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
    public synchronized boolean hasMoreData(int currentPage, int pageSize) {
        return (currentPage + 1) * pageSize < TOTAL_USERS;
    }

    /**
     * 关注用户
     */
    public synchronized boolean followUser(String userId) {
        Log.d(TAG, "关注用户: " + userId);
        try {
            Thread.sleep(0);
            User user = userMap.get(userId);
            if (user == null) {
                Log.w(TAG, "关注用户失败，用户ID不存在: " + userId);
                return false;
            }
            if (user.isFollowed()) {
                Log.d(TAG, "用户已被关注，无需重复关注: " + userId);
                return true;
            }

            // 更新用户状态
            user.setFollowed(true);

            // 增加关注计数
            followedCount++;
            dataVersion++;
            Log.d(TAG, "用户关注成功: " + userId + "，当前已关注用户数: " + followedCount);
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
    public synchronized boolean unfollowUser(String userId) {
//        Log.d(TAG, "=== 开始取消关注诊断 ===");
//        Log.d(TAG, "请求用户ID: " + userId);
//
//        // 1. 检查Map中是否存在该键
//        if (!userMap.containsKey(userId)) {
//            Log.e(TAG, "❌ 用户ID不存在于映射表中");
//            Log.e(TAG, "映射表键集: " + userMap.keySet());
//            return false;
//        }
//
//        // 2. 获取用户对象
//        User user = userMap.get(userId);
//        Log.d(TAG, "找到用户对象: " + System.identityHashCode(user));
//        Log.d(TAG, "用户ID: " + user.getUserId());
//        Log.d(TAG, "用户名: " + user.getUsername());
//        Log.d(TAG, "关注状态: " + user.isFollowed());
//
//        // 3. 验证对象一致性
//        User directUser = null;
//        for (User u : allUsers) {
//            if (userId.equals(u.getUserId())) {
//                directUser = u;
//                break;
//            }
//        }
//
//        if (directUser != null) {
//            Log.d(TAG, "直接查找用户对象: " + System.identityHashCode(directUser));
//            Log.d(TAG, "对象是否相同: " + (user == directUser));
//            Log.d(TAG, "内容是否相同: " + user.equals(directUser));
//        }
//
//        // ==============================

        Log.d(TAG, "取消关注用户: " + userId);
        // 模拟数据库操作
        try {
            Thread.sleep(0);
            User user = userMap.get(userId);
            if (user == null) {
                Log.w(TAG, "取消关注用户失败，用户ID不存在: " + userId);
                return false;
            }
            if (!user.isFollowed()) {
                Log.d(TAG, "用户未被关注，无需取消关注: " + userId);
                return true;
            }
            // 更新用户状态
            user.setFollowed(false);
            user.setSpecial(false); // 取消关注时同时取消特别关注
            // 减少关注计数
            followedCount--;
            dataVersion++;
            Log.d(TAG, "用户取消关注成功: " + userId + "，当前已关注用户数: " + followedCount);
            return true;
        } catch (InterruptedException e) {
            Log.e(TAG, "取消关注用户操作被中断", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 设置特别关注
     */
    public synchronized boolean setUserSpecial(String userId, boolean isSpecial) {
        Log.d(TAG, "设置用户特别关注: " + userId + "，状态: " + isSpecial);
        // 模拟数据库操作
        try {
            Thread.sleep(0);
            User user = userMap.get(userId);
            if (user == null) {
                Log.w(TAG, "设置特别关注失败，用户ID不存在: " + userId);
                return false;
            }
            if (!user.isFollowed()) {
                Log.w(TAG, "设置特别关注失败，用户未被关注: " + userId);
                return false;
            }
            if (user.isSpecial() == isSpecial) {
                Log.d(TAG, "用户特别关注状态未改变，无需更新: " + userId);
                return true;
            }
            // 更新用户状态
            user.setSpecial(isSpecial);
            dataVersion++;
            Log.d(TAG, "用户特别关注状态更新成功: " + userId + "，新状态: " + isSpecial);
            return true;
        } catch (InterruptedException e) {
            Log.e(TAG, "设置特别关注操作被中断", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 设置用户备注
     */
    public synchronized boolean setUserNote(String userId, String note) {
        Log.d(TAG, "设置用户备注: " + userId + "，备注: " + note);
        // 模拟数据库操作
        try {
            Thread.sleep(0);
            User user = userMap.get(userId);
            if (user == null) {
                Log.w(TAG, "设置用户备注失败，用户ID不存在: " + userId);
                return false;
            }
            // 更新用户备注
            user.setNote(note);
            dataVersion++;
            Log.d(TAG, "用户备注更新成功: " + userId + "，新备注: " + note);
            return true;
        } catch (InterruptedException e) {
            Log.e(TAG, "设置用户备注操作被中断", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 更新用户信息
     */
    public synchronized boolean updateUser(User user) {
        Log.d(TAG, "更新用户信息: " + user.getUserId());
        // 模拟数据库操作
        try {
            Thread.sleep(0);
            User existingUser = userMap.get(user.getUserId());
            if (existingUser == null) {
                Log.w(TAG, "更新用户信息失败，用户ID不存在: " + user.getUserId());
                return false;
            }
            // 更新用户信息（除id之外的其他字段）
            existingUser.setUsername(user.getUsername());
            existingUser.setAvatarResId(user.getAvatarResId());
            existingUser.setNote(user.getNote());
            existingUser.setSpecial(user.isSpecial());
            existingUser.setFollowDate(user.getFollowDate());
            dataVersion++;
            Log.d(TAG, "用户信息更新成功: " + user.getUserId());
            return true;
        } catch (InterruptedException e) {
            Log.e(TAG, "更新用户信息操作被中断", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 根据ID获取用户
     */
    public synchronized User getUserById(String userId) {
        return userMap.get(userId);
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

    /**
     * 刷新数据（重新加载用户）
     */
    public synchronized void refreshData() {
        Log.d(TAG, "数据已刷新，版本：" + dataVersion + "，关注用户数：" + followedCount);
        // 这里可以重新从网络加载，目前使用内存数据
    }

    /**
     * 获取数据版本（用于检测数据变化）
     */
    public synchronized int getDataVersion() {
        return dataVersion;
    }
}
