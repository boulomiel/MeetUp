package com.rubenmimoun.meetup.app.DrawerFragments.activitiesFragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rubenmimoun.meetup.app.MainMenuPackage.MainMenu;
import com.rubenmimoun.meetup.app.Models.Actions;
import com.rubenmimoun.meetup.app.Models.Route;
import com.rubenmimoun.meetup.app.Models.User;
import com.rubenmimoun.meetup.app.R;
import com.rubenmimoun.meetup.app.direction.BringURL;
import com.rubenmimoun.meetup.app.direction.DirectionFinderListener;
import com.rubenmimoun.meetup.app.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements OnMapReadyCallback,  DirectionFinderListener, LocationListener  {

    private LatLng destinationLocation;
    private LocationManager mLocationManager;
    private Location userLocation ;
    private LatLng userGmpLoc ;
    private FusedLocationProviderClient apiClient;
    private LocationRequest mLocationRequest;
    private ArrayList <Polyline> polyLinePaths ;

    private String name ;
    private GoogleMap mMap;
    private Timer timer;
    private TimerTask task;
    public static final int MY_PERMISSION = 1;
    public static boolean ONMAP =false ;
    private boolean GRANTED ;

    private Button in_btn ;
    private TextView participants_txt ;


    private TextView description_text ;
    private InterstitialAd mInterstitialAd;
    private String UNITID = "HIDDEN" ;
    private String TESTID ="HIDDEN";

    //TODO register to the activity button, set people counter,

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
       mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = getArguments();
        if (bundle != null) {
            // handle your code here.
            destinationLocation = new LatLng(bundle.getDouble("latitude"), bundle.getDouble("longitude"));
            name = bundle.get("name").toString();

            setParticipantsCount();


        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ONMAP = false ;
        mLocationManager.removeUpdates(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        ONMAP = true ;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_maps, container, false);

        description_text =v.findViewById(R.id.description_content);
        in_btn =v.findViewById(R.id.btn_register_activity) ;
        participants_txt = v.findViewById(R.id.number_people) ;

        ONMAP = true ;

        final SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));  //use SuppoprtMapFragment for using in fragment instead of activity  MAPFragment = activity   SupportMapFragment = fragment
        assert mapFragment != null;
        mapFragment.getMapAsync(this);



        if(!PermissionUtils.checkLocationPermission(getContext())){
            checkPermission();
        }else{
            setUserLocation();
        }

        in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewParticipant();
            }

        });


        MobileAds.initialize(getContext(), "ca-app-pub-9747466889192287~8079629754");

       // mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(UNITID);
       // mInterstitialAd.setAdUnitId(TESTID);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                showInterstitial();
            }
        });



        return v ;
    }

    private void setUserLocation(){

        if(PermissionUtils.checkLocationPermission(getContext())){
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000,
                    5, this);
            userLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(userLocation != null){
                userGmpLoc =  new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            }

        }

    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
            mInterstitialAd.show();
        }
    }

    private void checkPermission(){
        //PermissionUtils.requestLocationPermission(getActivity(), MY_PERMISSION);
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION);

    }

    private void sleep(long time){
        try {

            Thread.sleep(time);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addNewParticipant() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DatabaseReference listRef =  FirebaseDatabase.getInstance().getReference("activities").child(name).child("participating");
                User user = dataSnapshot.getValue(User.class);
                            listRef.child(user.getName()).setValue(user.getId());
                            Snackbar.make(getView(),"Enjoy",Snackbar.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }





    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        requestLocationUpdate();
        setDescriptionContent();
        if(PermissionUtils.checkLocationPermission(getContext())){
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userGmpLoc, 16));
            setDestinationMarker(googleMap);
            setPathToDestination(googleMap,destinationLocation);

        }



    }

    private void setDescriptionContent(){

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("activities").child(name) ;
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Actions actions =  dataSnapshot.getValue(Actions.class) ;

                if(actions != null){
                    if(actions.getDescription()!= null){
                        description_text.setText(actions.getDescription());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void requestLocationUpdate(){

        if( ! PermissionUtils.checkLocationPermission(getContext())){
            PermissionUtils.requestLocationPermission(getActivity(),MY_PERMISSION);
            return;
        }


        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                0, this);
        userLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        assert userLocation != null;
        userGmpLoc =  new LatLng(userLocation.getLatitude(), userLocation.getLongitude());



        createLocationRequest();
    }

    private void createLocationRequest() {

        mLocationRequest = LocationRequest.create();
        // priority will influence the use of the gps and the battery, the more accurate it is
        // the more the demand in power will be great
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                // turn it on every 5 seconds
                .setInterval(1000*5)
                // if you already have the location update ever 0.5s
                .setFastestInterval(500);

    }


//    private LocationCallback locationCallback = new LocationCallback(){
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            super.onLocationResult(locationResult);
//            if( locationResult == null) return;
//            apiClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(final Location location) {
//                    if (location != null) {
//                        userLocation = location ;
//                    }
//
//                }
//            });
//
//        }
//
//    };



    private void setParticipantsCount(){

         DatabaseReference listRef =  FirebaseDatabase.getInstance().getReference("activities").child(name).child("participating");

         listRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 if(isAdded()){

                     ArrayList<String> users_participating = new ArrayList<>() ;

                     for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                         String user = snapshot.getValue(String.class);


                         users_participating.add(user) ;

                     }

                     String total_users ;

                     if( users_participating != null){
                         total_users  = users_participating.size() + " " + "Participants" ;
                     }else {
                         total_users = "0 Participants" ;
                     }

                     participants_txt.setText(total_users);
                 }



             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });



    }

    private void setDestinationMarker(GoogleMap googleMap){

        googleMap.addMarker(new MarkerOptions()
                .title(name)
                .position(destinationLocation)

        );
    }

    private void setPathToDestination(final GoogleMap gMap,LatLng destinationLocation){

        new BringURL(getContext(), gMap, this)
                .execute(getUrl(userGmpLoc,
                        destinationLocation,
                        "walking")
                        ,"walking");

        }

    private String getUrl(LatLng origin, LatLng dest, String mode){

        String str_origin = "origin=" +origin.latitude + "," +origin.longitude;
        String str_dest  = "destination=" +dest.latitude + "," +dest.longitude;
        String str_mode =  "mode=" + mode ;
        String parameters =  str_origin + "&" + str_dest + "&" + str_mode;
        String output = "json" ;

        return "https://maps.googleapis.com/maps/api/directions/" +
                output+ "?"
                + parameters
                + "&key="
                + getActivity().getString(R.string.apiKey); //AIzaSyBReocje6XFv7IPLl8poFa8o0LmWDJ0MnA
    }


    @Override
    public void onDirectionFinderStart() {
        if(polyLinePaths != null){
            for (Polyline polyline: polyLinePaths) {
                        polyline.remove();
            }
        }

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        polyLinePaths = new ArrayList<>();
        for (Route route : routes) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .geodesic(true)
                    .color(Color.BLUE)
                    .width(10);


            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
            }

            polyLinePaths.add(mMap.addPolyline(polylineOptions));
        }

    }


    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        if(requestCode ==  MY_PERMISSION  ){
            if( permissions.length >0 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                    && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                requestLocationUpdate();
                setUserLocation();
                onMapReady(mMap);

            }

        }

    }


}
