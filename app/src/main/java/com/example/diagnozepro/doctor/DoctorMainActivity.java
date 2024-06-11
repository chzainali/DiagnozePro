package com.example.diagnozepro.doctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.View;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.diagnozepro.R;
import com.example.diagnozepro.databinding.ActivityDoctorMainBinding;
import com.example.diagnozepro.databinding.ActivityMainBinding;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class DoctorMainActivity extends AppCompatActivity {
    ActivityDoctorMainBinding binding;
    NavHostFragment navHostFragment;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        setBottomNavigation();
    }

    private void setBottomNavigation() {

        // Add navigation items to the bottom navigation bar
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_booking_24));
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.iv_chat));
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_baseline_person_24));

        navController.navigate(R.id.appointmentsFragment2);
        binding.bottomNav.bottomNavigation.show(1, true);

        binding.bottomNav.bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {

                switch (model.getId()) {
                    case 1:
                        navController.navigate(R.id.appointmentsFragment2);
                        break;

                    case 2:
                        navController.navigate(R.id.doctorChatsFragment2);
                        break;

                    case 3:
                        navController.navigate(R.id.userProfileFragment2);
                        break;
                }

                return null;
            }
        });

        binding.bottomNav.appointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomNav.bottomNavigation.show(1, true);
                navController.navigate(R.id.appointmentsFragment2);
            }
        });

        binding.bottomNav.chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomNav.bottomNavigation.show(2, true);
                navController.navigate(R.id.doctorChatsFragment2);
            }
        });

        binding.bottomNav.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomNav.bottomNavigation.show(3, true);
                navController.navigate(R.id.userProfileFragment2);
            }
        });
    }

}