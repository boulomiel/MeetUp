package com.rubenmimoun.meetup.app.DrawerFragments.activitiesFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

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
import com.rubenmimoun.meetup.app.Models.Actions;
import com.rubenmimoun.meetup.app.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FragmentActiviesToJoin extends Fragment {

    private RecyclerView recyclerView ;
    private FirebaseUser firebaseUser ;
    private DatabaseReference mRef ;
    private ArrayList<Actions> activityList;
    private AdapterRecyclerViews adapter;


    private TextView chosen_activity_type ;
    private TextView chosen_city ;
    private ExpandableListView expandableListType;
    private ExpandableListView expandableListPlace;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableTitleType;
    private List<String> expandableTitlePlace;
    private List<String> cities ;
    private List<String>type ;
    private HashMap<String, List<String>> expandableTypeDetail;
    private HashMap<String, List<String>> expandablePlaceDetail;
    private String activity ;
    private String city ;
    private  String chosenCity ;
    private String chosenKind ;

    public FragmentActiviesToJoin(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO set the reference according to the city, check if is the city content is null, set a toast/ snackbar

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("activities");


        View v  =  inflater.inflate(R.layout.fragment_activities_tojoin,container,false);

        recyclerView = v.findViewById(R.id.recycler_view_tojoin) ;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        listInit();
        listViewInit(v);



        return  v ;
    }

    private void listInit(){

        cities = new ArrayList<>();
        cities.add("Tel Aviv");
        cities.add("Herzliya");
        cities.add("Netanya");
        cities.add("Ramat Gan");
        cities.add("Givatayim");
        cities.add("All");
        //new cities
        cities.add("Ako");
        cities.add("Nahariya");
        cities.add("Haifa");
        cities.add("Yoknam");
        cities.add("Zikhon Ya'akov");
        cities.add("Rishon lTzion");
        cities.add("Bat Yam");
        cities.add("Petah Tikva");
        cities.add("Rehovot");
        cities.add("Beer Sheva");
        cities.add("Modi'in");
        cities.add("Rosh Ha Hayin");
        cities.add("Eilat");
        Collections.sort(cities);

        type = new ArrayList<>();
        type.add("Bar") ;
        type.add("Cinema");
        type.add("Cultural");
        type.add("Party") ;
        type.add("Restaurant");
        type.add("Sport");
        type.add("All");
        type.add("Else");
        Collections.sort(type);


    }

    private void listViewInit(View v){

        chosen_activity_type = v.findViewById(R.id.join_chosen_kind);
        chosen_city = v.findViewById(R.id.join_chosen_city) ;

        expandableListType = v.findViewById(R.id.join_city);
        expandableTypeDetail = ExpandableListDataPump.getData("Kind",type);
        expandableTitleType = new ArrayList<String>(expandableTypeDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(getContext(), expandableTitleType, expandableTypeDetail);
        expandableListType.setAdapter(expandableListAdapter);

        expandableListPlace = v.findViewById(R.id.join_kind);
        expandablePlaceDetail = ExpandableListDataPump.getData("Where",cities);
        expandableTitlePlace = new ArrayList<String>(expandablePlaceDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(getContext(), expandableTitlePlace, expandablePlaceDetail);
        expandableListPlace.setAdapter(expandableListAdapter);

        expandableListType.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        expandableListType.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {


            }
        });

        expandableListType.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                parent.collapseGroup(groupPosition);
                activity =  expandableTypeDetail.get("Kind").get(childPosition) ;

                for (String a: type) {
                    if(activity.equals(a)){
                        setChosenKind(a);
                        chosen_activity_type.setText(a);
                    }
                }

                getActivitiesFromDb();


                //TODO UPDATE DATABASE ;

                return false;
            }
        });

        expandableListPlace.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        expandableListPlace.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {


            }
        });

        expandableListPlace.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                parent.collapseGroup(groupPosition);

                city =  expandablePlaceDetail.get("Where").get(childPosition);

                for (String c: cities) {
                    if(city.equals(c)){
                        setChosenCity(c);
                        chosen_city.setText(c);
                    }
                }
                getActivitiesFromDb();

                //TODO UPDATE DATABASE ;

                return false;
            }

        });



    }

    private void setChosenCity(String city){

        this.chosenCity = city ;
    }

    private void setChosenKind(String chosenKind){

        this.chosenKind = chosenKind ;
    }



    private void getActivitiesFromDb(){



        activityList =  new ArrayList<>();
        final String[] img_url = {null};


        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                activityList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        Actions activity = snapshot.getValue(Actions.class) ;

                    if ( chosenKind == null || chosenCity ==null||
                            chosenKind == null && chosenCity.equals("All") ||
                            chosenCity == null  && chosenKind.equals("All")
                            || chosenKind.equals("All") && chosenCity.equals("All")){

                        img_url[0] = activity.getImg_url();
                        activityList.add(activity);
                    }else{

                        if(activity.getPlace().equals(chosenCity) && activity.getKind().equals(chosenKind)){
                            img_url[0] = activity.getImg_url();
                            activityList.add(activity);
                        }

                    }



                    adapter = new AdapterRecyclerViews( getContext(),getActivity(), activityList
                            , img_url[0],true,false,false,recyclerView);
                    recyclerView.setAdapter(adapter);



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }



        });


    }
 // TODO SET DELETING ACTIVITIES

    public void updateData(List<Actions> activities) {
        activityList.clear();
        activityList.addAll(activities);
        adapter.notifyDataSetChanged();
    }
    public void addItem(int position, Actions viewModel) {
        activityList.add(position, viewModel);
        adapter.notifyItemInserted(position);
    }

    public void removeItem(int position) {
        activityList.remove(position);
        adapter.notifyItemRemoved(position);
        recyclerView.removeViewAt(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, activityList.size());
    }




}
