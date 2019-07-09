package com.example.goforlunch.controler.fragments;


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.goforlunch.R;
import com.example.goforlunch.model.Api.Firebase.MessageHelper;
import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.example.goforlunch.model.Message;
import com.example.goforlunch.model.User;
import com.example.goforlunch.views.recyclerViews.ChatAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;


public class ChatFragment extends Fragment implements ChatAdapter.Listener {
    private static final String CHAT_NAME_RESTAURANT = "restaurant";
    @BindView(R.id.chat_recycler_view)
    RecyclerView chatRecycler;
    @BindView(R.id.chat_text_view)
    TextView chatIsEmpty;
    @BindView(R.id.input_text_message)
    TextInputEditText inputTextMessage;
    @BindView(R.id.image_preview)
    ImageView imagePreview;
    private User modelCurrentUser;
    private String currentChatName;
    private ChatAdapter chatAdapter;
    private Uri uriImageSelected;

    // STATIC DATA FOR PICTURE
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    public static final int RC_CHOOSE_PHOTO = 200;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Forward result to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponse(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view;
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);
        configureRecyclerView(CHAT_NAME_RESTAURANT);
        getCurrentUserFromFirestore();
        return view;
    }

    //------------------
    //UTILS
    //------------------
    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    //-------------
    //ACTION
    //-------------

    @OnClick(R.id.chat_send_button)
    void onClickMessageSend() {
        if (!TextUtils.isEmpty(inputTextMessage.getText()) && modelCurrentUser != null) {
            //check if the imageView is set
            if (this.imagePreview.getDrawable()==null){
                //send message with text
                MessageHelper.createMessageForChat(Objects.requireNonNull(inputTextMessage.getText()).toString(),
                        this.currentChatName, modelCurrentUser).addOnFailureListener(this.onFailureListener());
                this.inputTextMessage.setText("");
            }else {
                //send a message with text and image
                this.uploadPhotoInFirebaseAndSendAMessage(Objects.requireNonNull(inputTextMessage.getText()).toString());
                this.inputTextMessage.setText("");
                this.imagePreview.setImageDrawable(null);
            }

        }
    }

 
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    void onClickAddFile() {
        this.chooseImageFromPhone();
    }

    //--------------
    //REST REQUESTS
    //--------------
    //Get current user
    private void getCurrentUserFromFirestore() {
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser = documentSnapshot.toObject(User.class);
            }
        });
    }
    //Upload a picture in Firebase and send a message
    private void uploadPhotoInFirebaseAndSendAMessage(final String message){
        String uuid = UUID.randomUUID().toString();//GENERATE UNIQUE STRING
        //Upload to CGS
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
        UploadTask uploadTask = mImageRef.putFile(this.uriImageSelected);

        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (task.isSuccessful()){
                    throw Objects.requireNonNull(task.getException());
                }
                return mImageRef.getDownloadUrl();
            }
        }) .addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    MessageHelper.createMessageWithImageForChat(Objects.requireNonNull(downloadUri).toString(), message,currentChatName, modelCurrentUser).addOnFailureListener(onFailureListener());
                }else{
                    Log.e("chat", "Error on complete:"+ task.getException());
                }
            }
        });

    }

    // --------------------
    // UI
    // --------------------
    // 5 - Configure RecyclerView with a Query
    private void configureRecyclerView(String chatName) {
        //Track current chat name
        this.currentChatName = chatName;
        //Configure Adapter & RecyclerView
        this.chatAdapter = new ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessageForChat(this.currentChatName)), Glide.with(this), this, this.getCurrentUser().getUid(), this);

        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                chatRecycler.smoothScrollToPosition(chatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        chatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecycler.setAdapter(this.chatAdapter);
    }

    // 6 - Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChanged() {
        // 7 - Show TextView in case RecyclerView is empty
        chatIsEmpty.setVisibility(this.chatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    //------------------
    //ERROR HANDLER
    //------------------
    private OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "An error is coming", Toast.LENGTH_SHORT).show();
            }
        };
    }

    //-----------------
    //FILE MANAGEMENT
    //-----------------
    private void chooseImageFromPhone() {
        if (!EasyPermissions.hasPermissions(Objects.requireNonNull(getContext()), PERMS)) {
            EasyPermissions.requestPermissions(this, "this application need permission to run", RC_IMAGE_PERMS, PERMS);
            return;
        }
        //Launch an "selection image" Activity
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    // Handle activity response (after user has chosen or not a picture)
    private void handleResponse(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {//SUCCESS
                this.uriImageSelected = data.getData();
                Glide.with(this)//SHOWING PREVIEW OF IMAGE
                        .load(this.uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.imagePreview);
            } else {
                Toast.makeText(getContext(), "No picture selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
