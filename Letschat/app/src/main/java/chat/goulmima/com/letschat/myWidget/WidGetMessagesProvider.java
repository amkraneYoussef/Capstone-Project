package chat.goulmima.com.letschat.myWidget;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import chat.goulmima.com.letschat.POJOS.AppUser;
import chat.goulmima.com.letschat.R;
import chat.goulmima.com.letschat.dataUtils;

public class WidGetMessagesProvider implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private ArrayList<AppUser> lastMessagesSenders;

    public WidGetMessagesProvider(Context context, Intent intent)
    {
        mContext = context;
        lastMessagesSenders = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        Log.e("widgetTest","widget created");
        loadLastMessages();
    }

    @Override
    public void onDataSetChanged() {
        Log.e("widgetTest","widget datasetchanged");
        loadLastMessages();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return lastMessagesSenders.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_msg_item);

        view.setTextViewText(R.id.tv_username, lastMessagesSenders.get(position).getAppUserName());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(mContext.getString(R.string.USER_EXTRA), lastMessagesSenders.get(position));
        view.setOnClickFillInIntent(R.id.ll_container, fillInIntent);
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void loadLastMessages()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
        {
            Log.e("newMessage", "not logged");
            return;
        }

        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("ReceivedMsgs/" + user.getUid());
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                addUser(dataSnapshot);
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            private void addUser(@NonNull DataSnapshot dataSnapshot) {
                // user is added to arraylist passed in the parameters
               dataUtils.getUserByID(dataSnapshot.getKey(), lastMessagesSenders,mContext);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    String userID = dataSnapshot.getKey();
                    AppUser toBeRemoved=null;
                    for (AppUser Myuser : lastMessagesSenders)
                    {
                        if( Myuser.getAppUserID().equals(userID))
                        {
                            toBeRemoved = Myuser;
                            break;
                        }
                    }
                    if(toBeRemoved == null) return;
                    lastMessagesSenders.remove(toBeRemoved);
                    MessagesListWidget.sendRefreshBroadcast(mContext);
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
