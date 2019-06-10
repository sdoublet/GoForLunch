package com.example.goforlunch.controler.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.goforlunch.R;
import com.example.goforlunch.model.Api.Firebase.MessageHelper;
import com.example.goforlunch.model.Api.Firebase.UserHelper;
import com.example.goforlunch.model.Message;
import com.example.goforlunch.model.User;
import com.example.goforlunch.views.recyclerViews.ChatAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class ChatFragment extends Fragment implements ChatAdapter.Listener {
    private static final String CHAT_NAME_RESTAURANT ="restaurant" ;
    @BindView(R.id.chat_recycler_view)
    RecyclerView chatRecycler;
    @BindView(R.id.chat_text_view)
    TextView chatIsEmpty;
    @BindView(R.id.input_text_message)
    TextInputEditText inputTextMessage;
    private User modelCurrentUser;
    private String currentChatName;
    private ChatAdapter chatAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new ChatFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view;
        view=inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);
        configureRecyclerView(CHAT_NAME_RESTAURANT);
        getCurrentUserFromFirestore();
        return view;
    }
    //------------------
    //UTILS
    //------------------
    private FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }
    protected Boolean isCurrentUserLogged(){
        return (this.getCurrentUser()!=null);
    }

    //-------------
    //ACTION
    //-------------

    @OnClick(R.id.chat_send_button)
    void onClickMessageSend(){
        if (!TextUtils.isEmpty(inputTextMessage.getText())&&modelCurrentUser!=null){
            MessageHelper.createMessageForChat(Objects.requireNonNull(inputTextMessage.getText()).toString(),
                    this.currentChatName, modelCurrentUser).addOnFailureListener(this.onFailureListener());
            this.inputTextMessage.setText("");
        }
    }

    @OnClick(R.id.chat_add_file_button)
    void onClickAddFile(){}

    //--------------
    //REST REQUESTS
    //--------------
    //Get current user
    private void getCurrentUserFromFirestore(){
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser= documentSnapshot.toObject(User.class);
            }
        });
    }
    // --------------------
    // UI
    // --------------------
    // 5 - Configure RecyclerView with a Query
    private void configureRecyclerView(String chatName){
        //Track current chat name
        this.currentChatName = chatName;
        //Configure Adapter & RecyclerView
        this.chatAdapter = new ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessageForChat(this.currentChatName)), Glide.with(this), this, this.getCurrentUser().getUid(),this );

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
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
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
    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "An error is coming",Toast.LENGTH_SHORT).show();
            }
        };
    }
}
