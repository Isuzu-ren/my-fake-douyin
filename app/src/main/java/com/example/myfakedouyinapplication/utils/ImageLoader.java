package com.example.myfakedouyinapplication.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.myfakedouyinapplication.R;
import com.example.myfakedouyinapplication.models.User;

import java.util.List;

public class ImageLoader {
    private static final String TAG = "ImageLoader";

    // 图片加载配置
    private static final int AVATAR_SIZE = 50; // 头像大小
    private static final int CORNER_RADIUS = 25; // 圆角半径（头像为圆形）
    private static final int FADE_DURATION = 300; // 渐变动画持续时间（毫秒）

    /**
     * 加载用户头像（主方法）
     */
    public static void loadUserAvatar(@NonNull ImageView imageView, @NonNull User user) {
        if (imageView == null) {
            Log.w(TAG, "ImageView is null, 无法加载头像");
            return;
        }

        if (user == null) {
            Log.w(TAG, "User is null, 设置默认头像");
            loadDefaultAvatar(imageView);
            return;
        }

        // 优先使用网络头像URL
        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
            loadNetworkAvatar(imageView, avatarUrl, user);
        } else if (user.getAvatarResId() != 0) {
            // 使用本地资源头像
            loadLocalAvatar(imageView, user.getAvatarResId());
        } else {
            // 使用默认头像
            loadDefaultAvatar(imageView);
        }
    }

    /**
     * 获取头像加载配置
     */
    private static RequestOptions getAvatarRequestOptions() {
        return new RequestOptions()
                .centerCrop()
                .circleCrop() // 圆形头像
                .override(AVATAR_SIZE, AVATAR_SIZE) // 设置头像大小
                .placeholder(R.drawable.avatar_placeholder) // 占位图
                .error(R.drawable.ic_warning) // 错误图
                .dontAnimate() // 禁用默认动画
                .diskCacheStrategy(DiskCacheStrategy.ALL) // 缓存所有版本
                .skipMemoryCache(false) // 使用内存缓存
                .encodeQuality(85); // 压缩质量
    }

    /**
     * 加载默认头像
     */
    private static void loadDefaultAvatar(ImageView imageView) {
        imageView.setImageResource(android.R.drawable.sym_def_app_icon);
    }

    /**
     * 加载本地资源头像
     */
    private static void loadLocalAvatar(ImageView imageView, int resId) {
        loadDefaultAvatar(imageView);
    }

    /**
     * 加载网络头像
     */
    private static void loadNetworkAvatar(ImageView imageView, String url, User user) {
        Log.d(TAG, "加载网络头像: " + url + " 用户: " + user.getUsername());
        RequestManager requestManager = Glide.with(imageView.getContext());

        // 创建渐变动画工厂
        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory.Builder(FADE_DURATION)
                .setCrossFadeEnabled(true)
                .build();

        // 构建请求
        RequestBuilder<Drawable> requestBuilder = requestManager
                .load(url)
                .apply(getAvatarRequestOptions())
                .transition(DrawableTransitionOptions.with(factory))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.w(TAG, "头像加载失败: " + url + " 用户: " + user.getUsername(), e);
                        // 网络加载失败时加载本地资源作为备用

                        if (user != null && user.getAvatarResId() != 0) {
                            Log.d(TAG, "尝试加载本地头像资源作为备用: " + user.getAvatarResId());
                            loadLocalAvatar(imageView, user.getAvatarResId());
                        } else {
                            Log.d(TAG, "没有可用的本地头像资源，加载默认头像");
                            loadDefaultAvatar(imageView);
                        }
                        return true; // 表示我们处理了错误
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "头像加载成功: " + url + " 用户: " + user.getUsername());
                        return false;
                    }
                });
        // 执行请求
        requestBuilder.into(imageView);
    }

    /**
     * 预加载用户头像（用于优化体验）
     */
    public static void preloadUserAvatar(Context context, User user) {
        if (context == null || user == null) {
            Log.w(TAG, "Context或User为null，无法预加载头像");
            return;
        }

        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
            Log.d(TAG, "预加载用户头像: " + avatarUrl + " 用户: " + user.getUsername());
            Glide.with(context)
                    .load(avatarUrl)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.DATA) // 仅缓存原始数据
                            .override(80, 80) // 预加载较小尺寸
                            .dontAnimate() // 禁用动画
                    ).preload();
        }
    }

    /**
     * 预加载多个用户头像
     */
    public static void preloadUserAvatars(Context context, List<User> users) {
        if (context == null || users == null || users.isEmpty()) {
            Log.w(TAG, "Context或用户列表为null或空，无法预加载头像");
            return;
        }

        // 限制预加载数量，避免内存压力
        int maxPreload = Math.min(users.size(), 15);

        for (int i = 0; i < maxPreload; i++) {
            preloadUserAvatar(context, users.get(i));
        }
    }

    /**
     * 暂停图片加载（滚动时调用以提高流畅度）
     */
    public static void pauseLoads(Context context) {
        if (context != null) {
            Glide.with(context).pauseRequests();
            Log.d(TAG, "图片加载已暂停");
        }
    }

    /**
     * 恢复图片加载（滚动停止时调用）
     */
    public static void resumeLoads(Context context) {
        if (context != null) {
            Glide.with(context).resumeRequests();
            Log.d(TAG, "图片加载已恢复");
        }
    }
}
