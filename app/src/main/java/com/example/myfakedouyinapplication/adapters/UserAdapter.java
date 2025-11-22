package com.example.myfakedouyinapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfakedouyinapplication.R;
import com.example.myfakedouyinapplication.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onUnfollowClick(int position);

        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView username;
        private TextView specialBadge;
        private Button unfollowButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.user_avatar);
            username = itemView.findViewById(R.id.user_name);
            specialBadge = itemView.findViewById(R.id.specialBadge);
            unfollowButton = itemView.findViewById(R.id.follow_button);

            unfollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onUnfollowClick(position);
                        }
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void bind(User user) {
            avatar.setImageResource(user.getAvatarResId());
            username.setText(user.getDisplayName());
            if (user.isSpecial()) {
                specialBadge.setVisibility(View.VISIBLE);
                specialBadge.setText(R.string.special_follow);
            } else {
                specialBadge.setVisibility(View.GONE);
            }
            if (user.isFollowed()) {
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

    public void updateData(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }
}
