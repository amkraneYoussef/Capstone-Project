package chat.goulmima.com.letschat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import chat.goulmima.com.letschat.DataAdapters.OnlineUsersAdapter;
import chat.goulmima.com.letschat.POJOS.AppUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private OnlineUsersAdapter onlineUsersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Intent intent;
        if(currentUser == null)
        {
            intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        tryCreateUser();
        setContentView(R.layout.activity_main);

        // go online
        firebaseDatabase = FirebaseDatabase.getInstance();

        findViewById(R.id.signOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabase.goOffline();
                mAuth.signOut();
                finish();
            }
        });

        ImageView profilePhoto = findViewById(R.id.iv_profile);
        Glide.with(this).load(currentUser.getPhotoUrl()).into(profilePhoto);

        onlineUsersAdapter = new OnlineUsersAdapter(this);
        RecyclerView rv = findViewById(R.id.rv_onlineUsers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(onlineUsersAdapter);
        getOnlineUsers();
    }

    private void getOnlineUsers() {

        DatabaseReference usersReference = firebaseDatabase.getReference("users");
        usersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                AppUser user = dataSnapshot.getValue(AppUser.class);
                if (user.getConnected() && user.getAppUserID() != null)
                {
                    for(AppUser usr : onlineUsersAdapter.appUsers)
                    {
                        if(usr.getAppUserID().equals(user.getAppUserID())) return; // if user is on database do not add it
                    }
                    if (user.getAppUserID().equals(currentUser.getUid()))
                    {
                        return;
                    }
                    onlineUsersAdapter.appUsers.add(user);
                    onlineUsersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                AppUser user = dataSnapshot.getValue(AppUser.class);
                if (user.getConnected() && user.getAppUserID() != null)
                {
                    for(AppUser usr : onlineUsersAdapter.appUsers)
                    {
                        if(usr.getAppUserID().equals(user.getAppUserID())) return; // if user is on database do not add it
                    }
                    if (user.getAppUserID().equals(currentUser.getUid()))  return;
                        onlineUsersAdapter.appUsers.add(user);
                        onlineUsersAdapter.notifyDataSetChanged();

                }else
                {
                    removeUser(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            private void removeUser(DataSnapshot  dataSnapshot)
            {
                AppUser user = dataSnapshot.getValue(AppUser.class);
                for (AppUser Myuser : onlineUsersAdapter.appUsers )
                {
                    if( Myuser.getAppUserID().equals(user.getAppUserID()))
                    {
                        user = Myuser;
                        break;
                    }
                }
                onlineUsersAdapter.appUsers.remove(user);
                onlineUsersAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void tryCreateUser ()
    {
        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.exists())){
                    AppUser appUser = new AppUser();
                    appUser.setAppUserID(currentUser.getUid());
                    appUser.setAppUserName(currentUser.getDisplayName());
                    appUser.setPhotoUrl(currentUser.getPhotoUrl().toString());
                    appUser.setConnected(true);
                    usersRef.child(currentUser.getUid()).setValue(appUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            changeOnlineStatus(currentUser.getUid());
                        }
                    });
                }else
                    changeOnlineStatus(currentUser.getUid());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void changeOnlineStatus(String userID)
    {
        // status check variables
        DatabaseReference myConnectionsRef;
        DatabaseReference lastOnlineRef;
        DatabaseReference connectedRef;

        Log.e("TAG","updating status");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance()  ;

        myConnectionsRef = firebaseDatabase.getReference("users/" + userID + "/connected");

        // stores the timestamp of my last disconnect (the last time I was seen online)
        lastOnlineRef = firebaseDatabase.getReference("users/" + userID + "/lastOnline");

        connectedRef = firebaseDatabase.getReference(".info/connected");

        myConnectionsRef.setValue(true);

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // when I disconnect, update the last time I was seen online
                lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                // when this device disconnects, remove it
                myConnectionsRef.onDisconnect().setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println("Listener was cancelled at .info/connected");
            }
        });
    }
}
