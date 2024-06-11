package com.example.diagnozepro.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.diagnozepro.R;
import com.example.diagnozepro.model.UserModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.ViewHolder> {
    List<UserModel> list;
    Context context;

    public DoctorsAdapter(List<UserModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    //Updates the list of doctors.
    public void setList(List<UserModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctors, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UserModel doctorsModel = list.get(position);
        holder.tvName.setText(doctorsModel.getName());
        holder.tvExpertise.setText(doctorsModel.getExpertise());
        holder.tvTime.setText(doctorsModel.getAvailability());
        Glide.with(context)
                .asBitmap()
                .load(Uri.parse(doctorsModel.getImage()))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.ivImage.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

        // Set a click listener for the "View" button
        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", doctorsModel);
                Navigation.findNavController(v).navigate(R.id.action_doctorFragment_to_doctorDetailsFragment, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

   //ViewHolder class for holding the views of each item in the RecyclerView.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvExpertise, tvTime;
        AppCompatButton btnView;
        CircleImageView ivImage;

        //The view of the item in the RecyclerView
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvExpertise = itemView.findViewById(R.id.tv_expertise);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnView = itemView.findViewById(R.id.btnView);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }
}
