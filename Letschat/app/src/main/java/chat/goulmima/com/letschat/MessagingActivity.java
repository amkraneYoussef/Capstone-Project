package chat.goulmima.com.letschat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

import static chat.goulmima.com.letschat.FireBaseDataUtils.*;

public class MessagingActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        currentUserID = FirebaseAuth.getInstance().getUid();


        Intent intent = getIntent();
        if(intent != null)
        {
            if(intent.hasExtra(getString(R.string.USER_EXTRA)))
            {
                remoteAppUser = intent.getParcelableExtra(getString(R.string.USER_EXTRA));
            }
        }

        firebaseDatabase = FirebaseDatabase.getInstance();

        if (remoteAppUser != null)
        {
          //  TextView tv_receiver_name = findViewById(R.id.tv_receiver_username);
            // tv_receiver_name.setText(remoteAppUser.getAppUserName());
            chatSessionId = getChatSessionID(currentUserID,remoteAppUser.getAppUserID());
            messagesReference = firebaseDatabase.getReference("private_" + chatSessionId);
            remoteUserReceivedMessagesRef = firebaseDatabase.getReference("ReceivedMsgs/" + remoteAppUser.getAppUserID() + "/" + currentUserID);
            localUserReceivedMessagesRef = firebaseDatabase.getReference("ReceivedMsgs/" + currentUserID);
        }else
        {
            messagesReference = firebaseDatabase.getReference("messages");
        }


        final EditText mMessageText = findViewById(R.id.et_messageText);

        findViewById(R.id.sendMsg).setOnClickListener(new View.OnClickListener() {
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



        messagesAdapter = new MessagesAdapter(messagesArray,this,currentUserID,remoteAppUser.getPhotoUrl());
        recyclerView  = findViewById(R.id.rv_messages_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
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


    }

    @Override
    protected void onPause() {
        super.onPause();
        messagesReference.removeEventListener(mSentMessagesChildEventListener);
    }
}
