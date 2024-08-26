package com.pherom.easysaleassignment.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pherom.easysaleassignment.R;
import com.pherom.easysaleassignment.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.UserViewHolder>{

    Context context;
    List<User> users = new ArrayList<>();

    public UserRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserRecyclerViewAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_recycler_view_row, parent, false);
        return new UserRecyclerViewAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRecyclerViewAdapter.UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.getNameView().setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        holder.getEmailView().setText(user.getEmail());
        String avatar = user.getAvatar();
        if (URLUtil.isNetworkUrl(avatar) || URLUtil.isContentUrl(avatar)) {
            Picasso.get().load(user.getAvatar()).into(holder.getAvatarView());
        }
        else {
            Uri avatarUri = Uri.parse(avatar);
            holder.getAvatarView().setImageURI(avatarUri);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarView;
        private TextView nameView;
        private TextView emailView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            avatarView = itemView.findViewById(R.id.avatarView);
            nameView = itemView.findViewById(R.id.nameView);
            emailView = itemView.findViewById(R.id.emailView);
        }

        public ImageView getAvatarView() {
            return avatarView;
        }

        public TextView getNameView() {
            return nameView;
        }

        public TextView getEmailView() {
            return emailView;
        }
    }
}
