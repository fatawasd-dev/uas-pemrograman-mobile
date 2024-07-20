package com.example.utsmobile_fahmiagungtajulabidin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "RegisterActivity";
    private EditText emailEditText, passwordEditText, rePasswordEditText, usernameEditText;
    private Button buttonRegister, buttonRegisterGoogle;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        rePasswordEditText = findViewById(R.id.editTextRePassword);
        usernameEditText = findViewById(R.id.editTextUsername);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegisterGoogle = findViewById(R.id.buttonRegisterGoogle);

        progressBar = findViewById(R.id.progressBar);

        TextView textView = findViewById(R.id.registerFooterText);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String rePassword = rePasswordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    usernameEditText.setError("Username harus diisi");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email harus diisi");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password harus diisi");
                    return;
                }

                if (!password.equals(rePassword)) {
                    Toast.makeText(
                            RegisterActivity.this,
                            "Password dan konfirmasi password tidak cocok",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                firebaseAuthWithEmail(username, email, password);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        buttonRegisterGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void firebaseAuthWithEmail(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", username);
                            user.put("email", email);

                            db.collection("users").document(userId)
                                    .set(user)
                                    .addOnCompleteListener(aVoid -> {
                                        Toast.makeText(
                                                RegisterActivity.this,
                                                "Registrasi berhasil!",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        Intent intent = new Intent(
                                                RegisterActivity.this,
                                                NewsPortalDashboardActivity.class
                                        );
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                    });
                        } else {
                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Registrasi gagal: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                            Log.e("RegisterActivity", "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signIn() {
        progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "register:firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "register:signInWithCredentials:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("name", user.getDisplayName());
                            userMap.put("email", user.getEmail());

                            db.collection("users").document(userId)
                                    .set(userMap)
                                    .addOnCompleteListener(aVoid -> {
                                        Toast.makeText(RegisterActivity.this, "Daftar sukses", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, NewsPortalDashboardActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "register:failure", e);
                                        Toast.makeText(RegisterActivity.this, "Daftar gagal", Toast.LENGTH_SHORT).show();
                                    });

                        } else {
                            Log.w(TAG, "register:signInWithCredentials:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Login gagal", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}