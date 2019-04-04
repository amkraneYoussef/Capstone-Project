package chat.goulmima.com.letschat;


import android.annotation.SuppressLint;
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


import chat.goulmima.com.letschat.DataAdapters.MessagesAdapter;
import chat.goulmima.com.letschat.POJOS.AppUser;
import chat.goulmima.com.letschat.POJOS.TextMessage;

import static chat.goulmima.com.letschat.FireBaseDataUtils.getChatSessionID;



@SuppressWarnings("ALL")
public class MessagingFragment extends Fragment {
    private AppUser remoteAppUser;
    private DatabaseReference messagesReference;
    private ChildEventListener mSentMessagesChildEventListener;
    //private final ArrayList<TextMessage> messagesArray=new ArrayList<>();
    private MessagesAdapter messagesAdapter;
    private DatabaseReference remoteUserReceivedMessagesRef;
    private DatabaseReference localUserReceivedMessagesRef;
    private int receivedMessagesCount=0;
    private EditText mMessageTextBox;
    private static final String EXTRA_TEXT = "EXTRATEXT";

    public MessagingFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            remoteAppUser =  bundle.getParcelable(getString(R.string.USER_EXTRA));
        }
    }

    @SuppressLint("CutPasteId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messaging, container, false);
        String currentUserID = FirebaseAuth.getInstance().getUid();
        mMessageTextBox = view.findViewById(R.id.et_messageText);
        if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TEXT))
        {
            mMessageTextBox.setText(savedInstanceState.getString(EXTRA_TEXT));
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        if (remoteAppUser != null)
        {
            String chatSessionId = getChatSessionID(currentUserID, remoteAppUser.getAppUserID());
            messagesReference = firebaseDatabase.getReference("private_" + chatSessionId);
            remoteUserReceivedMessagesRef = firebaseDatabase.getReference("ReceivedMsgs/" + remoteAppUser.getAppUserID() + "/" + currentUserID);
            localUserReceivedMessagesRef = firebaseDatabase.getReference("ReceivedMsgs/" + currentUserID);
        }else
        {
            messagesReference = firebaseDatabase.getReference("messages");
        }


        @SuppressLint("CutPasteId") final EditText mMessageText = view.findViewById(R.id.et_messageText);

        view.findViewById(R.id.sendMsg).setOnClickListener(view1 -> {
            if (mMessageText.getText().toString().isEmpty()) return;
            TextMessage textMessage = new TextMessage(FirebaseAuth.getInstance().getUid(), mMessageText.getText().toString(), "");
            DatabaseReference messageId = messagesReference.push();
            messageId.setValue(textMessage);
            remoteUserReceivedMessagesRef.child(messageId.getKey()).setValue(true);
            mMessageText.setText("");
        });

        mSentMessagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TextMessage newMessage = dataSnapshot.getValue(TextMessage.class);
                messagesAdapter.messagesList.add((newMessage));
                messagesAdapter.notifyDataSetChanged();
                //noinspection ConstantConditions
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

        messagesAdapter = new MessagesAdapter(getContext(), currentUserID,remoteAppUser.getPhotoUrl());
        RecyclerView recyclerView = view.findViewById(R.id.rv_messages_list);
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
