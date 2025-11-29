package com.example.myfakedouyinapplication.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.myfakedouyinapplication.R;
import com.example.myfakedouyinapplication.constants.MessageConstants;
import com.example.myfakedouyinapplication.models.User;
import com.example.myfakedouyinapplication.repositorys.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 用户数据任务处理器
 * 专门处理用户数据相关的业务逻辑
 */
public class UserDataTaskHandler {
    private static final String TAG = "UserDataTaskHandler";

    private UserRepository userRepository;
    private Handler mainThreadHandler; // 主线程的Handler的引用

    public UserDataTaskHandler(UserRepository userRepository, Handler mainThreadHandler) {
        this.userRepository = userRepository;
        this.mainThreadHandler = mainThreadHandler;
        Log.d(TAG, "用户数据任务处理器已初始化");
    }

    /**
     * 处理加载更多用户数据的请求
     */
    public void handleLoadMoreUsers(int page) {
        Log.d(TAG, "处理加载更多用户数据请求，页码: " + page);

        try {
            sendLoadingState(true); // 发送加载状态到主线程

            Thread.sleep(0);

            // 使用 UserRepository 获取数据
            List<User> users = userRepository.getUsersByPage(page, MessageConstants.PAGE_SIZE);
            boolean hasMoreData = userRepository.hasMoreData(page, MessageConstants.PAGE_SIZE);

            // 调试日志
            Log.d(TAG, "从UserRepository获取的数据量: " + (users != null ? users.size() : "null"));
            if (users != null && !users.isEmpty()) {
                Log.d(TAG, "第一条用户ID: " + users.get(0).getUserId());
            }

            // 发送成功消息到主线程
            Message successMsg = Message.obtain();
            successMsg.what = MessageConstants.MSG_LOAD_USERS_SUCCESS;
            successMsg.obj = users;
            successMsg.arg1 = hasMoreData ? 1 : 0;
            successMsg.arg2 = page;

            mainThreadHandler.sendMessage(successMsg);
            Log.d(TAG, "用户数据加载成功，页码: " + page + "，用户数: " + users.size());
        } catch (InterruptedException e) {
            Log.e(TAG, "加载用户数据时被中断", e);
            sendErrorMessage("加载超时，请重试。");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            Log.e(TAG, "加载用户数据时发生错误", e);
            sendErrorMessage("加载用户数据失败：" + e.getMessage());
        } finally {
            sendLoadingState(false); // 发送加载完成状态到主线程
        }
    }

    /**
     * 处理刷新用户数据的请求
     */
    public void handleRefreshUsers() {
        Log.d(TAG, "处理刷新用户数据请求");

        // 刷新时从第一页开始加载
        handleLoadMoreUsers(0);
    }

    /**
     * 处理关注用户操作
     */
    public void handleFollowUser(String userIdx) {
        Log.d(TAG, "处理关注用户请求: " + userIdx);
        try {
            Thread.sleep(2);
            boolean success = userRepository.followUser(userIdx);
            if (success) {
                sendSuccessMessage("已关注用户 " + userIdx, MessageConstants.MSG_FOLLOW_USER_SUCCESS);
            } else {
                sendErrorMessage("关注用户失败");
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "关注用户时发生错误", e);
            sendErrorMessage("关注用户失败：" + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "更新用户信息时发生错误", e);
            sendErrorMessage("更新用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 处理取消关注用户操作
     */
    public void handleUnfollowUser(String userIdx) {
        Log.d(TAG, "处理取消关注用户请求: " + userIdx);
        try {
            Thread.sleep(2);
            boolean success = userRepository.unfollowUser(userIdx);
            if (success) {
                sendSuccessMessage("已取消关注用户 " + userIdx, MessageConstants.MSG_UNFOLLOW_USER_SUCCESS);
            } else {
                sendErrorMessage("取消关注用户失败");
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "取消关注用户时发生错误", e);
            sendErrorMessage("取消关注用户失败：" + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "更新用户信息时发生错误", e);
            sendErrorMessage("更新用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    public void handleUpdateUser(User user) {
        Log.d(TAG, "处理更新用户信息请求: " + user.getUserId());
        try {
            Thread.sleep(2);
            boolean success = userRepository.updateUser(user);
            if (success) {
                sendSuccessMessage("用户信息已更新 " + user.getUserId(), MessageConstants.MSG_UPDATE_USER_SUCCESS);
            } else {
                sendErrorMessage("更新用户信息失败");
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "更新用户信息时被中断", e);
            sendErrorMessage("更新用户信息失败：" + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "更新用户信息时发生错误", e);
            sendErrorMessage("更新用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 发送错误消息到主线程
     */
    private void sendErrorMessage(String error) {
        Message msg = Message.obtain();
        msg.what = MessageConstants.MSG_LOAD_USERS_FAILED;
        msg.obj = error;
        mainThreadHandler.sendMessage(msg);
    }

    /**
     * 发送成功消息
     */
    private void sendSuccessMessage(String message, int messageType) {
        Message msg = Message.obtain();
        msg.what = messageType;
        msg.obj = message;
        mainThreadHandler.sendMessage(msg);
        Log.d(TAG, "操作成功: " + message);
    }

    /**
     * 发送加载状态到主线程
     */
    private void sendLoadingState(boolean isLoading) {
        Message msg = Message.obtain();
        msg.what = isLoading ? MessageConstants.MSG_SHOW_LOADING : MessageConstants.MSG_HIDE_LOADING;
        mainThreadHandler.sendMessage(msg);
    }

    /**
     * 公共方法：发送错误消息
     * 供NetworkThread调用
     */
    public void sendError(String errorMessage) {
        sendErrorMessage(errorMessage);
    }

    /**
     * 公共方法：发送加载状态
     */
    public void setLoading(boolean isLoading) {
        sendLoadingState(isLoading);
    }
}
