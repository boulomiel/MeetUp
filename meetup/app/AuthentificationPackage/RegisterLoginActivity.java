package com.rubenmimoun.meetup.app.AuthentificationPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rubenmimoun.meetup.app.MainMenuPackage.MainMenu;
import com.rubenmimoun.meetup.app.Models.User;
import com.rubenmimoun.meetup.app.R;

import java.util.ArrayList;

public class RegisterLoginActivity extends AppCompatActivity {
    public static  final String USERS = "users";

    private DatabaseReference mRef ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_);
        setContentView(R.layout.activity_main);

        mRef = FirebaseDatabase.getInstance().getReference(USERS);
        clearValuesOnstart(mRef);


        ViewPager viewPager =  findViewById(R.id.viewPager);

        AuthenticationPagerAdapter pagerAdapter = new AuthenticationPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragmet(new LoginFragment());
        pagerAdapter.addFragmet(new RegisterFragment());
        viewPager.setAdapter(pagerAdapter);




    }


    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        clearValuesOnstart(mRef);

        if(FirebaseAuth.getInstance() != null ){

            if(user != null){
                Intent intent  = new Intent(RegisterLoginActivity.this, MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK) ;
                startActivity(intent);
                finish();
            }
        }





    }

    private void clearValuesOnstart( DatabaseReference mRef){

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    user.setStatus("offline");
                    user.setPlaying("no");

                    }

                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    class AuthenticationPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragmentList = new ArrayList<>();

        AuthenticationPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        void addFragmet(Fragment fragment) {
            fragmentList.add(fragment);
        }
    }






}

