package com.rubenmimoun.meetup.app.ChatUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rubenmimoun.meetup.app.DrawerFragments.FragmentHome;
import com.rubenmimoun.meetup.app.Models.Chat;
import com.rubenmimoun.meetup.app.Models.User;
import com.rubenmimoun.meetup.app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentChat extends Fragment {

    private CircleImageView profile_pic;
    private TextView username ;
    private ImageButton send_btn ;
    private EditText text_send ;


    private FirebaseUser firebaseUser ;
    private DatabaseReference reference ;

    Intent intent ;
    private String userid ;

    private RecyclerView recyclerView ;
    private MessageAdapter adapter ;
    private List<Chat> chatList ;

    private ValueEventListener seenListener  ;

    private String city ;
    private String nameline = "";

    private boolean notify = false ;


    public FragmentChat(){}

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = getArguments();
        if (bundle != null) {
            // handle your code here.

            city = bundle.get("user_city").toString();


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View v  = inflater.inflate(R.layout.fragment_message,container,false);
        recyclerView =  v.findViewById(R.id.recycler_view_message_activity) ;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =  new LinearLayoutManager(getContext()) ;
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);


        send_btn= v.findViewById(R.id.btn_send);
        text_send= v.findViewById(R.id.text_send);
        username= v.findViewById(R.id.username_message);
        profile_pic = v.findViewById(R.id.profil_image_message);
        username = v.findViewById(R.id.username_message);




        firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        getOtherUsers();


        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify= true ;

                String message =  text_send.getText().toString() ;
                if(!message.equals("")){
                    sendMessage(firebaseUser.getUid(),nameline, message);
                }else{
                    Toast.makeText(getActivity(),
                            "You cannot send an empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");

            }
        });


        return v ;
    }


    private void getOtherUsers(){

        DatabaseReference mref =  FirebaseDatabase.getInstance().getReference("users") ;

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> users_names  = new ArrayList<>();

                for ( DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    User user =  snapshot.getValue(User.class) ;

                    if(user.getId() != firebaseUser.getUid()){
                        users_names.add(user.getName()) ;
                    }



                }

                if( users_names.size() < 2 ){
                    setResearchDialog();
                }else {
                    //seenMessage(userid);
                    setuserProfileBar(users_names);
                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setResearchDialog(){

        new AlertDialog.Builder(getContext())
                .setTitle("No one to chat with ...")
                .setMessage("Hey buddy, we currently cannot find someone to chat to, would you like to wait 30s or go back to the menu ?")
                .setPositiveButton("Wait", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog pb =  new ProgressDialog(getContext());
                        pb.show();
                        pb.setCancelable(false);
                        pb.setMessage("Looking for another chatter...");
                        Timer timer =  new Timer();
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                pb.dismiss();
                                FragmentManager manager = getFragmentManager() ;
                                manager.beginTransaction().replace(R.id.flContent,new FragmentHome()).commit();
                            }
                        };
                        timer.schedule(task,1000*30);
                    }
                })
                .setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentManager manager = getFragmentManager() ;
                        manager.beginTransaction().replace(R.id.flContent,new FragmentHome()).commit();
                    }
                })
                .create().show();

    }

    private void setuserProfileBar(final ArrayList<String> users_name){
        //TODO set list of users instead of one current user to talk tp

        if(users_name == null) return;

        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    User user =  s.getValue(User.class) ;
                    if( user.getId() != firebaseUser.getUid()){

                        for (int i = 0; i <users_name.size() ; i++) {
                            if(user.getName().equals(users_name.get(i))){
                                nameline += user.getName()+", " ;
                            }
                        }
                    }

                }
                User user = dataSnapshot.getValue(User.class);

                username.setText(nameline);

                if(users_name.size() == 0){
                    if(user.getImageURL().equals("default") || user.getImageURL() == null){
                        profile_pic.setImageResource(R.drawable.unknown);
                    }else{
                        Glide.with(getContext()).load(user.getImageURL()).into(profile_pic);

                    }

                }


                readMessage(firebaseUser.getUid(), user.getImageURL());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }



        });
    }

    private void seenMessage(final String userid){

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat =  snapshot.getValue(Chat.class);

                    assert chat != null;
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>() ;
                        hashMap.put("isseen", true) ;
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String sender , final String receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);

        reference.child("Chats").push().setValue(hashMap);



        final String msg = message ;
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()) ;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class) ;
                if( notify){
                   //sendNotification(receiver, user.getUsername(), msg);
                }


                notify = false ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void readMessage(final String myid, final String imageurl){

        chatList = new ArrayList<>();
        reference =  FirebaseDatabase.getInstance().getReference("Chats") ;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){


                    Chat chat =  snapshot.getValue(Chat.class) ;
                    chatList.add(chat);

                    adapter =  new MessageAdapter(getContext(), chatList,imageurl);
                    recyclerView.setAdapter(adapter);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
