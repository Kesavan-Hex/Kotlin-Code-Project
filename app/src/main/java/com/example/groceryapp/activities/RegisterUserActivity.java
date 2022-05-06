package com.example.groceryapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RegisterUserActivity extends AppCompatActivity  implements LocationListener {

    private ImageButton backbtn,gpsBtn;
    private ImageView profileIv;
    private EditText nameEt, phoneEt,emailEt,passwordEt,cpasswordEt,addressEt,cityEt
            ,stateEt,countryEt;
    private Button registerBtn;
    private TextView AccountTv;

    //permissions
    private  static  final  int LOCATION_REQUEST_CODE = 100;
    private  static  final  int CAMERA_REQUEST_CODE = 200;
    private  static  final  int STORAGE_REQUEST_CODE = 300;
    //image
    private  static  final  int IMAGE_PICK_GALLERY_CODE = 400;
    private  static  final  int IMAGE_PICK_CAMERA_CODE = 500;

    //permissions Array
    private  String[] locationPermissions;
    private  String[] cameraPermissions;
    private  String[] storagePermissions;

    //image pickup
    private Uri image_uri;

    private double latitude, longtitude;

    private LocationManager locationManager;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        //init UI
        backbtn = findViewById(R.id.backBtn);
        gpsBtn = findViewById(R.id.gpsBtn);
        profileIv = findViewById(R.id.profileIv);
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        passwordEt = findViewById(R.id.passwordEt);
        emailEt = findViewById(R.id.emailEt);
        cpasswordEt = findViewById(R.id.cpasswordEt);
        addressEt = findViewById(R.id.addressEt);
        stateEt = findViewById(R.id.stateEt);
        cityEt = findViewById(R.id.cityEt);
        countryEt = findViewById(R.id.countryEt);
        registerBtn = findViewById(R.id.registerBtn);
        AccountTv = findViewById(R.id.AccountTv);

            //init permissions
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions= new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);






        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //detect Current Location
                if(checkLocationPermission()){
                    detectLocation();
                }
                else {
                    // not allowed, req
                    requestLocationPermission();
                }

            }
        });

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pick Image
                showImagePickDialog();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //register user
                inputData();
            }
        });

        AccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterUserActivity.this, LoginActivity.class));
            }
        });
    }

    private  String fullName,email,phoneNum,password,cpassword,address,city,state,country;
    private void inputData() {
        //input data
        fullName = nameEt.getText().toString().trim();
        email = emailEt.getText().toString().trim();
        phoneNum = phoneEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();
        cpassword = cpasswordEt.getText().toString().trim();
        address = addressEt.getText().toString().trim();
        city = cityEt.getText().toString().trim();
        state = stateEt.getText().toString().trim();
        country = countryEt.getText().toString().trim();

        //validate Data
        if(TextUtils.isEmpty(fullName)){
            Toast.makeText(this, "Enter Valid Name...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Enter Valid Email...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phoneNum)){
            Toast.makeText(this, "Enter Valid Phone Number...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter Valid Password...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(cpassword)){
            Toast.makeText(this, "Enter Valid Confirm Password...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(address)){
            Toast.makeText(this, "Enter Valid Address...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(city)){
            Toast.makeText(this, "Enter Valid Cty...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(state)){
            Toast.makeText(this, "Enter Valid State...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(country)){
            Toast.makeText(this, "Enter Valid Country...!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(cpassword)){
            Toast.makeText(this, "Password Mismatched", Toast.LENGTH_SHORT).show();
            return;
        }

        createAccount();
        createAccount1();
    }

    private void createAccount() {
        progressDialog.setMessage("Creating Account...!");
        progressDialog.show();

        //create Account
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //account Created
                        saverFirebaseDate();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saverFirebaseDate() {
        progressDialog.setMessage("Saving...!");

        String timestamp = ""+System.currentTimeMillis();

        if(image_uri==null){
            //save info without Image

            //setup data
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("email",""+email);
            hashMap.put("name",""+fullName);
            hashMap.put("phone",""+phoneNum);
            hashMap.put("address",""+address);
            hashMap.put("city",""+city);
            hashMap.put("state",""+state);
            hashMap.put("country",""+country);
            hashMap.put("latitude",""+latitude);
            hashMap.put("longtitude",""+longtitude);
            hashMap.put("timestamp",""+timestamp);
            hashMap.put("accountType","user");
            hashMap.put("online","true");
            hashMap.put("profileImage","");

            //save to DB
            DatabaseReference ref = FirebaseDatabase.getInstance("https://groceryapp-bb972-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //db Update
                            progressDialog.dismiss();
                            Toast.makeText(RegisterUserActivity.this, "Account Created Suucessfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterUserActivity.this, DashboardUserActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }else {
            //save info with Image

            //name and path of image
            String filePathandName = "profile_images/"+""+firebaseAuth.getUid();
            //upload Image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathandName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //get url of uploadaed Image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri dounloadImageUrl = uriTask.getResult();

                            if(uriTask.isSuccessful()){
                                //setup data
                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                hashMap.put("email",""+email);
                                hashMap.put("name",""+fullName);
                                hashMap.put("phone",""+phoneNum);
                                hashMap.put("address",""+address);
                                hashMap.put("city",""+city);
                                hashMap.put("state",""+state);
                                hashMap.put("country",""+country);
                                hashMap.put("latitude",""+latitude);
                                hashMap.put("longtitude",""+longtitude);
                                hashMap.put("timestamp",""+ timestamp);
                                hashMap.put("accountType","user");
                                hashMap.put("online","true");
                                hashMap.put("profileImage","" + dounloadImageUrl);

                                //save to DB
                                DatabaseReference ref = FirebaseDatabase.getInstance("https://groceryapp-bb972-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //db Update
                                                progressDialog.dismiss();
                                                Toast.makeText(RegisterUserActivity.this, "Account Created Suucessfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RegisterUserActivity.this,DashboardUserActivity.class));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(RegisterUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                            Toast.makeText(RegisterUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void createAccount1() {

        firebaseAuth.createUserWithEmailAndPassword("admin2@sbps.com","123456")
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saveFireBaseData1();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void saveFireBaseData1() {

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

    private void showImagePickDialog() {
        //options to display in dialog
        String[] options = {"Camera", "Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if(which == 0){
                            //camera clicked
                            if (checkCameraPermissions()) {
                                //permission Allowed
                                pickFromCamera();
                            }else {
                                //permission not Allowed
                                requestCameraPermission();
                            }

                        }else {
                            //Galaery clicked
                            if (checkStroagePermissions()) {
                                //permission Allowed
                                pickFromGallery();
                            }else {
                                //permission not Allowed
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private  void  pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private  void  pickFromCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private void detectLocation() {
        Toast.makeText(this, "Please Wait..!", Toast.LENGTH_LONG).show();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }

    private void findAddress() {
        //find address
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longtitude,1);
            String address = addresses.get(0).getAddressLine(0); //compleete addess
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();

            //set Address
            countryEt.setText(country);
            stateEt.setText(state);
            cityEt.setText(city);
            addressEt.setText(address);
        }
        catch (Exception e){

        }
    }

    private boolean checkLocationPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,locationPermissions,LOCATION_REQUEST_CODE);
    }

    private boolean checkStroagePermissions(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void  requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);

    }
    private boolean checkCameraPermissions(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result && result1;

    }
    private void  requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    // loction Detected
        latitude = location.getLatitude();
        longtitude = location.getLongitude();

        findAddress();
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
        Toast.makeText(this, "Pleable Turn On Location...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean locationAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted){
                        //permission Allowed
                        detectLocation();
                    }
                    else{
                        //permission Denied

                        Toast.makeText(this, "Location Permission Mandratory..!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1]== PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && storageAccepted){
                        //permission Allowed
                        pickFromCamera();
                    }
                    else{
                        //permission Denied

                        Toast.makeText(this, "Camera Permission Mandratory..!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){

                    boolean storageAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;

                    if(storageAccepted){
                        //permission Allowed
                        pickFromGallery();
                    }
                    else{
                        //permission Denied

                        Toast.makeText(this, "Storage Permission Mandratory..!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                //get picked image
                image_uri = data.getData();
                profileIv.setImageURI(image_uri);
            }
            else  if(requestCode == IMAGE_PICK_CAMERA_CODE){
                profileIv.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}