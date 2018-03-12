package com.example.n0584052.whatsfortea;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG2 = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // [START config_signin]
        // Configure Google Sign In
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);// [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
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
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("ShoppingList").setValue("Nothing here yet.");
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("ShoppingList").child("Item").setValue("Nothing here yet.");
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("ShoppingList").child("Item").child("ItemName").setValue("Nothing here yet.");
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("ShoppingList").child("Item").child("ItemQuantity").setValue("Nothing here yet.");
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("FoodAtHome").setValue("Nothing here yet.");
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("FoodAtHome").child("Item").setValue("Nothing here yet.");
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("FoodAtHome").child("Item").child("ItemName").setValue("Nothing here yet.");
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("FoodAtHome").child("Item").child("ItemQuantity").setValue("Nothing here yet.");
                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            myIntent.putExtra("signIn",mAuth.getCurrentUser().getDisplayName());
                            startActivity(myIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG2, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]
}
