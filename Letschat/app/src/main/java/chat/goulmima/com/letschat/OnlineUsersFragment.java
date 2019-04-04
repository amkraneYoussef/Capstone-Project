package chat.goulmima.com.letschat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import chat.goulmima.com.letschat.DataAdapters.OnlineUsersAdapter;
import chat.goulmima.com.letschat.myWidget.MessagesListWidget;
import chat.goulmima.com.letschat.myWidget.WidgetUpdateService;


public class OnlineUsersFragment extends Fragment {

    public OnlineUsersFragment() {
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online_users, container, false);
        RecyclerView rv = view.findViewById(R.id.rv_onlineUsers);
        OnlineUsersAdapter onlineUsersAdapter = new OnlineUsersAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(onlineUsersAdapter);
        FireBaseDataUtils.getOnlineUsers(onlineUsersAdapter, FirebaseAuth.getInstance().getUid());
        /*MessagesListWidget.sendRefreshBroadcast(getContext());*/
        WidgetUpdateService.startActionUpdateAppWidgets(getContext().getApplicationContext());
        return view;
    }
}
