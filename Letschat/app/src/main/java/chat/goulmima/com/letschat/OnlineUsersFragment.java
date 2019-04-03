package chat.goulmima.com.letschat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import chat.goulmima.com.letschat.DataAdapters.OnlineUsersAdapter;
import chat.goulmima.com.letschat.myWidget.MessagesListWidget;


public class OnlineUsersFragment extends Fragment {
    private OnlineUsersAdapter onlineUsersAdapter;
    RecyclerView rv;

    public OnlineUsersFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online_users, container, false);
        rv = view.findViewById(R.id.rv_onlineUsers);
        onlineUsersAdapter = new OnlineUsersAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(onlineUsersAdapter);
        FireBaseDataUtils.getOnlineUsers(onlineUsersAdapter, FirebaseAuth.getInstance().getUid());
        MessagesListWidget.sendRefreshBroadcast(getContext());
        return view;
    }
}
