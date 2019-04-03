package chat.goulmima.com.letschat;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import chat.goulmima.com.letschat.DataAdapters.MessagesAdapter;
import chat.goulmima.com.letschat.POJOS.AppUser;
import chat.goulmima.com.letschat.POJOS.TextMessage;

import static chat.goulmima.com.letschat.FireBaseDataUtils.getChatSessionID;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessagingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagingFragment extends Fragment {
    private AppUser remoteAppUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference messagesReference;
    private ChildEventListener mSentMessagesChildEventListener;
    private ArrayList<TextMessage> messagesArray=new ArrayList<>();
    private MessagesAdapter messagesAdapter;
    private RecyclerView recyclerView;
    private String chatSessionId;
    private String currentUserID;
    private DatabaseReference remoteUserReceivedMessagesRef;
    private DatabaseReference localUserReceivedMessagesRef;
    private int receivedMessagesCount=0;
    EditText mMessageTextBox;
    private static String EXTRA_TEXT = "EXTRATEXT";

    public MessagingFragment() {
    }

    public static MessagingFragment newInstance(String param1, String param2) {
        MessagingFragment fragment = new MessagingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            remoteAppUser = (AppUser) bundle.getParcelable(getString(R.string.USER_EXTRA));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messaging, container, false);
        currentUserID = FirebaseAuth.getInstance().getUid();
        mMessageTextBox = view.findViewById(R.id.et_messageText);
        if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TEXT))
        {
            mMessageTextBox.setText(savedInstanceState.getString(EXTRA_TEXT));
        }

        firebaseDatabase = FirebaseDatabase.getInstance();

        if (remoteAppUser != null)
        {
            chatSessionId = getChatSessionID(currentUserID,remoteAppUser.getAppUserID());
            messagesReference = firebaseDatabase.getReference("private_" + chatSessionId);
            remoteUserReceivedMessagesRef = firebaseDatabase.getReference("ReceivedMsgs/" + remoteAppUser.getAppUserID() + "/" + currentUserID);
            localUserReceivedMessagesRef = firebaseDatabase.getReference("ReceivedMsgs/" + currentUserID);
        }else
        {
            messagesReference = firebaseDatabase.getReference("messages");
        }


        final EditText mMessageText = view.findViewById(R.id.et_messageText);

        view.findViewById(R.id.sendMsg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMessageText.getText().toString().isEmpty()) return;
                TextMessage textMessage = new TextMessage(FirebaseAuth.getInstance().getUid(), mMessageText.getText().toString(), "");
                DatabaseReference messageId = messagesReference.push();
                messageId.setValue(textMessage);
                remoteUserReceivedMessagesRef.child(messageId.getKey()).setValue(true);
                mMessageText.setText("");
            }
        });

        mSentMessagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TextMessage newMessage = dataSnapshot.getValue(TextMessage.class);
                messagesAdapter.messagesList.add((newMessage));
                messagesAdapter.notifyDataSetChanged();
                localUserReceivedMessagesRef.child(remoteAppUser.getAppUserID()).child(dataSnapshot.getKey()).removeValue();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        messagesAdapter = new MessagesAdapter(messagesArray,getContext(),currentUserID,remoteAppUser.getPhotoUrl());
        recyclerView  = view.findViewById(R.id.rv_messages_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(messagesAdapter);

        messagesReference.addChildEventListener(mSentMessagesChildEventListener);
        // TextView tv = findViewById(R.id.tv_newMessagesCount);
        // start listening for arrived messages
        localUserReceivedMessagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                receivedMessagesCount += 1;
                //  tv.setText(String.valueOf(receivedMessagesCount));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMessageTextBox != null)
        {
            outState.putString(EXTRA_TEXT,mMessageTextBox.getText().toString());
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        messagesReference.removeEventListener(mSentMessagesChildEventListener);
    }
}
