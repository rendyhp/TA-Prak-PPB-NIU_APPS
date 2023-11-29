package com.example.ta_ppb_niu_apps.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.ta_ppb_niu_apps.R;
import com.example.ta_ppb_niu_apps.activities.ChatActivity;
import com.example.ta_ppb_niu_apps.activities.ProfileSettingActivity;
import com.example.ta_ppb_niu_apps.activities.UserProfileActivity;
import com.example.ta_ppb_niu_apps.activities.UsersActivity;
import com.example.ta_ppb_niu_apps.adapters.ImageAdapter;
import com.example.ta_ppb_niu_apps.adapters.RecentConversationsAdapter;
import com.example.ta_ppb_niu_apps.databinding.FragmentHomeBinding;
import com.example.ta_ppb_niu_apps.listeners.ConversionListener;
import com.example.ta_ppb_niu_apps.models.ChatMessage;
import com.example.ta_ppb_niu_apps.models.User;
import com.example.ta_ppb_niu_apps.utilities.Constants;
import com.example.ta_ppb_niu_apps.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements ConversionListener {

    private FragmentHomeBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;

    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;
    private ListenerRegistration conversationListenerRegistration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferenceManager = new PreferenceManager(requireContext());

        ViewPager mViewPager = binding.imageSlider;
        ImageAdapter adapterView = new ImageAdapter(requireContext());
        mViewPager.setAdapter(adapterView);

        // Start auto image sliding with a delay of 2000 milliseconds (2 seconds)
        adapterView.startAutoSlider(mViewPager, 10000);

        loadUserDetails();
        setListeners();
        init();
        listenConversation();
    }

    private void setListeners() {
        binding.imagesetting.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ProfileSettingActivity.class)));
    }

    private void loadUserDetails() {
        binding.textNama.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.textEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        if(preferenceManager.getString(Constants.KEY_USER_INFO) != null) {
            binding.textInfo.setText(preferenceManager.getString(Constants.KEY_USER_INFO));
        } else {
            binding.textInfo.setText("Ada");
        }

        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);

    }


    private void init() {
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations, this);
        binding.conversationsRecyclerView.setAdapter(conversationsAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void listenConversation() {
        conversationListenerRegistration = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

        conversationListenerRegistration = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if(value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receiverId = receiverId;
                    if(preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                        chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    } else {
                        chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversations.add(chatMessage);
                } else if(documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversations.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if(conversations.get(i).senderId.equals(senderId) && conversations.get(i).receiverId.equals(receiverId)){
                            conversations.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversations.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversations, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            conversationsAdapter.notifyDataSetChanged();
            binding.conversationsRecyclerView.smoothScrollToPosition(0);
            binding.conversationsRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        requireActivity().startActivity(intent);
    }
}
