package com.example.diagnozepro.user.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.diagnozepro.R;
import com.example.diagnozepro.adapter.DoctorsAdapter;
import com.example.diagnozepro.adapter.UsersAdapter;
import com.example.diagnozepro.databinding.FragmentDoctorBinding;
import com.example.diagnozepro.databinding.FragmentDoctorChatsBinding;
import com.example.diagnozepro.model.HelperClass;
import com.example.diagnozepro.model.MessagesModel;
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

public class DoctorChatsFragment extends Fragment {
    FragmentDoctorChatsBinding binding;
    UsersAdapter usersAdapter;
    List<UserModel> listOfDoctors = new ArrayList<>();
    List<UserModel> listOfUsersOfDoctorSide = new ArrayList<>();
    List<String> listOfUserNames = new ArrayList<>();
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers, dbRefChats;

    public DoctorChatsFragment() {
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
        binding = FragmentDoctorChatsBinding.inflate(inflater, container, false);
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
        dbRefChats = FirebaseDatabase.getInstance().getReference("Chats");

    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.show();
        listOfDoctors.clear();
        listOfUsersOfDoctorSide.clear();

        if (Objects.equals(HelperClass.users.getType(), "Doctor")) {
            dbRefChats.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                MessagesModel model = ds.getValue(MessagesModel.class);
                                    if (Objects.equals(model.getReceiverId(), auth.getCurrentUser().getUid())) {
                                        if (!listOfUserNames.contains(model.getSenderId())){
                                            listOfUserNames.add(model.getSenderId());
                                        }
                                    }
                            } catch (DatabaseException e) {
                                e.printStackTrace();
                            }
                        }
                        getUsersData();
                    } else {
                        getUsersData();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            getUsersData();
        }

    }

    private void getUsersData() {
        dbRefUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    progressDialog.dismiss();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            UserModel model = ds.getValue(UserModel.class);
                            if (Objects.equals(HelperClass.users.getType(), "User")) {
                                if (Objects.equals(model.getType(), "Doctor")) {
                                    listOfDoctors.add(model);
                                }
                            } else {
                                if (listOfUserNames.size() > 0){
                                    if (listOfUserNames.contains(model.getId())) {
                                        listOfDoctors.add(model);
                                    }
                                }
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    setAdapter();

                    if (listOfDoctors.isEmpty()) {
                        binding.tvNoDataFound.setVisibility(View.VISIBLE);
                        binding.rvUsers.setVisibility(View.GONE);
                    } else {
                        binding.tvNoDataFound.setVisibility(View.GONE);
                        binding.rvUsers.setVisibility(View.VISIBLE);
                    }
                } else {
                    progressDialog.dismiss();
                    binding.tvNoDataFound.setVisibility(View.VISIBLE);
                    binding.rvUsers.setVisibility(View.GONE);
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
        usersAdapter = new UsersAdapter(listOfDoctors, requireContext());
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvUsers.setAdapter(usersAdapter);
    }

}