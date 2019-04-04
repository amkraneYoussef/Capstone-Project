package chat.goulmima.com.letschat;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import chat.goulmima.com.letschat.DataAdapters.OnlineUsersAdapter;
import chat.goulmima.com.letschat.POJOS.AppUser;

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

                if(savedInstanceState == null)
                {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    MessagingFragment messagingFragment = new MessagingFragment();
                    messagingFragment.setArguments(bundle);
                    fragmentManager.beginTransaction()
                            .add(R.id.fragment_container,messagingFragment)
                            .commit();
                }
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
