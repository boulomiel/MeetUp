package com.rubenmimoun.meetup.app.AuthentificationPackage;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rubenmimoun.meetup.app.MainMenuPackage.MainMenu;
import com.rubenmimoun.meetup.app.Models.User;
import com.rubenmimoun.meetup.app.R;

import java.util.HashMap;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {


    private EditText name ;
    private EditText password ;
    private EditText email ;
    private EditText confimation_password ;
    private TextView verified ;
    private Button register ;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser ;
    private DatabaseReference db ;
    private String uid ;
    private User user_  ;
    private double lon ;
    private double lat ;
    private String online ;

    private ProgressDialog progressDialog ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_register, container, false);

        name = v.findViewById(R.id.et_name_register);
        password =v.findViewById(R.id.et_password_register) ;
        email =v.findViewById(R.id.et_email_register);
        confimation_password =v.findViewById(R.id.et_repassword_register);
        register=v.findViewById(R.id.btn_register);



        mAuth =  FirebaseAuth.getInstance() ;

        db = FirebaseDatabase.getInstance().getReference() ;
        uid = mAuth.getUid() ;

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Registering ...");



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email_txt = email.getText().toString();
                String passWord_txt = password.getText().toString() ;
                String name_txt = name.getText().toString();
                String status = "offline";
                String imageURL = "default";

                user_ = new User(name_txt,email_txt,passWord_txt,imageURL,status);

                register(email_txt,passWord_txt,name_txt, imageURL,status);


            }
        });

        return  v ;

    }




    private void register(final String email, final String password, final String name, final String imageURL,final String status){

        progressDialog.show();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty( password)) {
            Toast.makeText(getContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser() ;
                            final String userId =  user.getUid() ;

                            HashMap<String , String> hashMap =  new HashMap<>() ;
                            hashMap.put("id", userId) ;
                            hashMap.put("name", name) ;
                            hashMap.put("email", email) ;
                            hashMap.put("password", password) ;
                            hashMap.put("imageURL",imageURL);
                            hashMap.put("status",status);


                            progressDialog.dismiss();


                            db.child("users").child(userId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(),"You are now registered",Toast.LENGTH_SHORT).show();
                                    Intent intent  = new Intent(getContext(), MainMenu.class) ;
                                    intent.putExtra("userid", userId);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showError(e.getMessage());
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(getActivity(), "Registration failed.", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });





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