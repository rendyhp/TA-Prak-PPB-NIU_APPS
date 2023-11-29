package com.example.ta_ppb_niu_apps.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ta_ppb_niu_apps.adapters.ChatAdapter;
import com.example.ta_ppb_niu_apps.databinding.ActivityUserProfileBinding;
import com.example.ta_ppb_niu_apps.models.ChatMessage;
import com.example.ta_ppb_niu_apps.models.User;
import com.example.ta_ppb_niu_apps.utilities.Constants;
import com.example.ta_ppb_niu_apps.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;
    private PreferenceManager preferenceManager;
    private ChatActivity chatActivity;
    private String encodedImage;
    private FirebaseFirestore database;
    private User receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private String conversionId = null;
    private Boolean isReceiverAvailable = false;

    private String receiverUserId;


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
        binding.layoutImage.setOnClickListener(view -> {
            binding.showImage.setVisibility(View.VISIBLE);

            Intent intent = getIntent();
            receiverUser = (User) intent.getSerializableExtra(Constants.KEY_USER);
            byte[] bytes = Base64.decode(receiverUser.image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.showImageProfile.setImageBitmap(bitmap);
        });
        binding.showImage.setOnClickListener(view -> {
            binding.showImage.setVisibility(View.GONE);
        });
    }

    private void loadReceiverDetails() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.KEY_USER)) {
            receiverUser = (User) intent.getSerializableExtra(Constants.KEY_USER);

            binding.textEmail.setText(receiverUser.email);
            // Gunakan informasi dari receiverUser untuk menampilkan detail pengguna
            byte[] bytes = Base64.decode(receiverUser.image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.imageProfile.setImageBitmap(bitmap);
            binding.textNama.setText(receiverUser.name);
            if (receiverUser.info != null){
                binding.textInfo.setText(receiverUser.info);
            } else {
                binding.textInfo.setText("Blm ada info");
            }


            binding.textNama.setVisibility(View.VISIBLE);
            binding.textEmail.setVisibility(View.VISIBLE);
            binding.textInfo.setVisibility(View.VISIBLE);
        }
    }

}
