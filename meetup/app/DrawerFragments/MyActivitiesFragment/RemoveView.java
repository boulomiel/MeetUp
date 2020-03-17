package com.rubenmimoun.meetup.app.DrawerFragments.MyActivitiesFragment;

import android.content.Context;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rubenmimoun.meetup.app.DrawerFragments.activitiesFragment.AdapterRecyclerViews;
import com.rubenmimoun.meetup.app.Models.Actions;
import com.rubenmimoun.meetup.app.Models.User;

import java.util.ArrayList;

public class RemoveView {

    private Context context ;
    private RecyclerView.Adapter<AdapterRecyclerViews.ActionsViewHolder> adapter;
    private RecyclerView v ;
    private ArrayList<Actions>activityList ;

    public RemoveView(Context context, RecyclerView v,ArrayList<Actions>activityList,RecyclerView.Adapter<AdapterRecyclerViews.ActionsViewHolder> adapter) {
        this.context = context;
        this.v = v;
        this.activityList =activityList;
        this.adapter = adapter ;
    }


    public void removeItem(int position, RecyclerView v, final DatabaseReference mRef, final String uid) {
        adapter.notifyItemRemoved(position);
        activityList.remove(position);
        v.removeViewAt(position);
        //adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, activityList.size());

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    User user  =  snapshot.getValue(User.class) ;
                    if( user.getId().equals(uid)){
                        dataSnapshot.getRef().removeValue();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
