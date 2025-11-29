package com.example.myfakedouyinapplication.models;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * UserDataManager - 已过时
 *
 * @deprecated 已由新的 ThreadManager + UserRepository 架构替代
 * 保留此类仅为向后兼容，新代码请使用 {@link com.example.myfakedouyinapplication.core.ThreadManager}
 * <p>
 * 迁移指南：
 * 1. 数据获取：使用 ThreadManager.getInstance().loadMoreUsers(page)
 * 2. 用户操作：使用 ThreadManager.getInstance().followUser(userId) 等
 * 3. 生命周期：在onDestroy中调用 ThreadManager.getInstance().shutdown()
 */
@Deprecated
public class UserDataManager {
    private static final String TAG = "UserDataManager[Deprecated]";
    private static UserDataManager instance;

    // 空实现或抛出异常，防止误用
    public static UserDataManager getInstance(Context context) {
        Log.w(TAG, "⚠️ UserDataManager 已过时，请使用 ThreadManager");
        if (instance == null) {
            instance = new UserDataManager();
        }
        return instance;
    }

    // 返回空列表或模拟数据
    public List<User> getFollowedUsers() {
        Log.w(TAG, "此方法已过时，返回空列表");
        return new ArrayList<>();
    }

    // 空实现
    public void refreshData() {
        Log.w(TAG, "此方法已过时，无实际操作");
    }

    // 空实现
    public void updateUser(User user) {
        Log.w(TAG, "此方法已过时，无实际操作");
    }

    // 添加迁移提示方法
    public void showMigrationGuide() {
        Log.e(TAG, "================================================");
        Log.e(TAG, "❌ UserDataManager 已过时，请迁移到新架构：");
        Log.e(TAG, "✅ 新架构：ThreadManager + UserRepository + UserDataTaskHandler");
        Log.e(TAG, "✅ 获取实例：ThreadManager.getInstance()");
        Log.e(TAG, "✅ 初始化：threadManager.initialize(context, fragment)");
        Log.e(TAG, "✅ 加载数据：threadManager.loadMoreUsers(page)");
        Log.e(TAG, "================================================");
    }

    // 私有构造函数，防止外部实例化
    private UserDataManager() {
        Log.w(TAG, "UserDataManager 已过时，请使用新的 ThreadManager 架构");
    }
}