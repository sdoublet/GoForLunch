package com.example.goforlunch.controler.fragments;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.goforlunch.R;
import com.example.goforlunch.model.Api.Firebase.MessageHelper;
import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.example.goforlunch.model.Message;
import com.example.goforlunch.model.User;
import com.example.goforlunch.views.recyclerViews.ChatAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;



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




    public ChatFragment() {
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view;
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);
        configureRecyclerView();
        getCurrentUserFromFirestore();
        return view;
    }

    //------------------
    //UTILS
    //------------------
    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
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
            }
        }
    }



    //--------------
    //REST REQUESTS
    //--------------
    //Get current user
    private void getCurrentUserFromFirestore() {
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> modelCurrentUser = documentSnapshot.toObject(User.class));
    }


    // --------------------
    // UI
    // --------------------
    //  Configure RecyclerView with a Query
    private void configureRecyclerView() {
        //Track current chat name
        this.currentChatName = ChatFragment.CHAT_NAME_RESTAURANT;
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

    //  Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    // Hide search button item
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item=menu.findItem(R.id.search_button);
        if (item!=null){
            item.setVisible(false);
        }
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
        return e -> Toast.makeText(getContext(), "An error is coming", Toast.LENGTH_SHORT).show();
    }


}
