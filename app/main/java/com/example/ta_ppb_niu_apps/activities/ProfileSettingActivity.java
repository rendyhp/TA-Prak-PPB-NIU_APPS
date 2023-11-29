package com.example.ta_ppb_niu_apps.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.ta_ppb_niu_apps.R;
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

    private void setListeners() {

        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSetImage.setOnClickListener(v -> {
            pickImageFromGallery();
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

        binding.editAccount.setOnClickListener(v -> {
            showEditNameButtonDialog();

        });

        binding.editInfo.setOnClickListener(v -> {
            showEditInfoButtonDialog();
        });
    }

    private void setImage(String encodedImage) {
        // Update the user's image in Firestore and preferences
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_IMAGE, encodedImage);

        // Update the image in Firestore
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(user)
                .addOnSuccessListener(aVoid -> {
                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                    showToast("Image updated successfully");
                })
                .addOnFailureListener(e -> {
                    showToast("Error updating image: " + e.getMessage());
                });

        // Notify the calling activity about the updated image
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_USER, encodedImage);
        setResult(Activity.RESULT_OK, intent);
    }

    private void pickImageFromGallery() {
        // Start the image picker activity
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);
    }

    private void showImagePreview(Bitmap bitmap) {
        // Show a preview of the selected image
        binding.showImage.setVisibility(View.VISIBLE);
        binding.showImageProfile.setImageBitmap(bitmap);
    }

    private void hideImagePreview() {
        // Hide the image preview
        binding.showImage.setVisibility(View.GONE);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loadUserDetails() {
        binding.outputNama.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.outputEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));

        String userInfo = preferenceManager.getString(Constants.KEY_USER_INFO);
        if (!TextUtils.isEmpty(userInfo)) {
            binding.outputInfo.setText(userInfo);
        } else {
            binding.outputInfo.setText("Ada");
        }


        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
        binding.outputNama.setVisibility(View.VISIBLE);
        binding.outputEmail.setVisibility(View.VISIBLE);
        binding.outputInfo.setVisibility(View.VISIBLE);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        // Decode the selected image
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        // Show the image preview
                        showImagePreview(bitmap);

                        // Encode the image and set it to Firestore
                        encodedImage = encodeImage(bitmap);
                        setImage(encodedImage);
                    } catch (FileNotFoundException e) {
                        showToast("Error picking image: " + e.getMessage());
                    }
                }
            }
    );

    private String encodeImage(Bitmap bitmap) {
        // Encode the image to a Base64 string
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void showEditNameButtonDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.editname_sheet_layout);

        EditText inputNama = dialog.findViewById(R.id.inputNama);
        AppCompatButton confirmButton = dialog.findViewById(R.id.confirmButton);
        AppCompatButton cancelButton = dialog.findViewById(R.id.cancelButton);

        inputNama.setText(preferenceManager.getString(Constants.KEY_NAME));

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = inputNama.getText().toString().trim();

                if (!newName.isEmpty()) {
                    // Lakukan sesuatu dengan newName, misalnya simpan ke Firebase Firestore
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    HashMap<String, Object> user = new HashMap<>();
                    user.put(Constants.KEY_NAME, newName);
                    database.collection(Constants.KEY_COLLECTION_USERS)
                            .document(preferenceManager.getString(Constants.KEY_USER_ID))
                            .update(user)
                            .addOnSuccessListener(aVoid -> {
                                preferenceManager.putString(Constants.KEY_NAME, newName);
                                showToast("Name updated successfully");
                            })
                            .addOnFailureListener(e -> {
                                showToast("Error updating name");
                            });
                    Intent intent = new Intent();
                    intent.putExtra(Constants.KEY_NAME, newName);
                    setResult(Activity.RESULT_OK, intent);
                    finish();

                    dialog.dismiss();
                } else {
                    showToast("Please enter a valid name");
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showEditInfoButtonDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.editinfo_sheet_layout);

        EditText inputInfo = dialog.findViewById(R.id.inputInfo);
        AppCompatButton confirmButton = dialog.findViewById(R.id.confirmButton);
        AppCompatButton cancelButton = dialog.findViewById(R.id.cancelButton);

        if (preferenceManager.getString(Constants.KEY_USER_INFO) != null) {
            inputInfo.setText(preferenceManager.getString(Constants.KEY_USER_INFO));
        } else {
            inputInfo.setText("Ada");
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newInfo = inputInfo.getText().toString().trim();

                if (!newInfo.isEmpty()) {
                    // Lakukan sesuatu dengan newName, misalnya simpan ke Firebase Firestore
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    HashMap<String, Object> user = new HashMap<>();
                    user.put(Constants.KEY_USER_INFO, newInfo);
                    database.collection(Constants.KEY_COLLECTION_USERS)
                            .document(preferenceManager.getString(Constants.KEY_USER_ID))
                            .update(user)
                            .addOnSuccessListener(aVoid -> {
                                preferenceManager.putString(Constants.KEY_USER_INFO, newInfo);
                                showToast("Info updated successfully");
                            })
                            .addOnFailureListener(e -> {
                                showToast("Error updating info");
                            });

                    Intent intent = new Intent();
                    intent.putExtra(Constants.KEY_NAME, newInfo);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    dialog.dismiss();
                } else {
                    showToast("Please enter a valid name");
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
