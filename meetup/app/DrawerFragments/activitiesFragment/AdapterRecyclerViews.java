package com.rubenmimoun.meetup.app.DrawerFragments.activitiesFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rubenmimoun.meetup.app.BuildConfig;
import com.rubenmimoun.meetup.app.DrawerFragments.MyActivitiesFragment.RemoveView;
import com.rubenmimoun.meetup.app.Models.Actions;
import com.rubenmimoun.meetup.app.Models.User;
import com.rubenmimoun.meetup.app.Notification.Notification;
import com.rubenmimoun.meetup.app.R;
import com.rubenmimoun.meetup.app.utils.MapFragmentChoice;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRecyclerViews extends RecyclerView.Adapter<AdapterRecyclerViews.ActionsViewHolder> {

    private FragmentActivity activity_Fragment;
    private WeakReference <Context> mContext ;
    private ArrayList<Actions> activityList;
    private String image_url ;

    private DatabaseReference mRef ;
    private boolean toMap;
    private boolean deledteFromUser ;
    private  boolean modify ;
    private boolean gotData ;
    private RecyclerView view;
    FirebaseUser firebaseUser ;
    private Notification notification ;



    public AdapterRecyclerViews(Context context,
                                FragmentActivity activity,
                                ArrayList<Actions> list,
                                String image_url, boolean toMap,boolean deledteFromUser,boolean modify, RecyclerView view){

        this.mContext = new WeakReference<>(context) ;
        this.activityList = list ;
        this.image_url = image_url ;
        this.activity_Fragment = activity;
        this.toMap = toMap;
        this.view = view ;
        this.deledteFromUser = deledteFromUser ;
        this.modify = modify ;


    }



    @NonNull
    @Override
    public ActionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_activities, parent, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        return  new ActionsViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ActionsViewHolder holder, final int position) {

        final Actions activity = activityList.get(position) ;

        holder.activity_name.setText(activity.getName());
        holder.activity_time.setText(activity.getTime());
        holder.activity_date.setText(activity.getDate());
        holder.activity_place.setText(activity.getPlace());
        holder.activity_expected_end.setText(activity.getEnd_time());

        if(activity.getImg_url().equals("default")){
            holder.activity_img.setImageResource(R.drawable.cheers);
        }else{
            Glide.with(mContext.get()).load(activity.getImg_url()).into(holder.activity_img);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if( v != null){

                    final String adress= activity.getAdress() ;
                    final String name = activity.getName() ;
                    mRef = FirebaseDatabase.getInstance().getReference("activities").child(String.valueOf(name));

                    if(!deledteFromUser &&!modify){
                        if(toMap){

                            MapFragmentChoice choice = new MapFragmentChoice(activity_Fragment,mRef,adress,name);
                            choice.setChoice();
                            Snackbar.make(activity_Fragment.findViewById(R.id.flContent)
                                    ,"Loading data ...", Snackbar.LENGTH_LONG).show();

                        }

                    }else if(deledteFromUser && !modify){


                        new AlertDialog.Builder(activity_Fragment)
                                .setMessage("Would you like to remove or modify this event ?")
                                .setPositiveButton("Modify", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Fragment fragment = new FragmentModifiyEvent();
                                        try {
                                            passDataToModifier(fragment,name);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        FragmentManager fragmentManager =  activity_Fragment.getSupportFragmentManager() ;

                                        fragmentManager.beginTransaction().replace(R.id.flContent,fragment ).commit();


                                    }
                                }).setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                removeItem(position,mRef);
                                sendNotifications(mContext.get(),activity_Fragment,mRef,name);
                            }
                        })
                                .create()
                                .show();

                    }



                }

            }
        });

    }


    private void sendNotifications(final Context context, final Activity activity, DatabaseReference activitiesRef, String name){


        final String id = FirebaseDatabase.getInstance().getReference("activities").child(name).child("participating").child(firebaseUser.getUid()).getKey();
        System.out.println("IIIIDDD" + id);
        DatabaseReference mref =  FirebaseDatabase.getInstance().getReference("users") ;
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    User user  =  snapshot.getValue(User.class) ;

                        if( user.getId().equals(id)){

                            notification = new Notification(context) ;
                            notification.createNotificationChannel("TEST","TEST");
                            notification.createNotification("Event removed","The event you registered to has been remove by its creator",activity);


                        }



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void notifyUser(final Context context,final String id,final Activity activity){

        final DatabaseReference nRef = FirebaseDatabase.getInstance().getReference("users");
        nRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user =  snapshot.getValue(User.class) ;

                    if(user.getId().equals(id)){

                        notification = new Notification(context) ;
                        notification.createNotificationChannel("TEST","TEST");
                        notification.createNotification("Event removed","The event you registered to has been remove by its creator",activity);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removeItem(int position, final DatabaseReference mRef) {

        RecyclerView v = activity_Fragment.findViewById(R.id.joined_activity_recycle_view);


        if(!deledteFromUser){

            final String id = FirebaseDatabase.getInstance().getReference("activities").child("participating").child(firebaseUser.getUid()).getKey();
            this.notifyItemRemoved(position);
            activityList.remove(position);
            v.removeViewAt(position);
            this.notifyItemRangeChanged(position, activityList.size());
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for ( DataSnapshot snapshot: dataSnapshot.getChildren()) {

                        Actions actions  =  snapshot.getValue(Actions.class) ;
                        if(actions.getParticipating() != null){
                            if( actions.getParticipating().containsValue(id)){
                                snapshot.getRef().child("participating").child(id).getParent().removeValue();

                            }
                        }


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }else{
            activityList.remove(position);
            view.removeViewAt(position);
            mRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Snackbar.make(activity_Fragment.findViewById(R.id.flContent),"Removed",Snackbar.LENGTH_SHORT);
                }
            });

            this.notifyItemRemoved(position);

        }



    }

    private void passDataToModifier(final Fragment fragment,String name) throws InterruptedException {


        DatabaseReference mref =  FirebaseDatabase.getInstance().getReference("activities").child(name);

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Actions actions = dataSnapshot.getValue(Actions.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("name", actions.getName());
                    bundle.putString("adress", actions.getAdress());
                    bundle.putString("kind", actions.getKind());
                    bundle.putString("place",actions.getPlace());
                    bundle.putString("date",actions.getDate());
                    bundle.putString("time", actions.getTime());
                    bundle.putString("end" , actions.getEnd_time()) ;
                    bundle.putString("img_url" , actions.getImg_url());
                    fragment.setArguments(bundle);





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Thread.sleep(1500);


    }



    @Override
    public int getItemCount() {
        Log.i("call","getItemCount");
        return activityList.size() ;
    }



//        //TODO set remove if activity's date s passed

    public class ActionsViewHolder extends  RecyclerView.ViewHolder{

        private TextView activity_name ;
        private TextView activity_time ;
        private TextView activity_place ;
        private TextView activity_expected_end  ;
        private TextView activity_date ;
        private CircleImageView activity_img ;
        private CardView card ; 

        public ActionsViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.second_card);
            activity_name = itemView.findViewById(R.id.activity_name);
            activity_place = itemView.findViewById(R.id.activity_place);
            activity_time = itemView.findViewById(R.id.activity_hour);
            activity_date = itemView.findViewById(R.id.activity_date);
            activity_expected_end =  itemView.findViewById(R.id.activity_endtime) ;
            activity_img = itemView.findViewById(R.id.activity_img) ;


        }




    }
}
