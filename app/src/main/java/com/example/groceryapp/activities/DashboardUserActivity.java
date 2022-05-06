package com.example.groceryapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryapp.EditProfileUserActivity;
import com.example.groceryapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class DashboardUserActivity extends AppCompatActivity {

    private TextView nameTv,emailTv,phoneTv,tabProductTv,tabOrdersTv;
    private ImageButton logoutBtn,ueditBtn,backBtn;
    private ImageView profileIv;
    private RelativeLayout shopsRl,ordersRl;
    private RecyclerView shopsRv;



    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_user);

        nameTv = findViewById(R.id.nameTv);
        logoutBtn=findViewById(R.id.logoutBtn);
        ueditBtn=findViewById(R.id.ueditBtn);
        backBtn=findViewById(R.id.backBtn);
        profileIv=findViewById(R.id.profileIv);
        emailTv=findViewById(R.id.emailTv);
        phoneTv=findViewById(R.id.phoneTv);
        tabProductTv=findViewById(R.id.tabProductTv);
        tabOrdersTv=findViewById(R.id.tabOrdersTv);
        ordersRl=findViewById(R.id.ordersRl);



        //////
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait..!");
        progressDialog.setCanceledOnTouchOutside(false);
        //////
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();






        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMeOffline();

            }
        });





        ueditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open edit activity
                startActivity(new Intent(DashboardUserActivity.this, EditProfileUserActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });






    }





    private void makeMeOffline() {
        //chevking User
        progressDialog.setMessage("Logging  Out...");

        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("online","false");
        //update to db
        DatabaseReference ref = FirebaseDatabase.getInstance("https://groceryapp-bb972-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //update Successfully
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(DashboardUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(DashboardUserActivity.this, LoginActivity.class));
            finish();
        }else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://groceryapp-bb972-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            String email = ""+ds.child("email").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String accountType = ""+ds.child("accountType").getValue();

                            nameTv.setText(name);
                            emailTv.setText(email);
                            phoneTv.setText(phone);

                            try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_person_outline_24).into(profileIv);

                            }catch ( Exception e)
                            {
                                profileIv.setImageResource(R.drawable.ic_baseline_person_outline_24);
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}