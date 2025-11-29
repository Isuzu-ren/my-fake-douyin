package com.example.myfakedouyinapplication.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfakedouyinapplication.R;
import com.example.myfakedouyinapplication.models.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "UserAdapter";
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_LOADING = 1;
    private boolean isLoading = false;
    private boolean hasMore = true;
    private boolean isLoadMore = false;

    private List<User> userList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onUnfollowClick(int position);

        void onItemClick(int position);

        void onMoreClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public UserAdapter(List<User> userList) {
        this.userList = new ArrayList<>(userList != null ? userList : new ArrayList<>());
        Log.d(TAG, "适配器初始化，初始数据量: " + this.userList.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) {
            User user = userList.get(position);
            ((UserViewHolder) holder).bind(user, position);
        } else if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        return (userList != null ? userList.size() : 0) + (isLoadMore && hasMore ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个位置显示加载更多
        if (position >= (userList != null ? userList.size() : 0)) {
            return TYPE_LOADING;
        } else {
            return TYPE_ITEM;
        }
    }

    // 添加数据（用于分页加载）
    public void addData(List<User> newUsers) {
        if (newUsers == null || newUsers.isEmpty()) {
            return;
        }

        int startPosition = userList.size();
        // 创建独立副本以防外部修改
        List<User> usersCopy = new ArrayList<>(newUsers);
        this.userList.addAll(usersCopy);
        if (startPosition >= 0 && startPosition < getItemCount()) {
            notifyItemRangeInserted(startPosition, usersCopy.size());
            Log.d(TAG, "已通知插入范围: " + startPosition + " - " + (startPosition + usersCopy.size()));
        } else {
            Log.w(TAG, "起始位置超出范围，无法通知插入");
            notifyDataSetChanged();
        }
    }

    /**
     * 设置新数据（完全替换当前数据）
     */
    public void setData(List<User> newData) {
        Log.d(TAG, "设置新数据，当前: " + userList.size() + " 条，新数据: " +
                (newData != null ? newData.size() : "null") + " 条");

        // 创建独立副本
        List<User> dataCopy = new ArrayList<>();
        if (newData != null) {
            dataCopy.addAll(newData);
        }

        // 清空旧数据
        int oldSize = userList.size();
        userList.clear();
        if (oldSize > 0) {
            notifyItemRangeRemoved(0, oldSize);
        }

        // 添加新数据
        if (!dataCopy.isEmpty()) {
            userList.addAll(dataCopy);
            notifyItemRangeInserted(0, dataCopy.size());
        }

        Log.d(TAG, "数据设置完成，当前: " + userList.size() + " 条");

//        Log.d(TAG, "=== 适配器数据设置调试 ===");
//        Log.d(TAG, "传入数据: " + (newData != null ? newData.size() : "null") + " 条");
//        if (newData == null) {
//            newData = new ArrayList<>();
//            Log.w(TAG, "传入数据为null，使用空列表");
//        }
//        Log.d(TAG, "设置前适配器数据量: " + this.userList.size());
//        this.userList.clear();
//        this.userList.addAll(newData);
//        Log.d(TAG, "设置后适配器数据量: " + this.userList.size());
//        Log.d(TAG, "通知数据集变化");
//        notifyDataSetChanged();
//        Log.d(TAG, "getItemCount() 返回: " + getItemCount());
//        Log.d(TAG, "数据已更新，共 " + userList.size() + " 条记录");
    }

    /**
     * 清空所有数据
     */
    public void clearData() {
        int itemCount = getItemCount();
        this.userList.clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    /**
     * 获取当前数据列表（只读）
     */
    public List<User> getData() {
        return new ArrayList<>(this.userList);
    }

    /**
     * 获取指定位置的数据
     */
    public User getItem(int position) {
        if (position >= 0 && position < userList.size()) {
            return userList.get(position);
        }
        return null;
    }

    /**
     * 更新单个项目
     */
    public void updateItem(int position, User user) {
        if (position >= 0 && position < userList.size()) {
            userList.set(position, user);
            notifyItemChanged(position);
        }
    }

    /**
     * 移除单个项目
     */
    public void removeItem(int position) {
        if (position >= 0 && position < userList.size()) {
            userList.remove(position);
            notifyItemRemoved(position);
            // 通知后续项目位置变化
            notifyItemRangeChanged(position, userList.size() - position);
        }
    }

    // 设置加载状态
    public void setLoadingMore(boolean loading) {
        if (isLoadMore != loading) {
            isLoadMore = loading;
            if (loading) {
                notifyItemInserted(getItemCount() - 1);
            } else {
                notifyItemRemoved(getItemCount());
            }
        }
    }

    // 设置是否有更多数据
    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
        if (!hasMore && isLoadMore) {
            isLoadMore = false;
            notifyItemRemoved(getItemCount());
        }
    }

    // 设置全局加载状态
    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView username;
        private TextView specialBadge;
        private Button unfollowButton;
        private ImageButton MoreButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.user_avatar);
            username = itemView.findViewById(R.id.user_name);
            specialBadge = itemView.findViewById(R.id.specialBadge);
            unfollowButton = itemView.findViewById(R.id.follow_button);
            MoreButton = itemView.findViewById(R.id.moreinfo_button);
        }

        public void bind(User user, int position) {
            // 设置头像
            if (user.getAvatarResId() != 0) {
                Glide.with(itemView.getContext())
                        .load(user.getAvatarResId())
                        .apply(new RequestOptions()
                                .circleCrop()
                                .placeholder(R.drawable.avator_1)
                                .error(R.drawable.ic_warning))
                        .into(avatar);
            }

            // 设置用户名
            username.setText(user.getDisplayName());

            // 设置特别关注标识
            if (user.isSpecial()) {
                specialBadge.setVisibility(View.VISIBLE);
                specialBadge.setText(R.string.special_follow);
            } else {
                specialBadge.setVisibility(View.GONE);
            }

            // 设置关注按钮状态
            updateFollowButton(user.isFollowed());

            // 设置关注按钮点击事件
            unfollowButton.setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.onUnfollowClick(getAdapterPosition());
                }
            });

            // 设置更多按钮点击事件
            MoreButton.setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.onMoreClick(getAdapterPosition());
                }
            });

            // 设置整个item的点击事件
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }

        private void updateFollowButton(boolean isFollowed) {
            if (isFollowed) {
                unfollowButton.setText(R.string.following);
                unfollowButton.setBackgroundResource(R.drawable.btn_following);
                unfollowButton.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
            } else {
                unfollowButton.setText(R.string.follow);
                unfollowButton.setBackgroundResource(R.drawable.btn_unfollow);
                unfollowButton.setTextColor(itemView.getContext().getResources().getColor(android.R.color.white));
            }
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind() {
            // No specific binding needed for loading view
        }
    }
}
