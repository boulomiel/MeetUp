package com.rubenmimoun.meetup.app.DrawerFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rubenmimoun.meetup.app.ChatUtils.FragmentChat;
import com.rubenmimoun.meetup.app.DrawerFragments.activitiesFragment.AdapterRecyclerViews;
import com.rubenmimoun.meetup.app.Models.Actions;
import com.rubenmimoun.meetup.app.Models.User;
import com.rubenmimoun.meetup.app.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {

    private DatabaseReference mRef ;
    private DatabaseReference actRef ;
    private FirebaseUser firebaseUser ;
    private ArrayList<Actions>activity_list ;
    private RecyclerView recyclerView ;
    private AdapterRecyclerViews adapter ;
    private Button chat_btn ;
    private SwipeRefreshLayout refreshLayout ;



    public FragmentHome(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v  =  inflater.inflate(R.layout.fragment_home,container,false);

        refreshLayout = v.findViewById(R.id.refresh) ;


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        mRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        actRef = FirebaseDatabase.getInstance().getReference("activities");

        checkForUsersCity();

        recyclerView = v.findViewById(R.id.home_recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        chat_btn = v.findViewById(R.id.city_chat_btn);
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getData();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.flContent,new FragmentChat()).commit();
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkForUsersCity();
                refreshLayout.setRefreshing(false);
            }
        });

        return  v ;
    }

    private void getData(){



        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =  dataSnapshot.getValue(User.class) ;

                    Fragment fragment = new FragmentChat();
                    String city =user.getCity() ;
                    Bundle bundle = new Bundle();
                    bundle.putString("city",city);
                    fragment.setArguments(bundle);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }




    private void checkForUsersCity(){

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =  dataSnapshot.getValue(User.class) ;

                if( user.getCity() == null){
                    recyclerView.setVisibility(View.INVISIBLE);
                   new AlertDialog.Builder(getContext())
                    .setTitle("Profile not set")
                    .setMessage("To access to the activities around you, you must set a city in your profile")
                    .setPositiveButton("Go to profile", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.flContent, new FragmentProfile()).commit();
                                }
                            }).create().show();
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    displayActivityAccordingToLocation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void displayActivityAccordingToLocation(){

        final String[] user_city = new String[1];

        final String[] img_str = new String[1];
        activity_list = new ArrayList<>();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            User user  = dataSnapshot.getValue(User.class) ;
            user_city[0] = user.getCity();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        actRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    Actions action =  s.getValue(Actions.class) ;

                    if(action.getPlace().equals(user_city[0])){

                        if( !action.getCreator().equals(firebaseUser.getUid()))
                        activity_list.add(action);

                        if(action.getImg_url() != null){
                            img_str[0] =action.getImg_url() ;
                        }


                    }
                }
                adapter = new AdapterRecyclerViews(getContext(),getActivity(),activity_list, img_str[0],true,false,false,recyclerView);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //TODO set deleting when passed date


    public void updateData(List<Actions> activities) {
        activity_list.clear();
        activity_list.addAll(activities);
        adapter.notifyDataSetChanged();
    }
    public void addItem(int position, Actions viewModel) {
        activity_list.add(position, viewModel);
        adapter.notifyItemInserted(position);
    }

    public void removeItem(int position) {
        activity_list.remove(position);
        adapter.notifyItemRemoved(position);
        recyclerView.removeViewAt(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, activity_list.size());

    }




    }
