package com.example.diagnozepro.user.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.diagnozepro.R;
import com.example.diagnozepro.auth.LoginActivity;
import com.example.diagnozepro.databinding.FragmentUserProfileBinding;
import com.example.diagnozepro.model.HelperClass;

import java.util.Objects;

public class UserProfileFragment extends Fragment {
    FragmentUserProfileBinding binding;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Objects.equals(HelperClass.users.getType(), "User")){
            binding.tvLicenseCode.setVisibility(View.GONE);
            binding.tvExpertise.setVisibility(View.GONE);
            binding.tvAvailability.setVisibility(View.GONE);
            binding.tvLocation.setVisibility(View.GONE);
            binding.tvDetails.setVisibility(View.GONE);
        }

        binding.tvName.setText(HelperClass.users.getName());
        binding.tvPhone.setText(HelperClass.users.getPhone());
        binding.tvEmail.setText(HelperClass.users.getEmail());
        binding.tvLicenseCode.setText(HelperClass.users.getLicenseCode());
        binding.tvExpertise.setText(HelperClass.users.getExpertise());
        binding.tvAvailability.setText(HelperClass.users.getAvailability());
        binding.tvLocation.setText(HelperClass.users.getLocation());
        binding.tvDetails.setText(HelperClass.users.getDetails());

        if (!HelperClass.users.getImage().isEmpty()) {
            Glide.with(binding.ivImage).load(HelperClass.users.getImage()).into(binding.ivImage);
        }

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
                requireActivity().finishAffinity();
            }
        });


    }
}