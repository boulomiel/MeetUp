package com.rubenmimoun.meetup.app.utils;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class Modifier {

    private Context context ;
    private FragmentActivity activity ;
    private DatabaseReference mref ;

    private CircleImageView image_activity  ;
    private TextView chosen_activity_type ;
    private TextView picked_where ;
    private EditText activity_name ;
    private EditText activity_time ;
    private EditText activity_endtime ;
    private EditText activity_date ;
    private EditText adress ;
    private EditText activity_description ;
}
