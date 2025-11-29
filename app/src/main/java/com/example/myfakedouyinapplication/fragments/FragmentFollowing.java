package com.example.myfakedouyinapplication.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myfakedouyinapplication.R;
import com.example.myfakedouyinapplication.adapters.UserAdapter;
import com.example.myfakedouyinapplication.constants.MessageConstants;
import com.example.myfakedouyinapplication.core.ThreadManager;
import com.example.myfakedouyinapplication.dialogs.UserActionsDialog;
import com.example.myfakedouyinapplication.models.User;

import java.util.ArrayList;
import java.util.List;

public class FragmentFollowing extends Fragment {
    private static final String TAG = "FragmentFollowing";

    // UI组件
    private RecyclerView userRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView followCountText;
    private TextView emptyStateText;

    // 数据相关
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private ThreadManager threadManager;

    // 分页状态
    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasMoreData = true;
    private boolean isRefreshing = false;

    // 添加滚动位置状态保存
    private int savedScrollPosition = 0;
    private Parcelable savedRecyclerViewState = null;
    private static final String SCROLL_POSITION_KEY = "saved_scroll_position";

    public FragmentFollowing() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);

        // 初始化线程管理器
        initializeThreadManager();

        // 初始化UI组件
        initViews(view);

        // 设置RecyclerView
        setupRecyclerView();

        // 设置下拉刷新
        setupSwipeRefresh();

        // 加载初始数据
        loadInitialData();

        return view;
    }

    /**
     * 初始化线程管理器
     */
    private void initializeThreadManager() {
        if (threadManager == null) {
            threadManager = ThreadManager.getInstance();
        }
        threadManager.initialize(requireContext(), this);
        Log.d(TAG, "线程管理器已初始化");
    }

    private void initViews(View view) {
        userRecyclerView = view.findViewById(R.id.following_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.following_swipe_refresh);
        followCountText = view.findViewById(R.id.following_count_text);
        emptyStateText = view.findViewById(R.id.empty_state_text);

        // 初始隐藏空状态提示
        emptyStateText.setVisibility(View.GONE);
    }

    /**
     * 设置RecyclerView
     */
    private void setupRecyclerView() {
        Log.d(TAG, "设置RecyclerView");
        // 设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        userRecyclerView.setLayoutManager(layoutManager);

        // 创建适配器
        if (userAdapter == null) {
            userAdapter = new UserAdapter(userList);
            Log.d(TAG, "创建新的用户适配器");
        }
        userRecyclerView.setAdapter(userAdapter);

        // 设置滚动监听，实现分页加载
        userRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isLoading && hasMoreData && dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    Log.d(TAG, String.format("滚动检测: 可见%d/%d, 首项%d, 末项%d",
                            visibleItemCount, totalItemCount, firstVisibleItemPosition, lastVisibleItemPosition));

                    boolean isNearBottom = (totalItemCount - lastVisibleItemPosition) <= 3;
                    boolean hasEnoughItems = totalItemCount >= MessageConstants.PAGE_SIZE;

                    if (isNearBottom && hasEnoughItems) {
                        Log.d(TAG, "触发加载更多，当前页码: " + currentPage);
                        loadMoreData();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    checkAndLoadMore();
                }
            }
        });

        // 设置适配器点击事件
        userAdapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onUnfollowClick(int position) {
                handleUnfollowClick(position);
            }

            @Override
            public void onItemClick(int position) {
                handleItemClick(position);
            }

            @Override
            public void onMoreClick(int position) {
                handleMoreClick(position);
            }
        });
    }

    /**
     * 检查是否需要加载更多数据
     */
    private void checkAndLoadMore() {
        if (isLoading || !hasMoreData) {
            return;
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager) userRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

        boolean shouldLoadMore = (totalItemCount - lastVisibleItemPosition) <= 3 && totalItemCount > 0;

        if (shouldLoadMore) {
            Log.d(TAG, "检测到接近底部，触发加载更多");
            loadMoreData();
        }
    }

    /**
     * 设置下拉刷新
     */
    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
    }

    /**
     * 加载初始数据
     */
    private void loadInitialData() {
        Log.d(TAG, "加载初始数据");
        showLoading(true);
        currentPage = 0;
        hasMoreData = true;
        threadManager.loadMoreUsers(currentPage);
    }

    /**
     * 加载更多数据
     */
    private void loadMoreData() {
        if (isLoading || !hasMoreData) {
            return;
        }

        Log.d(TAG, "加载更多数据，页码: " + (currentPage + 1));
        isLoading = true;
        threadManager.loadMoreUsers(currentPage + 1);

        // 显示加载更多指示器（如果有的话）
        if (userAdapter != null) {
            userAdapter.setLoadingMore(true);
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (isRefreshing) {
            return;
        }

        Log.d(TAG, "刷新数据");
        isRefreshing = true;
        currentPage = 0;
        hasMoreData = true;
        threadManager.refreshUsers();
    }

    /**
     * 处理关注/取消关注点击
     */
    private void handleUnfollowClick(int position) {
        if (position < 0 || position >= userList.size()) {
            return;
        }
        User user = userList.get(position);

        if (user.isFollowed()) {
            handleUnfollowUser(position, user);
        } else {
            handleFollowUser(position, user);
        }
    }

    /**
     * 处理取消关注操作
     */
    private void handleUnfollowUser(int position, User user) {
        Log.d(TAG, "取消关注用户： " + user.getUsername());

        // 立即更新UI（乐观更新）
        user.setFollowed(false);
        user.setSpecial(false);
        userAdapter.notifyItemChanged(position);
        updateFollowCount();

        // 发送取消关注请求
        threadManager.unfollowUser(user.getUserId());

        Toast.makeText(getContext(), "已取消关注 " + user.getUsername(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 处理关注操作
     */
    private void handleFollowUser(int position, User user) {
        Log.d(TAG, "关注用户： " + user.getUsername());

        // 立即更新UI（乐观更新）
        user.setFollowed(true);
        userAdapter.notifyItemChanged(position);
        updateFollowCount();

        // 发送关注请求
        threadManager.followUser(user.getUserId());

        Toast.makeText(getContext(), "已关注 " + user.getUsername(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 处理item点击
     */
    private void handleItemClick(int position) {
        if (position < 0 || position >= userList.size()) {
            return;
        }
        User user = userList.get(position);
        Toast.makeText(getContext(), "已选中 " + user.getUsername(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 处理更多按钮点击
     */
    private void handleMoreClick(int position) {
        if (position < 0 || position >= userList.size()) {
            return;
        }

        User user = userList.get(position);
        Toast.makeText(getContext(), "更多操作: " + user.getUsername(), Toast.LENGTH_SHORT).show();

        // TODO: 这里可以打开用户操作弹窗
    }

    /**
     * 处理主线程消息（由MainThreadHandler调用）
     */
    public void handleMainThreadMessage(Message msg) {
        if (getActivity() == null || isDetached()) {
            Log.w(TAG, "Fragment未附加，无法处理消息");
            return;
        }

        switch (msg.what) {
            case MessageConstants.MSG_LOAD_USERS_SUCCESS:
                handleLoadUsersSuccess(msg);
                break;
            case MessageConstants.MSG_LOAD_USERS_FAILED:
                handleLoadUsersFailed(msg);
                break;
            case MessageConstants.MSG_SHOW_LOADING:
                handleShowLoading(true);
                break;
            case MessageConstants.MSG_HIDE_LOADING:
                handleShowLoading(false);
                break;
            default:
                Log.w(TAG, "未知消息类型: " + msg.what);
                break;
        }
    }

    /**
     * 处理加载用户数据成功
     */
    private void handleLoadUsersSuccess(Message msg) {
        List<User> newUsers = (List<User>) msg.obj;
        boolean hasMore = msg.arg1 == 1;
        int page = msg.arg2;

        Log.d(TAG, "加载用户数据成功，页码: " + page + "，用户数: " + (newUsers != null ? newUsers.size() : 0));

        isLoading = false;
        isRefreshing = false;

        currentPage = page;
        hasMoreData = hasMore;

        if (newUsers != null && !newUsers.isEmpty()) {
            Log.d(TAG, "更新当前页码为: " + currentPage);
//            Log.d(TAG, "开始处理 " + newUsers.size() + " 条用户数据");
            if (page == 0) {
                // 刷新或第一页：替换所有数据
                userList.clear();
                // Log.d(TAG, "清空后用户列表大小: " + userList.size());
                userList.addAll(newUsers);
                // Log.d(TAG, "添加新数据后用户列表大小: " + userList.size());
                if (userAdapter != null) {
                    userAdapter.setData(userList);
                    // Log.d(TAG, "调用setData后，适配器数据量应更新");
                } else {
                    Log.e(TAG, "用户适配器为null，无法设置数据");
                }
                Log.d(TAG, "刷新完成，共 " + userList.size() + " 条数据");
            } else {
                // 加载更多：追加数据
                userList.addAll(newUsers);
                userAdapter.addData(newUsers);
                Log.d(TAG, "加载更多完成，共 " + userList.size() + " 条数据");
            }

            userAdapter.setHasMore(hasMoreData);
            userAdapter.setLoadingMore(false);

            // 更新UI状态
            updateFollowCount();
            updateEmptyState();

            // 显示成功提示
            if (page == 0) {
                Toast.makeText(getContext(), "关注列表已刷新, 共加载 " + newUsers.size() + " 位用户", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "加载更多关注用户成功, 共加载 " + newUsers.size() + " 位用户", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 没有数据
            Log.w(TAG, "没有加载到用户数据");
            hasMoreData = false;
            userAdapter.setHasMore(false);
            userAdapter.setLoadingMore(false);

            if (page == 0) {
                // 刷新或第一页且无数据
                updateEmptyState();
                Toast.makeText(getContext(), "暂无关注用户", Toast.LENGTH_SHORT).show();
            } else {
                // 加载更多但无新数据
                Toast.makeText(getContext(), "没有更多关注用户可加载", Toast.LENGTH_SHORT).show();
            }
        }

        // 隐藏加载状态
        handleShowLoading(false);
        swipeRefreshLayout.setRefreshing(false);
        if (userAdapter != null) {
            userAdapter.setLoadingMore(false);
            userAdapter.setHasMore(hasMoreData);
        }

        // 恢复滚动位置
        // onDataLoadedRestorePosition();
    }

    /**
     * 处理加载用户数据失败
     */
    private void handleLoadUsersFailed(Message msg) {
        String error = msg.obj != null ? (String) msg.obj : "未知错误";
        Log.e(TAG, "加载用户数据失败: " + error);
        isLoading = false;
        isRefreshing = false;

        // 隐藏加载状态
        handleShowLoading(false);
        swipeRefreshLayout.setRefreshing(false);
        userAdapter.setLoadingMore(false);

        // 显示错误提示
        showToast("加载关注列表失败: " + error);

        // 更新空状态
        if (currentPage == 0 && userList.isEmpty()) {
            updateEmptyState();
        }
    }

    /**
     * 更新关注人数显示
     */
    private void updateFollowCount() {
        if (followCountText != null) {
            int followedCount = 0;
            for (User user : userList) {
                if (user.isFollowed()) {
                    followedCount++;
                }
            }
            String text = "我的关注（" + followedCount + "人）";
            followCountText.setText(text);
        }
    }

    /**
     * 更新空状态显示
     */
    private void updateEmptyState() {
        if (emptyStateText != null) {
            if (userList.isEmpty()) {
                emptyStateText.setVisibility(View.VISIBLE);
                emptyStateText.setText("暂无关注用户");
            } else {
                emptyStateText.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 更新UI
     */
    public void updateUI() {
        if (userAdapter != null) {
            userAdapter.notifyDataSetChanged();
        }
        updateFollowCount();
        updateEmptyState();
    }

    /**
     * 处理显示/隐藏加载状态
     */
    private void handleShowLoading(boolean show) {
        if (swipeRefreshLayout != null) {
            // 只有在非刷新状态下才控制下拉刷新的显示
            if (!isRefreshing) {
                swipeRefreshLayout.setRefreshing(show);
            }
        }

        if (userAdapter != null) {
            userAdapter.setLoading(show);
        }
    }

    /**
     * 显示加载状态
     */
    private void showLoading(boolean show) {
        handleShowLoading(show);
    }

    /**
     * 设置加载状态
     */
    public void setLoadingState(boolean loading) {
        handleShowLoading(loading);
    }

    /**
     * 用户数据加载完成回调（供外部调用）
     */
    public void onUsersLoaded(List<User> users, boolean hasMore, int page) {
        // 这个方法现在通过handleMainThreadMessage统一处理
        Log.d(TAG, "onUsersLoaded被调用，但建议使用handleMainThreadMessage");
    }

    /**
     * 加载失败回调
     */
    public void onLoadFailed(String error) {
        // 这个方法现在通过handleMainThreadMessage统一处理
        Log.d(TAG, "onLoadFailed被调用，但建议使用handleMainThreadMessage");
    }

    /**
     * 显示Toast
     */
    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "Fragment销毁，释放资源");

        if (threadManager != null) {
            threadManager.shutdown();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.d(TAG, "Fragment暂停，保存滚动位置");
//        saveScrollPosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Fragment恢复，刷新数据");

        if (userList.isEmpty()) {
            loadInitialData();
        } else {
            refreshData();
        }

        // 恢复滚动位置
        // restoreScrollPosition();
    }

    // 滚动位置保持相关函数

//    /**
//     * 保存滚动位置
//     */
//    private void saveScrollPosition() {
//        if (userRecyclerView != null) {
//            LinearLayoutManager layoutManager = (LinearLayoutManager) userRecyclerView.getLayoutManager();
//            if (layoutManager != null) {
//                savedScrollPosition = layoutManager.findFirstVisibleItemPosition();
//                savedRecyclerViewState = layoutManager.onSaveInstanceState();
//                Log.d(TAG, "保存滚动位置: " + savedScrollPosition);
//            }
//        }
//    }
//
//    /**
//     * 获取当前滚动位置
//     */
//    private int getCurrentScrollPosition() {
//        if (userRecyclerView != null) {
//            LinearLayoutManager layoutManager = (LinearLayoutManager) userRecyclerView.getLayoutManager();
//            if (layoutManager != null) {
//                return layoutManager.findFirstVisibleItemPosition();
//            }
//        }
//        return 0;
//    }
//
//    /**
//     * 滚动到指定位置
//     */
//    private void scrollToPosition(int position) {
//        if (userRecyclerView != null) {
//            userRecyclerView.scrollToPosition(position);
//            Log.d(TAG, "滚动到位置: " + position);
//        }
//    }
//
//    /**
//     * 恢复滚动位置
//     */
//    private void restoreScrollPosition() {
//        if (userRecyclerView != null && savedRecyclerViewState != null) {
//            LinearLayoutManager layoutManager = (LinearLayoutManager) userRecyclerView.getLayoutManager();
//            if (layoutManager != null) {
//                // 延迟恢复，确保布局已完成
//                Log.d(TAG, "恢复滚动位置: " + savedScrollPosition);
//                userRecyclerView.post(() -> {
//                    layoutManager.onRestoreInstanceState(savedRecyclerViewState);
//                    Log.d(TAG, "滚动位置已恢复");
//                });
//                savedRecyclerViewState = null; // 清除状态，避免重复恢复
//            }
//        }
//    }
//
//    /**
//     * 在数据加载完成后恢复位置
//     */
//    private void onDataLoadedRestorePosition() {
//        restoreScrollPosition();
//    }


//    private void showUserActionsDialog(int position) {
//        if (position < 0 || position >= userList.size()) {
//            return;
//        }
//        User user = userList.get(position);
//        UserActionsDialog dialog = new UserActionsDialog(getContext(), user,
//                new UserActionsDialog.OnUserActionListener() {
//                    @Override
//                    public void onSpecialChanged(String userId, boolean isSpecial) {
//                        updateUserSpecial(userId, isSpecial);
//                    }
//
//                    @Override
//                    public void onNoteChanged(String userId, String note) {
//                        updateUserNote(userId, note);
//                    }
//
//                    @Override
//                    public void onUnfollow(String userId) {
//                        updateUserUnfollow(userId);
//                    }
//                }
//        );
//
//        dialog.show();
//    }
//
//    private void updateUserSpecial(String userId, boolean isSpecial) {
//        for (int i = 0; i < userList.size(); i++) {
//            User user = userList.get(i);
//            if (user.getUserId().equals(userId)) {
//                user.setSpecial(isSpecial);
//                userDataManager.updateUser(user);
//                if (userAdapter != null) {
//                    userAdapter.notifyItemChanged(i);
//                }
//                break;
//            }
//        }
//    }
//
//    private void updateUserNote(String userId, String note) {
//        for (int i = 0; i < userList.size(); i++) {
//            User user = userList.get(i);
//            if (user.getUserId().equals(userId)) {
//                user.setNote(note);
//                userDataManager.updateUser(user);
//                if (userAdapter != null) {
//                    userAdapter.notifyItemChanged(i);
//                }
//                break;
//            }
//        }
//    }
//
//    private void updateUserUnfollow(String userId) {
//        for (int i = 0; i < userList.size(); i++) {
//            User user = userList.get(i);
//            if (user.getUserId().equals(userId)) {
//                user.setFollowed(false);
//                user.setSpecial(false);
//                userDataManager.updateUser(user);
//                if (userAdapter != null) {
//                    userAdapter.notifyItemChanged(i);
//                }
//                updateFollowingCount();
//                break;
//            }
//        }
//    }
//
//    private void showToast(final String message) {
//        if (getContext() == null) {
//            return;
//        }
//        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
//    }
}
