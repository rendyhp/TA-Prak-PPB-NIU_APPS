package com.example.ta_ppb_niu_apps.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ta_ppb_niu_apps.databinding.ActivityUserProfileBinding;
import com.example.ta_ppb_niu_apps.models.User;
import com.example.ta_ppb_niu_apps.utilities.Constants;
import com.example.ta_ppb_niu_apps.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;
    private PreferenceManager preferenceManager;
    private ChatActivity chatActivity;
    private String encodedImage;
    private FirebaseFirestore database;

    private User receiverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadReceiverDetails();
        setListeners();
    }



    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void loadReceiverDetails() {
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_RECEIVER_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
        binding.textNama.setVisibility(View.VISIBLE);
        binding.textEmail.setVisibility(View.VISIBLE);
        binding.textInfo.setVisibility(View.VISIBLE);
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 500;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }




}
