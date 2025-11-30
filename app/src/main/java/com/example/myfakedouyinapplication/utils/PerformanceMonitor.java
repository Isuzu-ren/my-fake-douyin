package com.example.myfakedouyinapplication.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PerformanceMonitor {
    private static final String TAG = "SimpleFpsMonitor";

    private long lastTime = 0;
    private int frameCount = 0;
    private double currentFps = 60.0;
    private boolean isMonitoring = false;

    // 回调接口
    public interface FpsListener {
        void onFpsUpdate(double fps);

        void onLowFps(double fps); // FPS低于55时触发
    }

    private FpsListener listener;

    /**
     * 开始监控
     */
    public void startMonitoring(RecyclerView recyclerView, FpsListener listener) {
        this.listener = listener;
        this.isMonitoring = true;
        this.lastTime = System.currentTimeMillis();
        this.frameCount = 0;

        if (recyclerView != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    recordFrame();
                }
            });
        }

        Log.d(TAG, "FPS监控已启动");
    }

    /**
     * 记录帧（核心计算逻辑）
     */
    private void recordFrame() {
        if (!isMonitoring) return;

        frameCount++;
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastTime;

        if (elapsedTime >= 1000) {
            currentFps = (frameCount * 1000.0) / elapsedTime;
            Log.d(TAG, "当前FPS: " + currentFps);

            // 性能警告
            if (currentFps < 55.0) {
                Log.w(TAG, "性能警告: 当前FPS低于55! 当前FPS: " + currentFps);
                if (listener != null) {
                    listener.onLowFps(currentFps);
                }
            } else if (currentFps >= 59.0) {
                Log.d(TAG, "性能良好: 当前FPS稳定在60以上.");
            }

            if (listener != null) {
                listener.onFpsUpdate(currentFps);
            }

            frameCount = 0;
            lastTime = currentTime;
        }
    }

    /**
     * 停止监控
     */
    public void stop() {
        this.isMonitoring = false;
        Log.d(TAG, "FPS监控已停止");
    }

    /**
     * 获取当前FPS
     */
    public double getCurrentFps() {
        return currentFps;
    }
}
