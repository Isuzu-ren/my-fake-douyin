package com.example.myfakedouyinapplication.core;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.myfakedouyinapplication.constants.MessageConstants;
import com.example.myfakedouyinapplication.fragments.FragmentFollowing;
import com.example.myfakedouyinapplication.models.User;
import com.example.myfakedouyinapplication.repositorys.UserRepository;

/**
 * 线程管理器
 * 统一管理应用程序中的所有线程和处理器
 */
public class ThreadManager {
    private static final String TAG = "ThreadManager";
    private static ThreadManager instance;

    private Context appContext;
    private NetworkThread networkThread;
    private MainThreadHandler mainThreadHandler;
    // private UserDataTaskHandler userDataTaskHandler;
    private UserRepository userRepository;

    private ThreadManager() {
        // 私有构造函数，防止外部实例化
    }

    /**
     * 获取单例实例
     */
    public static synchronized ThreadManager getInstance() {
        if (instance == null) {
            instance = new ThreadManager();
            Log.d(TAG, "创建ThreadManager单例实例");
        }
        return instance;
    }

    /**
     * 初始化线程管理器
     */
    public void initialize(Context context, FragmentFollowing fragment) {
        Log.d(TAG, "初始化线程管理器");
        this.appContext = context.getApplicationContext();
        // 初始化主线程Handler
        mainThreadHandler = new MainThreadHandler(fragment);
        // 初始化数据仓库
        userRepository = new UserRepository(appContext);
        Log.d(TAG, "数据仓库路径：" + userRepository.getDatabasePath());
        // 初始化网络线程
        networkThread = new NetworkThread();
        networkThread.start();
        networkThread.prepareHandler(mainThreadHandler, userRepository);
        // 初始化用户数据任务处理器
        // userDataTaskHandler = new UserDataTaskHandler(userRepository, mainThreadHandler);

        Log.d(TAG, "网络线程已启动");
    }

    /**
     * 获取网络工作线程的Handler
     */
    public Handler getNetworkHandler() {
        if (networkThread == null) {
            Log.e(TAG, "网络线程未初始化");
            return null;
        }
        return networkThread.getWorkerHandler();
    }

    /**
     * 获取主线程Handler
     */
    public Handler getMainHandler() {
        return mainThreadHandler;
    }

    /**
     * 获取用户数据任务处理器
     */
//    public UserDataTaskHandler getUserDataHandler() {
//        return userDataTaskHandler;
//    }

    /**
     * 获取用户数据仓库
     */
    public UserRepository getUserRepository() {
        return userRepository;
    }

    /**
     * 获取应用上下文
     */
    public Context getAppContext() {
        return appContext;
    }

    /**
     * 安全关闭所有线程
     */
    public void shutdown() {
        Log.d(TAG, "开始关闭线程管理器");

        if (mainThreadHandler != null) {
            mainThreadHandler.cleanup();
            mainThreadHandler = null;
        }

        if (networkThread != null) {
            networkThread.shutdown();
            networkThread = null;
        }

        // userDataTaskHandler = null;
        userRepository = null;

        Log.d(TAG, "线程管理器已关闭");
    }

    /**
     * 检查是否已初始化
     */
    public boolean isInitialized() {
        return networkThread != null && mainThreadHandler != null;
    }

    /**
     * 加载更多用户数据
     */
    public void loadMoreUsers(int page) {
        if (!isInitialized()) {
            Log.e(TAG, "线程管理器未初始化，无法加载数据");
            return;
        }

        Handler networkHandler = getNetworkHandler();
        if (networkHandler != null) {
            Message msg = Message.obtain();
            msg.what = MessageConstants.MSG_LOAD_MORE_USERS;
            msg.arg1 = page; // 传递页码
            networkHandler.sendMessage(msg);
            Log.d(TAG, "发送加载更多用户数据请求，页码: " + page);
        } else {
            Log.e(TAG, "无法获取网络处理器，加载请求未发送");
        }
    }

    /**
     * 刷新用户数据
     */
    public void refreshUsers() {
        if (!isInitialized()) {
            Log.e(TAG, "线程管理器未初始化，无法刷新数据");
            return;
        }

        Handler networkHandler = getNetworkHandler();
        if (networkHandler != null) {
            Message msg = Message.obtain();
            msg.what = MessageConstants.MSG_REFRESH_USERS;
            networkHandler.sendMessage(msg);
            Log.d(TAG, "已发送刷新数据的请求");
        } else {
            Log.e(TAG, "无法获取网络处理器，刷新请求未发送");
        }
    }

    /**
     * 关注用户
     */
    public void followUser(String userId, int position) {
        if (!isInitialized()) {
            Log.e(TAG, "线程管理器未初始化，无法执行关注操作");
            return;
        }

        Handler networkHandler = getNetworkHandler();
        if (networkHandler != null) {
            Message msg = Message.obtain();
            msg.what = MessageConstants.MSG_FOLLOW_USER;
            msg.obj = userId;
            msg.arg1 = position;
            networkHandler.sendMessage(msg);
            Log.d(TAG, "已发送关注用户请求: " + userId);
        } else {
            Log.e(TAG, "无法获取网络处理器，刷新请求未发送");
        }
    }

    /**
     * 取消关注用户
     */
    public void unfollowUser(String userId, int position) {
        if (!isInitialized()) {
            Log.e(TAG, "线程管理器未初始化，无法执行取消关注操作");
            return;
        }

        Handler networkHandler = getNetworkHandler();
        if (networkHandler != null) {
            Message msg = Message.obtain();
            msg.what = MessageConstants.MSG_UNFOLLOW_USER;
            msg.obj = userId;
            msg.arg1 = position;
            networkHandler.sendMessage(msg);
            Log.d(TAG, "已发送取消关注用户请求: " + userId);
        } else {
            Log.e(TAG, "无法获取网络处理器，刷新请求未发送");
        }
    }

    /**
     * 更新用户信息
     */
    public void updateUser(User user, int position) {
        if (!isInitialized()) {
            Log.e(TAG, "线程管理器未初始化，无法执行更新操作");
            return;
        }

        Handler networkHandler = getNetworkHandler();
        if (networkHandler != null) {
            Message msg = Message.obtain();
            msg.what = MessageConstants.MSG_UPDATE_USER;
            msg.obj = user;
            msg.arg1 = position;
            networkHandler.sendMessage(msg);
            Log.d(TAG, "已发送更新用户请求: " + user.getUserId());
        } else {
            Log.e(TAG, "无法获取网络处理器，刷新请求未发送");
        }
    }

    /**
     * 更新用户特别关注状态
     */
    public void updateUserSpecial(String userId, boolean isSpecial, int position) {
        if (!isInitialized()) {
            Log.e(TAG, "线程管理器未初始化，无法执行更新特别关注操作");
            return;
        }

        Handler networkHandler = getNetworkHandler();
        if (networkHandler != null) {
            Message msg = Message.obtain();
            msg.what = MessageConstants.MSG_TOGGLE_SPECIAL;
            msg.obj = userId;
            msg.arg1 = isSpecial ? 1 : 0;
            msg.arg2 = position;
            networkHandler.sendMessage(msg);
            Log.d(TAG, "已发送更新用户特别关注请求: " + userId + ", isSpecial: " + isSpecial);
        }
    }

    /**
     * 更新用户备注
     */
    public void updateUserNote(User user, int position) {
        if (!isInitialized()) {
            Log.e(TAG, "线程管理器未初始化，无法执行更新备注操作");
            return;
        }

        Handler networkHandler = getNetworkHandler();
        if (networkHandler != null) {
            Message msg = Message.obtain();
            msg.what = MessageConstants.MSG_SET_USER_NOTE;
            msg.obj = user;
            msg.arg1 = position;
            networkHandler.sendMessage(msg);
            Log.d(TAG, "已发送更新用户备注请求: " + user.getUserId());
        } else {
            Log.e(TAG, "无法获取网络处理器，刷新请求未发送");
        }
    }
}
