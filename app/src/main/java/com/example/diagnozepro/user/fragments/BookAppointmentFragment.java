package com.example.diagnozepro.user.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.diagnozepro.R;
import com.example.diagnozepro.databinding.FragmentAppointmentsBinding;
import com.example.diagnozepro.databinding.FragmentBookAppointmentBinding;
import com.example.diagnozepro.model.AppointmentsModel;
import com.example.diagnozepro.model.HelperClass;
import com.example.diagnozepro.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class BookAppointmentFragment extends Fragment {
    FragmentBookAppointmentBinding binding;
    String title, dateTime, details;
    UserModel doctorsModel;
    private int mYear, mMonth, mDay, mHour, mMinute;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefAppointments;

    public BookAppointmentFragment() {
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
        binding = FragmentBookAppointmentBinding.inflate(getLayoutInflater());
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
        dbRefAppointments = FirebaseDatabase.getInstance().getReference("Appointments");
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });

        binding.etDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(binding.etDateTime);
            }
        });

        binding.btnBookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = binding.etTitle.getText().toString().trim();
                dateTime = binding.etDateTime.getText().toString().trim();
                details = binding.etDetails.getText().toString().trim();

                if (title.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter title", Toast.LENGTH_SHORT).show();
                } else if (dateTime.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select date & time", Toast.LENGTH_SHORT).show();
                } else if (details.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter details", Toast.LENGTH_SHORT).show();
                } else {
                    String pushId = dbRefAppointments.push().getKey();
                    AppointmentsModel model = new AppointmentsModel(pushId, HelperClass.users.getId(), doctorsModel.getId(), HelperClass.users.getName(), doctorsModel.getName(), HelperClass.users.getPhone(), title, dateTime, details, "Pending");
                    dbRefAppointments.child(pushId).setValue(model).addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), "Successfully Requested for Appointment", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigateUp();
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void showDatePicker(TextView textView) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mDay = dayOfMonth;
                mMonth = monthOfYear + 1;
                mYear = year;
                showTimePicker(textView);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void showTimePicker(final TextView textView) {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
                // Format the date and time
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, mYear);
                calendar.set(Calendar.MONTH, mMonth - 1); // Months are zero-based
                calendar.set(Calendar.DAY_OF_MONTH, mDay);
                calendar.set(Calendar.HOUR_OF_DAY, mHour);
                calendar.set(Calendar.MINUTE, mMinute);
                String formattedDateTime = dateFormat.format(calendar.getTime());
                textView.setText(formattedDateTime);
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }

}