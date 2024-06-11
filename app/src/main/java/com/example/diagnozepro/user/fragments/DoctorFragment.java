package com.example.diagnozepro.user.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.diagnozepro.R;
import com.example.diagnozepro.adapter.DoctorsAdapter;
import com.example.diagnozepro.databinding.FragmentDoctorBinding;
import com.example.diagnozepro.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DoctorFragment extends Fragment {
    FragmentDoctorBinding binding;
    DoctorsAdapter doctorsAdapter;
    List<UserModel> listOfDoctors = new ArrayList<>();
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers;

    public DoctorFragment() {
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
        binding = FragmentDoctorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");

    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.show();
        dbRefUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listOfDoctors.clear();
                    progressDialog.dismiss();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            UserModel model = ds.getValue(UserModel.class);
                            if (Objects.equals(model.getType(), "Doctor")){
                                listOfDoctors.add(model);
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    setAdapter();

                    if (listOfDoctors.isEmpty()) {
                        binding.tvNoGoalFound.setVisibility(View.VISIBLE);
                        binding.rvGoals.setVisibility(View.GONE);
                    } else {
                        binding.tvNoGoalFound.setVisibility(View.GONE);
                        binding.rvGoals.setVisibility(View.VISIBLE);
                    }
                } else {
                    progressDialog.dismiss();
                    binding.tvNoGoalFound.setVisibility(View.VISIBLE);
                    binding.rvGoals.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setAdapter() {
        doctorsAdapter = new DoctorsAdapter(listOfDoctors, requireContext());
        binding.rvGoals.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvGoals.setAdapter(doctorsAdapter);
    }

}