package com.rubenmimoun.meetup.app.MainMenuPackage;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.multidex.MultiDex;
import androidx.navigation.ui.AppBarConfiguration;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rubenmimoun.meetup.app.AuthentificationPackage.RegisterLoginActivity;
import com.rubenmimoun.meetup.app.ChatUtils.FragmentChat;
import com.rubenmimoun.meetup.app.DrawerFragments.FragmentHome;
import com.rubenmimoun.meetup.app.DrawerFragments.FragmentMyActivities;
import com.rubenmimoun.meetup.app.DrawerFragments.FragmentProfile;
import com.rubenmimoun.meetup.app.DrawerFragments.activitiesFragment.FragmentActivities;
import com.rubenmimoun.meetup.app.DrawerFragments.activitiesFragment.MapFragment;
import com.rubenmimoun.meetup.app.Models.User;
import com.rubenmimoun.meetup.app.R;
import com.rubenmimoun.meetup.app.utils.MultiDexClass;

public class MainMenu extends MultiDexClass {

    private AppBarConfiguration mAppBarConfiguration;
    private ImageView imageViewProfil ;
    private TextView username_profile ;
    private FirebaseUser firebaseUser ;
    private DrawerLayout mDrawer ;
    private ActionBarDrawerToggle drawerToggle ;
    private  Toolbar toolbar ;

    private InterstitialAd mInterstitialAd;
    private String UNITID = "ca-app-pub-9747466889192287/4679989530" ;
    private String TESTID ="ca-app-pub-3940256099942544/1033173712";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        replaceFragmet(new FragmentHome());
       toolbar = findViewById(R.id.toolbar);
       toolbar.setLogo(R.drawable.logo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MultiDex.install(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        mDrawer = findViewById(R.id.drawer_layout);
        drawerToggle = setUpDrawerToggle();
        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        NavigationView navigationView = findViewById(R.id.nav_view);
        imageViewProfil =(ImageView)navigationView.getHeaderView(0).findViewById(R.id.imageView);
        username_profile = (TextView)navigationView.getHeaderView(0).findViewById(R.id.username_profil) ;



        setupDrawerContent(navigationView);
        setUserProfile();

        //TODO set screen/fragment my activities

        MobileAds.initialize(this, "ca-app-pub-9747466889192287~8079629754");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(UNITID);
       // mInterstitialAd.setAdUnitId(TESTID);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                showInterstitial();
            }
        });

    }


    private ActionBarDrawerToggle setUpDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }


    private void setUserProfile(){

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        System.out.println("mref"  + mRef.toString());

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);


                assert user != null;
                String username =  user.getName();
                System.out.println(username);
                username_profile.setText(user.getName()) ;

                    if( user.getImageURL().equals("default")){
                        imageViewProfil.setImageResource(R.drawable.unknown);
                    }else{
                        Glide.with(getApplicationContext()).load(user.getImageURL()).into(imageViewProfil);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, RegisterLoginActivity.class));
                finish();
                return true;
        }

        if (drawerToggle.onOptionsItemSelected(item)) {

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        switch(menuItem.getItemId()) {
            default:
            case R.id.nav_home:
                replaceFragmet(new FragmentHome());
                break;
            case R.id.nav_activities:
                replaceFragmet(new FragmentActivities());
                break;
            case R.id.nav_tools:
                replaceFragmet(new FragmentMyActivities());
                break;
            case R.id.nav_profile :
                replaceFragmet(new FragmentProfile());
                break;
            case R.id.nav_share :
                    shareTextUrl();

                break;

        }


        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }



    private void replaceFragmet(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Look at this !");
        share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.rubenmimoun.meetup.app");
        startActivity(Intent.createChooser(share, "Share link!"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
            mInterstitialAd.show();
        }
    }



}
