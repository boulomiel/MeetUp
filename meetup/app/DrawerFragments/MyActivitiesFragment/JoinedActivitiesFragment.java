package com.rubenmimoun.meetup.app.DrawerFragments.MyActivitiesFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rubenmimoun.meetup.app.DrawerFragments.activitiesFragment.AdapterRecyclerViews;
import com.rubenmimoun.meetup.app.Models.Actions;
import com.rubenmimoun.meetup.app.Models.User;
import com.rubenmimoun.meetup.app.Models.UserList;
import com.rubenmimoun.meetup.app.R;

import java.util.ArrayList;

public class JoinedActivitiesFragment extends Fragment {



    private DatabaseReference mRef ;
    private DatabaseReference actRef ;
    private FirebaseUser firebaseUser ;
    private ArrayList<Actions> activity_list ;
    private RecyclerView recyclerView ;
    private AdapterRecyclerViews adapter ;
    private Button chat_btn ;


    public JoinedActivitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v  =  inflater.inflate(R.layout.fragment_joined_activities, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        actRef = FirebaseDatabase.getInstance().getReference("activities");

        recyclerView = v.findViewById(R.id.joined_activity_recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setMyActivities();

        return v ;


    }

    private void setMyActivities(){
        final String id =  FirebaseDatabase.getInstance().getReference("activities").child("participating").child(firebaseUser.getUid()).getKey();
        actRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<Actions>list =  new ArrayList<>();
                String img_str = "";

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Actions actions = snapshot.getValue(Actions.class) ;
                        if( actions.getParticipating() != null){
                            if( actions.getParticipating().containsValue(id)){
                                list.add(actions) ;
                                if( actions.getImg_url() != null){
                                    img_str += actions.getImg_url();
                                }
                            }



                        }

                    }

                adapter= new AdapterRecyclerViews(getContext(),getActivity(),list, img_str,false,false,false,recyclerView);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
