package com.example.myfakedouyinapplication.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myfakedouyinapplication.constants.MessageConstants;
import com.example.myfakedouyinapplication.models.User;
import com.example.myfakedouyinapplication.repositorys.UserRepository;

public class NetworkThread extends HandlerThread {
    private static final String TAG = "NetworkThread";

    private Handler workerHandler; // 工作线程的Handler

    private UserDataTaskHandler userDataHandler; // 用户数据任务处理器

    public NetworkThread() {
        super("NetworkWorkerThread");
        setPriority(Thread.MAX_PRIORITY); // 设置线程优先级为最高
    }

    /**
     * 初始化工作线程的Handler
     *
     * @param mainHandler 主线程Handler，用于回调
     * @param repository  数据仓库实例
     */
    public void prepareHandler(Handler mainHandler, UserRepository repository) {
        userDataHandler = new UserDataTaskHandler(repository, mainHandler);

        workerHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                processMessage(msg);
            }
        };

        Log.d(TAG, "网络工作线程Handler已初始化");
    }

    /**
     * 处理接收到的消息
     */
    private void processMessage(Message msg) {
        if (userDataHandler == null) {
            Log.e(TAG, "用户数据处理器未初始化");
            sendErrorMessage("内部错误：用户数据处理器未初始化");
            return;
        }

        try {
            // 根据消息类型路由到相应的处理器
            switch (msg.what) {
                case MessageConstants.MSG_LOAD_MORE_USERS:
                    userDataHandler.handleLoadMoreUsers(msg.arg1); // arg1传递页码
                    break;

                case MessageConstants.MSG_REFRESH_USERS:
                    userDataHandler.handleRefreshUsers();
                    break;

                case MessageConstants.MSG_FOLLOW_USER:
                    userDataHandler.handleFollowUser((String) msg.obj, msg.arg1); // obj传递userId，arg1传递位置
                    break;

                case MessageConstants.MSG_UNFOLLOW_USER:
                    userDataHandler.handleUnfollowUser((String) msg.obj, msg.arg1); // obj传递userId，arg1传递位置
                    break;

                case MessageConstants.MSG_UPDATE_USER:
                    userDataHandler.handleUpdateUser((User) msg.obj, msg.arg1); // obj传递User对象，arg1传递位置
                    break;

                case MessageConstants.MSG_SET_USER_NOTE:
                    User user = (User) msg.obj; // obj传递User对象
                    String note = user.getNote();
                    String userId = user.getUserId();
                    userDataHandler.handleUpdateUserNote(userId, note, msg.arg1); // arg1传递位置
                    break;

                case MessageConstants.MSG_TOGGLE_SPECIAL:
                    userDataHandler.handleUpdateUserSpecial(
                            (String) msg.obj, // obj传递userId
                            msg.arg1 == 1,   // arg1传递是否特别关注状态
                            msg.arg2        // arg2传递位置
                    );
                    break;

                default:
                    Log.w(TAG, "收到未知消息类型: " + msg.what);
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "处理消息时发生错误", e);
            sendErrorMessage("处理请求时发生错误：" + e.getMessage());
        }
    }

    /**
     * 发送错误消息到主线程
     */
    private void sendErrorMessage(String error) {
        if (userDataHandler != null) {
            userDataHandler.sendError(error);
        } else {
            Log.e(TAG, "无法发送错误消息，用户数据处理器未初始化");
        }
    }

    /**
     * 获取工作线程的Handler
     */
    public Handler getWorkerHandler() {
        return workerHandler;
    }

    /**
     * 安全关闭线程
     */
    public void shutdown() {
        if (isAlive()) {
            quitSafely();
            Log.d(TAG, "网络工作线程已关闭");
        }
    }
}