package com.example.myfakedouyinapplication.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myfakedouyinapplication.constants.MessageConstants;
import com.example.myfakedouyinapplication.fragments.FragmentFollowing;
import com.example.myfakedouyinapplication.models.User;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 主线程消息处理器
 * 负责在主线程中安全地处理消息和更新UI
 */
public class MainThreadHandler extends Handler {
    private static final String TAG = "MainThreadHandler";

    private final WeakReference<FragmentFollowing> fragmentRef;

    public MainThreadHandler(@NonNull FragmentFollowing fragment) {
        super(Looper.getMainLooper()); // 确保在主线程上运行
        this.fragmentRef = new WeakReference<>(fragment);
        Log.d(TAG, "主线程Handler已初始化");
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        FragmentFollowing fragment = fragmentRef.get();

        // 检查Fragment是否仍然存在
        if (fragment == null || fragment.isDetached() || fragment.getActivity() == null) {
            Log.w(TAG, "Fragment已被销毁，无法处理消息");
            return;
        }

        try {
            processMessage(fragment, msg);
        } catch (Exception e) {
            Log.e(TAG, "处理消息时发生异常: " + e.getMessage(), e);
            // 显示通用错误提示
            if (fragment.getContext() != null) {
                Toast.makeText(fragment.getContext(), "操作失败，请重试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 处理具体的消息
     */
    private void processMessage(FragmentFollowing fragment, Message msg) {
        switch (msg.what) {
            case MessageConstants.MSG_LOAD_USERS_SUCCESS:
                fragment.handleMainThreadMessage(msg);
                break;
            case MessageConstants.MSG_LOAD_USERS_FAILED:
                fragment.handleMainThreadMessage(msg);
                break;
            case MessageConstants.MSG_SHOW_LOADING:
                handleShowLoading(fragment, true);
                break;
            case MessageConstants.MSG_HIDE_LOADING:
                handleShowLoading(fragment, false);
                break;
            case MessageConstants.MSG_UPDATE_UI:
                handleUpdateUI(fragment);
                break;
            default:
                fragment.handleMainThreadMessage(msg);
                break;
        }
    }

    /**
     * 处理加载用户数据成功的消息
     */
    private void handleLoadUsersSuccess(FragmentFollowing fragment, Message msg) {
        // 从消息中获取用户列表
        @SuppressWarnings("unchecked")

        List<User> users = (List<User>) msg.obj;
        boolean hasMore = msg.arg1 == 1;
        int page = msg.arg2;

        Log.d(TAG, "处理加载用户数据成功的消息，用户数量: " + users.size() + ", 是否有更多: " + hasMore + ", 页码: " + page);

        if (users != null) {
            fragment.onUsersLoaded(users, hasMore, page);

            // 显示成功提示（只在第一页或刷新时显示）
            if (page == 0 && fragment.getContext() != null) {
                Toast.makeText(fragment.getContext(),
                        "加载完成，共" + users.size() + "条数据", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "用户列表为空，无法更新UI");
            if (fragment.getContext() != null) {
                Toast.makeText(fragment.getContext(),
                        "加载失败，用户列表为空", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 处理加载用户数据失败的消息
     */
    private void handleLoadUsersFailed(FragmentFollowing fragment, Message msg) {
        String error = msg.obj != null ? msg.obj.toString() : "未知错误";
        Log.e(TAG, "处理加载用户数据失败的消息，错误信息: " + error);

        fragment.onLoadFailed(error);

        if (fragment.getContext() != null) {
            Toast.makeText(fragment.getContext(),
                    "加载用户数据失败: " + error, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 处理显示/隐藏加载状态
     */
    private void handleShowLoading(FragmentFollowing fragment, boolean show) {
        Log.d(TAG, "设置加载状态: " + (show ? "显示" : "隐藏"));
        fragment.setLoadingState(show);
    }

    /**
     * 处理UI更新消息
     */
    private void handleUpdateUI(FragmentFollowing fragment) {
        Log.d(TAG, "收到UI更新消息");
        fragment.updateUI();
    }

    /**
     * 处理未知消息
     */
    private void handleUnknownMessage(FragmentFollowing fragment, Message msg) {
        Log.w(TAG, "收到未知消息: what=" + msg.what +
                ", arg1=" + msg.arg1 + ", arg2=" + msg.arg2 +
                ", obj=" + msg.obj);

        if (fragment.getContext() != null) {
            Toast.makeText(fragment.getContext(), "系统繁忙，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 安全地发送消息到主线程
     */
    public void sendSafeMessage(int what) {
        sendSafeMessage(what, 0, 0, null);
    }

    public void sendSafeMessage(int what, Object obj) {
        sendSafeMessage(what, 0, 0, obj);
    }

    public void sendSafeMessage(int what, int arg1, int arg2, Object obj) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // 如果在主线程，直接处理
            Message msg = Message.obtain(this, what, arg1, arg2, obj);
            handleMessage(msg);
        } else {
            // 如果在其他线程，发送到主线程
            Message msg = obtainMessage(what, arg1, arg2, obj);
            sendMessage(msg);
        }
    }

    /**
     * 检查关联的Fragment是否有效
     */
    public boolean isFragmentValid() {
        FragmentFollowing fragment = fragmentRef.get();
        return fragment != null && !fragment.isDetached() && fragment.getActivity() != null;
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        fragmentRef.clear();
        removeCallbacksAndMessages(null); // 移除所有消息和回调
        Log.d(TAG, "主线程Handler已清理");
    }
}
