package chat.goulmima.com.letschat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
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
import chat.goulmima.com.letschat.DataAdapters.OnlineUsersAdapter;
import chat.goulmima.com.letschat.POJOS.AppUser;
import chat.goulmima.com.letschat.POJOS.TextMessage;

import static chat.goulmima.com.letschat.FireBaseDataUtils.*;

public class MessagingActivity extends AppCompatActivity implements OnlineUsersAdapter.OnUserClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        Intent intent = getIntent();
        if(intent != null)
        {
            if(intent.hasExtra(getString(R.string.USER_EXTRA)))
            {
               AppUser remoteAppUser = intent.getParcelableExtra(getString(R.string.USER_EXTRA));
               Bundle bundle = new Bundle();
               bundle.putParcelable(getString(R.string.USER_EXTRA),remoteAppUser);

                FragmentManager fragmentManager = getSupportFragmentManager();
                MessagingFragment messagingFragment = new MessagingFragment();
                messagingFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_container,messagingFragment)
                        .commit();
            }
        }

    }

    @Override
    public void onUserClick(AppUser appUser) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.USER_EXTRA),appUser);

        FragmentManager fragmentManager = getSupportFragmentManager();
        MessagingFragment messagingFragment = new MessagingFragment();
        messagingFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container,messagingFragment)
                .commit();
    }
}
