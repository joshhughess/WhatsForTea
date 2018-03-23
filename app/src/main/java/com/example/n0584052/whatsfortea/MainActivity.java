package com.example.n0584052.whatsfortea;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import java.text.ParseException;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG2 = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private GoogleSignInClient mGoogleSignInClient;

    // [START declare_auth]
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase;

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date todaysDate = new Date();
    DateTime jdTD = new DateTime(todaysDate);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Date theDate = null;
        try {
            theDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-03-18 19:25:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final DateTime jdTheDate = new DateTime(theDate);
        Log.d("todaysDate", jdTD.toString());
        Log.d("theDate", jdTheDate.toString());
        int seconds = Seconds.secondsBetween(jdTheDate,jdTD).getSeconds()*10;
        int minutesBetween = Minutes.minutesBetween(jdTheDate,jdTD).getMinutes();
        if(minutesBetween==8) {
            Log.d("minutesBetween", String.valueOf(minutesBetween));
            addNotification();
        }
//        Button b1 = (Button)findViewById(R.id.notificationButton);
//        b1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Log.d("daysBetween", String.valueOf(Days.daysBetween(jdTheDate,jdTD).getDays()));
//
//            }
//        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
            TextView accountUsername = (TextView)hView.findViewById(R.id.accountUsername);
            accountUsername.setText(mAuth.getCurrentUser().getDisplayName());
        TextView accountEmail = hView.findViewById(R.id.userEmail);
        accountEmail.setText(mAuth.getCurrentUser().getEmail());

        Uri imgUri = Uri.parse(mAuth.getCurrentUser().getPhotoUrl().toString());
        ImageView profilePic = (ImageView)hView.findViewById(R.id.imageView);
        Picasso.with(this).load(imgUri).into(profilePic);

        Fragment newFragment = new ChooseMealFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.mainContainer, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void addNotification() {
        String id = "w01";
        Notification.Builder builder = new Notification.Builder(this, id);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Channel title";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String desc = "Channel desc";

            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(desc);
            notificationManager.createNotificationChannel(channel);
            builder = new Notification.Builder(this , id);
        }
        builder = new Notification.Builder(this, id);
        builder.setAutoCancel(false);
        builder.setContentTitle("You need food!");
        builder.setContentText("You've ran out of eggs");
        builder.setSmallIcon(R.drawable.notification_icon);
        if (Build.VERSION.SDK_INT >= 24)
            builder.setColor(Color.parseColor("#ff0000"));
        Notification notification = builder.build();
        notificationManager.notify(0 , notification);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        Log.d("checkthis",mGoogleSignInClient.toString());
//        Log.d(TAG2, mGoogleSignInClient.getSignInIntent().toString());

        //noinspection SimplifiableIfStatement
        if (id == R.id.signOut) {
//            mGoogleSignInClient.signOut().addOnCompleteListener(this,
//                    new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            Log.d(TAG2, "signOut:success");
//                        }
//                    });
            FirebaseAuth.getInstance().signOut();
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(myIntent);
            finish();
            // Google sign out

        }else if(id == R.id.help){
            Fragment newFragment = new HelpFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mainContainer, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    // [START onactivityresult]

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_account) {
            // Create new fragment and transaction
            Fragment newFragment = new AccountFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.mainContainer, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        } else if (id == R.id.nav_mealPlan) {
            // Create new fragment and transaction
            Fragment newFragment = new MealPlanFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.mainContainer, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        } else if (id == R.id.nav_chooseMeal) {
            // Create new fragment and transaction
            Fragment newFragment = new ChooseMealFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.mainContainer, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        } else if (id == R.id.nav_shoppingList) {
            // Create new fragment and transaction
            Fragment newFragment = new ShoppingListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.mainContainer, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        } else if (id == R.id.nav_foodAtHome) {
            // Create new fragment and transaction
            Fragment newFragment = new FoodAtHomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.mainContainer, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        } else if (id == R.id.nav_shopsNearby) {
            // Create new fragment and transaction
            Log.d("CREATE", "onNavigationItemSelected: navshopsnearby");

            Intent intent = new Intent(this,ShopsNearbyFragment.class);

            Fragment newFragment =  new ShopsNearbyFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.mainContainer, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        } else if (id == R.id.nav_feedback) {
            // Create new fragment and transaction
            Fragment newFragment = new FeedbackFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.mainContainer, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

