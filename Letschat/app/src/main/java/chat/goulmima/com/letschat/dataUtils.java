package chat.goulmima.com.letschat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.internal.Asserts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import chat.goulmima.com.letschat.POJOS.AppUser;
import chat.goulmima.com.letschat.myWidget.MessagesListWidget;

public class dataUtils {
    private static String TAG = "DataUtils";
    static FirebaseDatabase database;


    public static void getUserByID(String id, ArrayList<AppUser> usersList,Context context)
    {
        database = FirebaseDatabase.getInstance();

        DatabaseReference user = database.getReference(getUserRefPathByID(id));
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(AppUser usr : usersList)
                {
                    if(usr.getAppUserID().equals(id)) return;
                }
                AppUser returnedUser  = dataSnapshot.getValue(AppUser.class);
                returnedUser.setAppUserID(id);
                usersList.add(returnedUser);
                MessagesListWidget.sendRefreshBroadcast(context);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG , "Error getting user");
            }
        });
    }

    private static String getUserRefPathByID(String id)
    {
        return "users/" + id;
    }
}
