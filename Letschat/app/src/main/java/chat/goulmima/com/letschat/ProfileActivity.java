package chat.goulmima.com.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import chat.goulmima.com.letschat.POJOS.AppUser;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final String EXTRA_USERNAME ="EXTRA_USERNAME";
    private static final String EXTRA_FULLNAME ="EXTRA_FULLNAME";
    private static final String EXTRA_CITY ="EXTRA_CITY";
    private static final String EXTRA_USER ="EXTRA_USER";
    private static final String EXTRA_USER_ID ="EXTRA_USER_ID";


    private AppUser appUser;
    private String userID1;
    private EditText et_username;
    private EditText et_FullName;
    private EditText et_city;
    private DatabaseReference usersRef;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_USERNAME,et_username.getText().toString());
        outState.putString(EXTRA_FULLNAME,et_FullName.getText().toString());
        outState.putString(EXTRA_CITY,et_city.getText().toString());
        outState.putParcelable(EXTRA_USER,appUser);
        outState.putString(EXTRA_USER_ID,userID1);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(AppUser.EXTRA_USERNAME)) {
            finish();
        }


        usersRef = FirebaseDatabase.getInstance().getReference("users");


        et_FullName = findViewById(R.id.et_fullname);
        et_city = findViewById(R.id.et_city);
        et_username = findViewById(R.id.et_username);
        if(savedInstanceState==null)
        {
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
        }else
        {
            et_username.setText(savedInstanceState.getString(EXTRA_USERNAME));
            et_FullName.setText(savedInstanceState.getString(EXTRA_FULLNAME));
            et_city.setText(savedInstanceState.getString(EXTRA_CITY));
            appUser = savedInstanceState.getParcelable(EXTRA_USER);
            userID1 = savedInstanceState.getString(EXTRA_USER_ID);
        }


        try {
            if (appUser.getPhotoUrl() != null) {
                ImageView profilePhoto = findViewById(R.id.iv_profile);
                Glide.with(this).load(appUser.getPhotoUrl()).into(profilePhoto);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            String fullName, userName, City;
            fullName = et_FullName.getText().toString();
            userName = et_username.getText().toString();
            City = et_city.getText().toString();
            if ( userName.isEmpty()) {
                Snackbar.make(view, R.string.should_enter_username, Snackbar.LENGTH_LONG)
                        .setAction(R.string.username_empty, null).show();
                return;
            }
            if ( City.isEmpty()) {
                Snackbar.make(view, R.string.should_enter_city, Snackbar.LENGTH_LONG)
                        .setAction(R.string.city_empty, null).show();
                return;
            }

            appUser.setAppUserName(userName);
            appUser.setCurrentCity(City);
            appUser.setFullName(fullName);
            if(appUser.getAppUserID()!=null)
            userID1 = appUser.getAppUserID();

            appUser.setAppUserID(null);
            usersRef.child(userID1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if((dataSnapshot.exists())){
                        usersRef.child(userID1).setValue(appUser, (databaseError, databaseReference) -> {
                            if (databaseError != null) {
                                Log.e(TAG, "Data could not be saved. " + databaseError.getMessage());
                            } else {
                                Log.v(TAG, "Data saved successfully.");
                                Snackbar.make(view, R.string.profileUpdated, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.Updated, null).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG,"Data could not be saved. " + databaseError.getMessage());
                }
            });
    });

    }


    private void initUI() {

        et_username.setText(appUser.getAppUserName());
        et_city.setText(appUser.getCurrentCity());
        et_FullName.setText(appUser.getFullName());
    }

}
