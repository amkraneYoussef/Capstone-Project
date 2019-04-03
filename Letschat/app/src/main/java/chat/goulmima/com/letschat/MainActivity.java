package chat.goulmima.com.letschat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;

import chat.goulmima.com.letschat.DataAdapters.OnlineUsersAdapter;
import chat.goulmima.com.letschat.POJOS.AppUser;
import chat.goulmima.com.letschat.login.LoginActivity;
import chat.goulmima.com.letschat.myWidget.MessagesListWidget;

import static chat.goulmima.com.letschat.FireBaseDataUtils.getOnlineUsers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();


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

        intent = getIntent();
        AppUser userData = new AppUser();
        userData.setAppUserID(currentUser.getUid());
        userData.setConnected(true);

        if(currentUser.getPhotoUrl() != null)
        {
            userData.setPhotoUrl(currentUser.getPhotoUrl().toString());
        }

        if (intent != null && intent.hasExtra(AppUser.EXTRA_USERNAME))
        {
               userData.setAppUserName(intent.getExtras().getString(AppUser.EXTRA_USERNAME));
        }else if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty())
        {
            userData.setAppUserName(currentUser.getDisplayName());
        }

        FireBaseDataUtils.tryCreateUser(userData);

        setContentView(R.layout.activity_main);

        // load test ad using admob
        loadAd();

        findViewById(R.id.signOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabase.goOffline();
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent,
                            ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                }else
                    startActivity(intent);
                finish();
            }
        });

        try
        {
            if(currentUser.getPhotoUrl() != null)
            {
                ImageView profilePhoto = findViewById(R.id.iv_profile);
                Glide.with(this).load(currentUser.getPhotoUrl()).into(profilePhoto);
            }
        }catch(Exception e)
        {
         Log.e(TAG,e.getMessage());
        }

        findViewById(R.id.iv_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this,ProfileActivity.class);
                intent1.putExtra(AppUser.EXTRA_USERNAME,currentUser.getUid());
                startActivity(intent1);
            }
        });
    }

    private void loadAd() {
        MobileAds.initialize(this);
        AdView mAdView = findViewById(R.id.adView1);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.e(TAG,"error loading ad : " + errorCode);
            }
        });
        mAdView.loadAd(adRequest);
    }
}
