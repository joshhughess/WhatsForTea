package com.example.n0584052.whatsfortea;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG2 = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        Log.d("googleSignInClient",mGoogleSignInClient.toString());
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);// [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            public String TAG = "";

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    //LoginActivity.this.startActivity(myIntent);
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                    user = mAuth.getCurrentUser();
                    //Adds UID to database
                    final DatabaseReference dref;
                    dref = FirebaseDatabase.getInstance().getReference("UserID/");
                    dref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (!snapshot.hasChild(mAuth.getUid())) {
                                myRef.child("UserID").child(mAuth.getUid()).setValue(mAuth.getCurrentUser().getDisplayName());
                                //Adds test modules to database
                                myRef.child("UserID").child(mAuth.getUid()).child("Modules").child("Test Module").setValue("Lorem Ipsum is simply dummy text of the printing and typesetting industry.");
                                //Adds test Deadlines to database
                                myRef.child("UserID").child(mAuth.getUid()).child("Deadlines").child("Test Deadline").setValue("MPD Is due 23rd");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(myIntent);
                    finish();
                } else {
                    // User is signed out
                    System.out.println("User has signed out");
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    mGoogleSignInClient.signOut();
                }
            }
        };
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ProgressBar progressBar = findViewById(R.id.progressBarLogin);
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                signInButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG2, "Google sign in failed", e);
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG2, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG2, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG2, mAuth.getCurrentUser().getDisplayName());
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.child("users").hasChild(mAuth.getCurrentUser().getUid())){
                                        Log.d(TAG2,"already found user");
                                    }else{
                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("ShoppingList");
                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("FoodAtHome");
                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("MealPlan");
                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("Preferences");
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            myIntent.putExtra("signIn",mAuth.getCurrentUser().getDisplayName());
                            startActivity(myIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG2, "signInWithCredential:failure", task.getException());
                            mGoogleSignInClient.signOut();
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]
}
