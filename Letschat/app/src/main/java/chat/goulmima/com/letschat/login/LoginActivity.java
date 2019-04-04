package chat.goulmima.com.letschat.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import chat.goulmima.com.letschat.MainActivity;
import chat.goulmima.com.letschat.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String EXTRA_EMAIL ="EXTRA_EMAIL";
    private static final String EXTRA_PASSWORD ="EXTRA_PASSWORD";

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 123;
    private TextView tv_register;
    private EditText emailTV, passwordTV;
    private Button loginBtn;
    private ProgressBar progressBar;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_EMAIL,emailTV.getText().toString());
        outState.putString(EXTRA_PASSWORD,passwordTV.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeUI();

        if(savedInstanceState!=null)
        {
            emailTV.setText(savedInstanceState.getString(EXTRA_EMAIL));
            passwordTV.setText(savedInstanceState.getString(EXTRA_PASSWORD));
        }

        tv_register.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        loginBtn.setOnClickListener(v -> loginUserAccount());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.WebClientID))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btn).setOnClickListener(view -> signIn());

    }



    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            //noinspection ConstantConditions
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("ssss", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        mGoogleSignInClient.signOut();
                        Intent intent = new Intent (getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("LoginActivity", "signInWithCredential:failure", task.getException());
                        Toast.makeText( getApplicationContext(),"Authentication Failed.", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void initializeUI() {
        tv_register = findViewById(R.id.tv_register);
        emailTV = findViewById(R.id.et_email);
        passwordTV = findViewById(R.id.et_password);

        loginBtn = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);
    }

    private void loginUserAccount() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), R.string.please_enter_email, Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), R.string.please_enter_pass, Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);

                        Intent intent = new Intent (getApplicationContext(),MainActivity.class);
                        startActivity(intent);

                    }
                    else {
                        Toast.makeText(getApplicationContext(), R.string.login_failed_retry, Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
