package com.example.groceryapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

import com.example.groceryapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterSellerActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_seller);

        createAccount();
    }

    private void createAccount() {

        firebaseAuth.createUserWithEmailAndPassword("admin@sbps.com","123456")
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saveFireBaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void saveFireBaseData() {

        String timestamp ="" + System.currentTimeMillis();

        HashMap<String,Object> hashMap=  new HashMap<>();
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("email","admin@sbps.com");
        hashMap.put("name","Balaji");
        hashMap.put("shopName","Sri Balaji Power System");
        hashMap.put("phone","9876543210");
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("accountType","Admin");
        hashMap.put("online","true");
        hashMap.put("shopOpen","true");

        //save to db
        DatabaseReference ref = FirebaseDatabase.getInstance("https://groceryapp-bb972-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        ref.child(firebaseAuth.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}