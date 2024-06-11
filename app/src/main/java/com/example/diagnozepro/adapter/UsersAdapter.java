package com.example.diagnozepro.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.diagnozepro.ChatActivity;
import com.example.diagnozepro.R;
import com.example.diagnozepro.model.UserModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.Vh> {
    List<UserModel> list;
    Context context;

    public UsersAdapter(List<UserModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_users, parent, false);
        return new Vh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, @SuppressLint("RecyclerView") int position) {
        UserModel model = list.get(position);
        holder.user_username.setText(model.getName());
        if (!model.getImage().isEmpty()) {
            Glide.with(context).load(model.getImage()).into(holder.user_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userId", model.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Vh extends RecyclerView.ViewHolder {
        CircleImageView user_image;
        TextView user_username;

        public Vh(@NonNull View itemView) {
            super(itemView);
            user_image = itemView.findViewById(R.id.user_image);
            user_username = itemView.findViewById(R.id.user_username);
        }
    }
}
