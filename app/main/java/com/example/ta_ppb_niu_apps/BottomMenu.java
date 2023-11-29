package com.example.ta_ppb_niu_apps;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ta_ppb_niu_apps.databinding.ActivityBottomMenuBinding;
import com.example.ta_ppb_niu_apps.fragment.AboutFragment;
import com.example.ta_ppb_niu_apps.fragment.HomeFragment;
import com.example.ta_ppb_niu_apps.fragment.MainFragment;

import com.example.ta_ppb_niu_apps.R;

public class BottomMenu extends AppCompatActivity {

    ActivityBottomMenuBinding binding;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBottomMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new MainFragment());
        binding.bottomNavigatorView.setBackground(null);
        binding.bottomNavigatorView.getMenu().findItem(R.id.main).setChecked(true);

        binding.bottomNavigatorView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.main) {
                replaceFragment(new MainFragment());
            } else if (itemId == R.id.about) {
                replaceFragment(new AboutFragment());
            } else {
                replaceFragment(new HomeFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
