package com.example.diagnozepro.auth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diagnozepro.R;
import com.example.diagnozepro.RegisterActivity;
import com.example.diagnozepro.databinding.ActivityLoginBinding;
import com.example.diagnozepro.doctor.DoctorMainActivity;
import com.example.diagnozepro.model.HelperClass;
import com.example.diagnozepro.model.UserModel;
import com.example.diagnozepro.user.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    String email, password;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers;
    String userType = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.tvRegister.setOnClickListener(view ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");

        binding.tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userType = "User";
                userTypeSelection();
            }
        });

        binding.tvDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userType = "Doctor";
                userTypeSelection();
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the input fields are validated
                if (isValidated()) {
                    // Show a progress dialog to indicate login process
                    progressDialog.show();

                    // Sign in with email and password using FirebaseAuth
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                dbRefUsers.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            UserModel model = snapshot.getValue(UserModel.class);
                                            HelperClass.users = model;
                                            if (Objects.equals(model.getType(), "User") && userType.equals("User")) {
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                finishAffinity();
                                            } else  if (Objects.equals(model.getType(), "Doctor") && userType.equals("Doctor")){
                                                startActivity(new Intent(LoginActivity.this, DoctorMainActivity.class));
                                                finishAffinity();
                                            }else{
                                                Toast.makeText(LoginActivity.this, "Not exists as a "+userType, Toast.LENGTH_SHORT).show();
                                            }
                                            progressDialog.dismiss();
                                        } else {
                                            progressDialog.dismiss();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // If there's an error retrieving user data
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // If sign-in fails due to an exception
                            showMessage(e.getMessage());
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });

    }

    private void userTypeSelection() {

        if (Objects.equals(userType, "User")){
            binding.tvUser.setBackgroundColor(getColor(R.color.main));
            binding.tvUser.setTextColor(getColor(R.color.white));
            binding.tvDoctor.setBackgroundColor(getColor(R.color.white));
            binding.tvDoctor.setTextColor(getColor(R.color.main));
        }else{
            binding.tvUser.setBackgroundColor(getColor(R.color.white));
            binding.tvUser.setTextColor(getColor(R.color.main));
            binding.tvDoctor.setBackgroundColor(getColor(R.color.main));
            binding.tvDoctor.setTextColor(getColor(R.color.white));
        }

    }

    private Boolean isValidated() {
        email = binding.emailEt.getText().toString().trim();
        password = binding.passET.getText().toString().trim();

        if (email.isEmpty()) {
            showMessage("Please enter email");
            return false;
        }
        if (!(Patterns.EMAIL_ADDRESS).matcher(email).matches()) {
            showMessage("Please enter email in correct format");
            return false;
        }
        if (password.isEmpty()) {
            showMessage("Please enter password");
            return false;
        }

        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userTypeSelection();
    }
}