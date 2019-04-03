package chat.goulmima.com.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import chat.goulmima.com.letschat.POJOS.AppUser;

public class ProfileActivity extends AppCompatActivity {
    private static String TAG = "ProfileActivity";
    private AppUser appUser;
    EditText et_username, et_fullname, et_city;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(AppUser.EXTRA_USERNAME)) {
            finish();
        }
        usersRef = FirebaseDatabase.getInstance().getReference("users");


        et_fullname = findViewById(R.id.et_fullname);
        et_city = findViewById(R.id.et_city);
        et_username = findViewById(R.id.et_username);
        String userID = intent.getExtras().getString(AppUser.EXTRA_USERNAME);
        usersRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appUser = dataSnapshot.getValue(AppUser.class);
                appUser.setAppUserID(userID);
                initUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        try {
            if (appUser.getPhotoUrl() != null) {
                ImageView profilePhoto = findViewById(R.id.iv_profile);
                Glide.with(this).load(appUser.getPhotoUrl()).into(profilePhoto);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName, userName, City;
                fullName = et_fullname.getText().toString();
                userName = et_username.getText().toString();
                City = et_city.getText().toString();
                if (userName == null || userName.isEmpty()) {
                    Snackbar.make(view, R.string.should_enter_username, Snackbar.LENGTH_LONG)
                            .setAction("Username empty", null).show();
                    return;
                }
                if (City == null || City.isEmpty()) {
                    Snackbar.make(view, R.string.should_enter_city, Snackbar.LENGTH_LONG)
                            .setAction("City empty", null).show();
                    return;
                }
                appUser.setAppUserName(userName);
                appUser.setCurrentCity(City);
                appUser.setFullName(fullName);

                String userID = appUser.getAppUserID();
                appUser.setAppUserID(null);
                usersRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if((dataSnapshot.exists())){
                            usersRef.child(userID).setValue(appUser, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Log.e(TAG,"Data could not be saved. " + databaseError.getMessage());
                                    } else {
                                        Log.v(TAG,"Data saved successfully.");
                                        Snackbar.make(view, R.string.profileUpdated, Snackbar.LENGTH_LONG)
                                                .setAction("Updated", null).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG,"Data could not be saved. " + databaseError.getMessage());
                    }
                });
        }
    });

    }


    private void initUI() {

        et_username.setText(appUser.getAppUserName());
        et_city.setText(appUser.getCurrentCity());
        et_fullname.setText(appUser.getFullName());
    }

}
