package com.example.diagnozepro.user.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.diagnozepro.ChatActivity;
import com.example.diagnozepro.R;
import com.example.diagnozepro.databinding.FragmentDoctorDetailsBinding;
import com.example.diagnozepro.model.UserModel;

public class DoctorDetailsFragment extends Fragment {
    FragmentDoctorDetailsBinding binding;
    UserModel doctorsModel;

    public DoctorDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            doctorsModel = (UserModel) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDoctorDetailsBinding.inflate(inflater);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the back button click listener to navigate up
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });

        // Populate UI with doctor's information if available
        if (doctorsModel != null){
            binding.tvName.setText("Name: "+doctorsModel.getName());
            binding.tvExpertise.setText("Expertise: "+doctorsModel.getExpertise());
            binding.tvTime.setText("Availability: "+doctorsModel.getAvailability());
            binding.tvLocation.setText("Location: "+doctorsModel.getLocation());
            binding.tvDetails.setText("Details: "+doctorsModel.getDetails());

            // Load doctor's image using Glide
            Glide.with(DoctorDetailsFragment.this)
                    .asBitmap()
                    .load(Uri.parse(doctorsModel.getImage()))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            binding.ivImage.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }

        binding.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("userId", doctorsModel.getId());
                startActivity(intent);
            }
        });

        binding.btnBookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", doctorsModel);
                Navigation.findNavController(v).navigate(R.id.action_doctorDetailsFragment_to_bookAppointmentFragment2, bundle);
            }
        });



    }

}