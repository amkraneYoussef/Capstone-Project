package chat.goulmima.com.letschat.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import chat.goulmima.com.letschat.MainActivity;
import chat.goulmima.com.letschat.POJOS.AppUser;
import chat.goulmima.com.letschat.R;

public class RegistrationActivity extends AppCompatActivity {
    private static final String EXTRA_USERNAME ="EXTRA_USERNAME";
    private static final String EXTRA_EMAIL ="EXTRA_EMAIL";
    private static final String EXTRA_PASSWORD ="EXTRA_PASSWORD";

    private EditText tv_username, tv_email, tv_password;
    private Button regBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_USERNAME,tv_username.getText().toString());
        outState.putString(EXTRA_EMAIL,tv_email.getText().toString());
        outState.putString(EXTRA_PASSWORD,tv_password.getText().toString());
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        initializeUI();

        if(savedInstanceState!=null)
        {
            tv_username.setText(savedInstanceState.getString(EXTRA_USERNAME));
            tv_email.setText(savedInstanceState.getString(EXTRA_EMAIL));
            tv_password.setText(savedInstanceState.getString(EXTRA_PASSWORD));
        }
        regBtn.setOnClickListener(v -> registerNewUser());
    }

    private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = tv_email.getText().toString();
        password = tv_password.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);

                        loginUserAccount();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    private void initializeUI() {
        tv_username = findViewById(R.id.username);
        tv_email = findViewById(R.id.email);
        tv_password = findViewById(R.id.password);
        regBtn = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);
    }

    private void loginUserAccount() {
        progressBar.setVisibility(View.VISIBLE);

        String username, email, password;
        email = tv_email.getText().toString();
        username = tv_username.getText().toString();
        password = tv_password.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), R.string.please_enter_email, Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), R.string.please_enter_pass, Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), R.string.pease_enter_username, Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);

                        Intent intent = new Intent (getApplicationContext(),MainActivity.class);
                        intent.putExtra(AppUser.EXTRA_USERNAME,username);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), R.string.login_failed_retry, Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}