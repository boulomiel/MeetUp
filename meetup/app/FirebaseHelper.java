package com.rubenmimoun.meetup.app;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.rubenmimoun.meetup.app.Models.Actions;

import java.util.ArrayList;

public class FirebaseHelper {


    DatabaseReference db;
    Boolean saved=null;
    ArrayList<Actions> activies =new ArrayList<>();

    public FirebaseHelper(DatabaseReference db) {
        this.db = db;
    }

    //SAVE
    public Boolean save(Actions activity)
    {
        if(activity ==null)
        {
            saved=false;
        }else {

            try
            {
                db.child("activities").push().setValue(activity);
                saved=true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved=false;
            }

        }

        return saved;
    }

    //READ
    public ArrayList<Actions> retrieve()
    {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return activies;
    }

    private void fetchData(DataSnapshot dataSnapshot)
    {
        activies.clear();
        System.out.println(dataSnapshot.toString());
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {

            Actions activity =ds.getValue(Actions.class);
            activies.add(activity);
        }
    }

}
