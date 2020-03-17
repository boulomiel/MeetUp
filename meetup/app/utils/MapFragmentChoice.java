package com.rubenmimoun.meetup.app.utils;



import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rubenmimoun.meetup.app.DrawerFragments.activitiesFragment.MapFragment;
import com.rubenmimoun.meetup.app.Models.Actions;
import com.rubenmimoun.meetup.app.R;

import java.io.IOException;
import java.util.List;


public class MapFragmentChoice {


    private DatabaseReference mref ;
    private FragmentActivity activity ;
    private String adress ;
    private String name ;


    public MapFragmentChoice (FragmentActivity activity ,DatabaseReference mref, String adress,String name) {
        this.activity = activity ;
        this.adress = adress ;
        this.mref = mref ;
        this.name =  name ;
    }

    public void setChoice(){

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Actions actions = dataSnapshot.getValue(Actions.class) ;

                try{

                    if( actions.getAdress() != null){
                        // TODO : set directions of fragment with adress

                        FragmentManager fragmentManager =  activity.getSupportFragmentManager() ;
                        Fragment fragment = new MapFragment();
                        passData(fragment,name);
                        fragmentManager.beginTransaction().replace(R.id.flContent,fragment ).commit();
                    }


                }catch (NullPointerException e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void passData(Fragment fragment,String name){

        LatLng location =  getLocationFromAddress(activity,adress);
        double latitude = location.latitude;
        double longitude = location.longitude ;
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        bundle.putDouble("latitude",latitude);
        bundle.putDouble("longitude", longitude);
        fragment.setArguments(bundle);
    }



    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 1);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

}
