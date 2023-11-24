package com.example.ta_ppb_niu_apps.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ta_ppb_niu_apps.databinding.ActivityProfileSettingBinding;
import com.example.ta_ppb_niu_apps.utilities.Constants;
import com.example.ta_ppb_niu_apps.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class ProfileSettingActivity extends AppCompatActivity {

    private ActivityProfileSettingBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        setListeners();
    }


    private void setName() {
        binding.editAccount.setOnClickListener(view -> {

        });
    }
    private void setListeners() {

        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSetImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
            HashMap<String, Object> image = new HashMap<>();
            image.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            try {
                preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                loadUserDetails();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.layoutImage.setOnClickListener(view -> {
            binding.showImage.setVisibility(View.VISIBLE);
            byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.showImageProfile.setImageBitmap(bitmap);
        });
        binding.showImage.setOnClickListener(view -> {
            binding.showImage.setVisibility(View.GONE);
        });
    }

    private void setImage() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, String> user = new HashMap<>();
        user.put(Constants.KEY_IMAGE, encodedImage);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnSuccessListener(documentReference -> {
                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                });
    }

    private void loadUserDetails() {
        binding.outputNama.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.outputEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        try {
            String userInfo = preferenceManager.getString(Constants.KEY_USER_INFO);
            if (userInfo != null) {
                binding.outputInfo.setText(userInfo);
            } else {
                binding.outputInfo.setText("Ada");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
        binding.outputNama.setVisibility(View.VISIBLE);
        binding.outputEmail.setVisibility(View.VISIBLE);
        binding.outputInfo.setVisibility(View.VISIBLE);
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

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        binding.imageProfile.setImageBitmap(bitmap);
                        encodedImage = encodeImage(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
    );
}
