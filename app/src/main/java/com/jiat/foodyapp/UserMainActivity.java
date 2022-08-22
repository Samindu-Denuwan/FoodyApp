package com.jiat.foodyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserMainActivity extends AppCompatActivity {

    private TextView nameView;
    private ImageButton logoutBtn, editProfileBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        nameView = findViewById(R.id.textViewUserName);
        logoutBtn = findViewById(R.id.imgBtnLogOut);
        editProfileBtn = findViewById(R.id.imgBtnEdit);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMeOffline();

            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open edit profile
                /*startActivity(new Intent(UserMainActivity.this, ProfileEditUserActivity.class));*/
                startActivity(new Intent(UserMainActivity.this, UserNaviActivity.class));
            }
        });

    }


    private void makeMeOffline() {
        progressDialog.setMessage("Login Out...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "false");

        //update value to DB

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //login out success
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed to  update
                        progressDialog.dismiss();
                        Toast.makeText(UserMainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(UserMainActivity.this, LoginActivity.class));
            finish();

        }else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            String accountType = ""+ds.child("accountType").getValue();

                            nameView.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}