package chat.goulmima.com.letschat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import chat.goulmima.com.letschat.POJOS.AppUser;

public class FireBaseDataUtils {
    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference usersReference;

    public static AppUser getUserInfoByID(String userID)
    {
        final AppUser appUser = new AppUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersReference = firebaseDatabase.getReference("users");
        usersReference.orderByChild(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(!dataSnapshot.exists()){
                  AppUser user = dataSnapshot.getValue(AppUser.class);
                  appUser.setAppUserID(user.getAppUserID());
                    appUser.setAppUserName(user.getAppUserName());
                    appUser.setLastOnline(user.getLastOnline());
                    appUser.setPhotoUrl(user.getPhotoUrl());
                    appUser.setConnected(user.getConnected());
                }
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
        if(appUser.getAppUserID() == null) return null;
        return appUser;
    }


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
}
