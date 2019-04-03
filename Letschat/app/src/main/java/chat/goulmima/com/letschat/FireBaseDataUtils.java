package chat.goulmima.com.letschat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
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

public class FireBaseDataUtils {

    public static String getChatSessionID(String currentUserID,String remoteUserID)
    {
        String chatSessionId;
        if (currentUserID.compareTo(remoteUserID) > 0)
        {
            chatSessionId = currentUserID + '_' + remoteUserID;
        }else
        {
            chatSessionId = remoteUserID + '_' + currentUserID ;
        }
        return chatSessionId;
    }


    public static void tryCreateUser (AppUser currentUser)
    {
        Log.e("username","creating user");
        String userID = currentUser.getAppUserID();
        currentUser.setAppUserID(null); // do not add it to firebase
        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.exists())){
                    Log.e("username","not existing creating");
                    usersRef.child(userID).setValue(currentUser, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.e("firebaseUtils","Data could not be saved. " + databaseError.getMessage());
                            } else {
                                changeOnlineStatus(userID);
                                Log.v("firebaseUtils","Data saved successfully.");
                            }
                        }
                    });
                }else{
                    changeOnlineStatus(userID);
                    Log.e("username","only changing status");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DataUtils",databaseError.getDetails());
            }
        });
    }

    public static void changeOnlineStatus(String userID)
    {
        // status check variables
        DatabaseReference myConnectionsRef;
        DatabaseReference lastOnlineRef;
        DatabaseReference connectedRef;

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

    public static void getOnlineUsers(OnlineUsersAdapter onlineUsersAdapter, String userID) {

        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
        usersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                AppUser user = dataSnapshot.getValue(AppUser.class);
                user.setAppUserID(dataSnapshot.getKey());
                if (user.getConnected() && user.getAppUserID() != null)
                {
                    for(AppUser usr : onlineUsersAdapter.appUsers)
                    {
                        if(usr.getAppUserID().equals(user.getAppUserID())) return; // if user is on database do not add it
                    }
                    if (user.getAppUserID().equals(userID))
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
                user.setAppUserID(dataSnapshot.getKey());
                if (user.getConnected() && user.getAppUserID() != null)
                {
                    for(AppUser usr : onlineUsersAdapter.appUsers)
                    {
                        if(usr.getAppUserID().equals(user.getAppUserID())) return; // if user is on database do not add it
                    }
                    if (user.getAppUserID().equals(userID))  return;
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
                user.setAppUserID(dataSnapshot.getKey());
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

}