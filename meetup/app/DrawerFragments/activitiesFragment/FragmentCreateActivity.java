package com.rubenmimoun.meetup.app.DrawerFragments.activitiesFragment;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rubenmimoun.meetup.app.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentCreateActivity extends Fragment {

    private CircleImageView image_activity  ;
    private TextView chosen_activity_type ;
    private TextView picked_where ;
    private EditText activity_name ;
    private EditText activity_time ;
    private EditText activity_endtime ;
    private EditText activity_date ;
    private EditText adress ;
    private EditText activity_description ;


    private Button create_activity ;

    private DatabaseReference db ;
    private FirebaseUser firebaseUser ;
    private StorageReference storageReference ;
    public static final int IMAGE_REQUEST = 1 ;
    private Uri image_uri  ;
    private StorageTask upload_task ;


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
    private String activityName ;


    public FragmentCreateActivity(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_activity_create,container,false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        listInit();

        image_activity =  v.findViewById(R.id.define_activity_img);
        chosen_activity_type =v.findViewById(R.id.type_picked);
        picked_where = v.findViewById(R.id.picked_where) ;
        create_activity = v.findViewById(R.id.create_activity_btn);
        activity_name =v.findViewById(R.id.create_activity_name);
        activity_time = v.findViewById(R.id.create_activity_time);
        activity_endtime =v.findViewById(R.id.create_activity_expected_end);
        activity_date =v.findViewById(R.id.create_activity_date) ;
        adress =v.findViewById(R.id.create_activity_adress) ;
        activity_description =v.findViewById(R.id.activity_description);

        expandableListType = v.findViewById(R.id.type_list);
        expandableTypeDetail = ExpandableListDataPump.getData("Kind",type);
        expandableTitleType = new ArrayList<String>(expandableTypeDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(getContext(), expandableTitleType, expandableTypeDetail);
        expandableListType.setAdapter(expandableListAdapter);

        expandableListPlace = v.findViewById(R.id.create_activity_place);
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
                chosen_activity_type.setText(activity);

                switch (activity){
                    case  "Bar" :
                        image_activity.setImageResource(R.drawable.bar);
                       image_uri = fromRessourceToUri(R.drawable.bar);
                        break ;
                    case "Restaurant" :
                        image_activity.setImageResource(R.drawable.restaurant);
                        image_uri = fromRessourceToUri(R.drawable.restaurant);
                        break;
                    case "Party" :
                        image_activity.setImageResource(R.drawable.party);
                        image_uri = fromRessourceToUri(R.drawable.party);
                        break ;
                    case "Else" :
                        image_activity.setImageResource(R.drawable.else_);
                        image_uri = fromRessourceToUri(R.drawable.else_);
                        break ;
                    case "Sport" :
                        image_activity.setImageResource(R.drawable.sport);
                        image_uri = fromRessourceToUri(R.drawable.sport);
                        break ;
                    case "Cultural" :
                        image_activity.setImageResource(R.drawable.culture);
                        image_uri = fromRessourceToUri(R.drawable.culture);
                        break;
                    case "Cinema" :
                        image_activity.setImageResource(R.drawable.cinema);
                        image_uri = fromRessourceToUri(R.drawable.cinema);
                        break;

                }


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

               city =  expandablePlaceDetail.get("Where").get(childPosition) ;
                picked_where.setText(city);


                //TODO UPDATE DATABASE ;

                return false;
            }
        });

        create_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                            .setTitle("Forgot Address ?")
                            .setMessage("Are you sure all the details are correct ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    createNewActivity();
                                    uploadBitmap();

                                    image_activity.setImageURI(null);
                                    chosen_activity_type.setText("");
                                    picked_where.setText("");
                                    activity_name.setText("");
                                    activity_time.setText("");
                                    activity_endtime.setText("");
                                    activity_date.setText("");
                                    adress.setText("");
                                    activity_description.setText("");

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                            builder.show();




            }
        });

        return v ;
    }


    private void listInit(){

        cities = new ArrayList<>();
        cities.add("Tel Aviv");
        cities.add("Herzliya");
        cities.add("Netanya");
        cities.add("Ramat Gan");
        cities.add("Givatayim");

        type = new ArrayList<>();
        type.add("Bar") ;
        type.add("Cinema");
        type.add("Cultural");
        type.add("Party") ;
        type.add("Restaurant");
        type.add("Sport");
        type.add("Else");


    }


    public void createNewActivity() {

            activityName = activity_name.getText().toString();
            String activityTime = activity_time.getText().toString() ;
            String activityEndTime = activity_endtime.getText().toString() ;
            String activityDate =  activity_date.getText().toString() ;
            String kind = chosen_activity_type.getText().toString() ;
            String where =  picked_where.getText().toString() ;
            String adress_str =  adress.getText().toString() ;
            String creator =  firebaseUser.getUid() ;
            String description = activity_description.getText().toString();


        DatabaseReference mRef=  FirebaseDatabase.getInstance().getReference("activities").child(activityName) ;

          if(checkFields(activityName,activityDate,activityTime,activityEndTime,adress_str)){

              HashMap<String,String> activityMap =  new HashMap<>();
              activityMap.put("creator",creator) ;
              activityMap.put("kind", kind) ;
              activityMap.put("name", activityName) ;
              activityMap.put("place",where) ;
              activityMap.put("adress",adress_str) ;
              activityMap.put("date",activityDate);
              activityMap.put("time", activityTime) ;
              activityMap.put("end" , activityEndTime) ;
              activityMap.put("img_url" , "default") ;
              if(description != null){
                  activityMap.put("description",description);
              }


              mRef.setValue(activityMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                      Snackbar.make(getView(),"Event successfully created", Snackbar.LENGTH_SHORT).show();
                  }
              }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      showError(e.getMessage());
                  }
              });


          }

    }


    private Boolean checkFields(String activityname, String activityDate, String activitytime, String activityEndtime,String activity_address){

        if(activityname.equals("") || activityDate.equals("")|| activitytime.equals("")|| activityEndtime.equals("") || activity_address.equals("") ){
            Toast.makeText(getContext(), "One of the required field is empty", Toast.LENGTH_SHORT).show();
        }


        String [] activityDateArr =  activityDate.split("/");


            int day  =Integer.parseInt(activityDateArr[0]) ;
            int month =  Integer.parseInt(activityDateArr[1]) ;
            String year = activityDateArr[2] ;

        String [] dateTimeArr = activitytime.split(":") ;
        int hours =  Integer.parseInt(dateTimeArr[0] );
        int minutes = Integer.parseInt(dateTimeArr[1]) ;


        String [] endTimeArr = activitytime.split(":") ;
        int hoursEnd =  Integer.parseInt(endTimeArr[0] );
        int minutesEnd = Integer.parseInt(endTimeArr[1]) ;



        try{

            if(activityDateArr.length == 3){

                if(day> 31){

                    activity_date.setError("Please insert a correct day value");
                    return false;
                }
                if(month > 12){

                    Toast.makeText(getContext(), "Please insert a correct month value", Toast.LENGTH_SHORT).show();
                    activity_date.setError("Please insert a correct month value");
                    return false;
                }

                if(year.length() < 4){

                    Toast.makeText(getContext(), "Please insert a correct year value", Toast.LENGTH_SHORT).show();
                    activity_date.setError("Please insert a year  value");
                    return false;
                }

            }else {
                Toast.makeText(getContext(), "Please insert a correct date : DD//MM/YYYY value", Toast.LENGTH_SHORT).show();
                return false ;
            }


            if( hours > 24){
                Toast.makeText(getContext(), "Please insert a correct hour value", Toast.LENGTH_SHORT).show();
                activity_time.setError("Please insert a correct hour value");
                return false;
            }


            if( minutes > 60){
                Toast.makeText(getContext(), "Please insert a correct minutes value", Toast.LENGTH_SHORT).show();
                activity_time.setError("Please insert a correct minutes value");
                return false;
            }


            if( hoursEnd > 24){
                Toast.makeText(getContext(), "Please insert a correct hour value", Toast.LENGTH_SHORT).show();
                activity_endtime.setError("Please insert a correct hour value");
                return false;
            }


            if( minutesEnd > 60){
                Toast.makeText(getContext(), "Please insert a correct minutes value", Toast.LENGTH_SHORT).show();
                activity_endtime.setError("Please insert a correct hour value");
                return false;
            }

        }catch (NullPointerException e){
                Toast.makeText(getContext(), "Date field is not filled properly", Toast.LENGTH_SHORT).show();
            }

        if(chosen_activity_type.getText().equals("")){
            Toast.makeText(getContext(), "You must choose a kind of activity", Toast.LENGTH_SHORT).show();
            return false;
        }

        if( picked_where.getText().equals("")){
            Toast.makeText(getContext(), "You must decide where it takes place", Toast.LENGTH_SHORT).show();
            return false;
        }

        if( adress.getText().equals("")){
            Toast.makeText(getContext(), "You must set an adress", Toast.LENGTH_SHORT).show();
            return false;
        }

        return  true ;


    }




    private void uploadBitmap(){

        Bitmap bitmap = ((BitmapDrawable) image_activity.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getContext(), "Image could not be uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });


        if(image_uri  != null){
            final StorageReference file_reference =
                    storageReference.child(System.currentTimeMillis()
                            +"."+ image_uri);

            upload_task = file_reference.putFile(image_uri);

            upload_task.continueWithTask(new Continuation< UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if( !task.isSuccessful()){
                        throw  task.getException() ;
                    }

                    return  file_reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task <Uri>task) {
                    if( task.isSuccessful()){

                        Uri download_uri = task.getResult() ;
                        String mUri = download_uri.toString() ;



                        db = FirebaseDatabase.getInstance().getReference("activities").child(activityName) ;

                        HashMap<String, Object>  hashMap =  new HashMap<>() ;

                        hashMap.put("img_url",mUri) ;
                        db.updateChildren(hashMap) ;


                    }else{
                        Toast.makeText(getContext(),"Failed", Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                    showError(e.getMessage());
                }
            });


        }else{

            Toast.makeText(getContext(),"No image selected", Toast.LENGTH_SHORT).show();


        }

    }


    private Uri fromRessourceToUri(int resourceId){

        Resources resources = getContext().getResources();
        Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resourceId))
                .appendPath(resources.getResourceTypeName(resourceId))
                .appendPath(resources.getResourceEntryName(resourceId))
                .build();


        System.out.println(uri.toString());

        return  uri ;
    }


    private void showError(String message){
        new AlertDialog.Builder(getContext())
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }







}
