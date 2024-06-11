package com.example.diagnozepro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.diagnozepro.databinding.ActivityRegisterBinding;
import com.example.diagnozepro.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    Animation rotate;
    String name, email, phone, password, licenseCode, expertise, availability, location, details, userType = "User", imageUri = "";
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("ProfilePictures");

        binding.tvLogin.setOnClickListener(view ->
                finish());

        binding.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), 123);
            }
        });

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

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidated()){
                    registerUser();
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
            binding.layoutLicenseCode.setVisibility(View.GONE);
            binding.layoutExpertise.setVisibility(View.GONE);
            binding.layoutTime.setVisibility(View.GONE);
            binding.layoutLocation.setVisibility(View.GONE);
            binding.layoutDetails.setVisibility(View.GONE);
        }else{
            binding.tvUser.setBackgroundColor(getColor(R.color.white));
            binding.tvUser.setTextColor(getColor(R.color.main));
            binding.tvDoctor.setBackgroundColor(getColor(R.color.main));
            binding.tvDoctor.setTextColor(getColor(R.color.white));
            binding.layoutLicenseCode.setVisibility(View.VISIBLE);
            binding.layoutExpertise.setVisibility(View.VISIBLE);
            binding.layoutTime.setVisibility(View.VISIBLE);
            binding.layoutLocation.setVisibility(View.VISIBLE);
            binding.layoutDetails.setVisibility(View.VISIBLE);
        }

    }

    private void registerUser() {
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(taskAuth -> {
           if (taskAuth.isSuccessful()){
               Uri uriImage = Uri.parse(imageUri);
               StorageReference imageRef = storageReference.child(uriImage.getLastPathSegment());
               imageRef.putFile(uriImage).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                   String downloadUri = uri.toString();
                   UserModel model;
                   if (Objects.equals(userType, "User")){
                       model = new UserModel(auth.getCurrentUser().getUid(), userType, name, email, phone, password, downloadUri, "","","","","");
                   }else{
                       model = new UserModel(auth.getCurrentUser().getUid(), userType, name, email, phone, password, downloadUri, licenseCode,expertise,availability, location,details);
                   }
                   dbRefUsers.child(auth.getCurrentUser().getUid()).setValue(model).addOnCompleteListener(task -> {
                       progressDialog.dismiss();
                       showMessage("Registered Successfully");
                       finish();
                   }).addOnFailureListener(e -> {
                       progressDialog.dismiss();
                       showMessage(e.getLocalizedMessage());
                   });
               }).addOnFailureListener(e -> {
                   progressDialog.dismiss();
                   showMessage(e.getLocalizedMessage());
               })).addOnFailureListener(e -> {
                   progressDialog.dismiss();
                   showMessage(e.getLocalizedMessage());
               });
           }else{
               progressDialog.dismiss();
               showMessage(taskAuth.getException().toString());
           }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            showMessage(e.getLocalizedMessage());
        });
    }

    private Boolean isValidated() {
        name = binding.etName.getText().toString().trim();
        email = binding.etEmail.getText().toString().trim();
        phone = binding.etPhone.getText().toString().trim();
        password = binding.etPassword.getText().toString().trim();
        licenseCode = binding.etLicenseCode.getText().toString().trim();
        expertise = binding.etExpertise.getText().toString().trim();
        availability = binding.etAvailability.getText().toString().trim();
        location = binding.etLocation.getText().toString().trim();
        details = binding.etDetails.getText().toString().trim();

        if (imageUri.isEmpty()) {
            showMessage("Please select image");
            return false;
        }
        if (name.isEmpty()) {
            showMessage("Please enter name");
            return false;
        }
        if (email.isEmpty()) {
            showMessage("Please enter email");
            return false;
        }
        if (!(Patterns.EMAIL_ADDRESS).matcher(email).matches()) {
            showMessage("Please enter email in correct format");
            return false;
        }
        if (phone.isEmpty()) {
            showMessage("Please enter phone");
            return false;
        }

        if (Objects.equals(userType, "Doctor")){
            if (licenseCode.isEmpty()) {
                showMessage("Please enter license code");
                return false;
            }
            if (expertise.isEmpty()) {
                showMessage("Please enter your expertise");
                return false;
            }
            if (availability.isEmpty()) {
                showMessage("Please enter your availability time");
                return false;
            }
            if (location.isEmpty()) {
                showMessage("Please enter your location");
                return false;
            }
            if (details.isEmpty()) {
                showMessage("Please enter details");
                return false;
            }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData().toString();
            binding.ivImage.setImageURI(data.getData());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        userTypeSelection();
    }
}