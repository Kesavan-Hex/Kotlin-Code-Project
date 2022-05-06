package com.example.groceryapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groceryapp.Constants;
import com.example.groceryapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditProductActivity extends AppCompatActivity {

    private  String productId;

    private ImageButton backBtn;
    private ImageView producticonIv;
    private EditText titleEt,descriptionEt,quantityEt,priceEt,dpriceEt,dpercenEt;
    private TextView categoryTv;
    private SwitchCompat switcSc;
    private Button updateProudctBtn;


    //permission
    private  static  final  int CAMERA_REQUEST_CODE = 200;
    private  static  final  int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    private  static  final  int IMAGE_PICK_GALLERY_CODE = 400;
    private  static  final  int IMAGE_PICK_CAMERA_CODE = 500;
    //permission arrays
    private  String[] cameraPermissions;
    private  String[] storagePermissions;

    private Uri image_uri;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        backBtn = findViewById(R.id.backBtn);
        producticonIv = findViewById(R.id.producticonIv);
        titleEt = findViewById(R.id.titleEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        categoryTv = findViewById(R.id.categoryTv);
        quantityEt = findViewById(R.id.quantityEt);
        priceEt = findViewById(R.id.priceEt);
        switcSc = findViewById(R.id.switcSc);
        dpercenEt = findViewById(R.id.dpercenEt);
        dpriceEt = findViewById(R.id.dpriceEt);
        updateProudctBtn = findViewById(R.id.updateProudctBtn);

        productId = getIntent().getStringExtra("productId");

        dpercenEt.setVisibility(View.GONE);
        dpriceEt.setVisibility(View.GONE);

        //init permissions
        cameraPermissions= new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //setup progresss bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //setup FB
        firebaseAuth = FirebaseAuth.getInstance();
        loadProductDetails();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        switcSc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    dpriceEt.setVisibility(View.VISIBLE);
                    dpercenEt.setVisibility(View.VISIBLE);
                }
                else{
                    dpriceEt.setVisibility(View.GONE);
                    dpercenEt.setVisibility(View.GONE);
                }
            }
        });

        producticonIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show dialog to pick image
                showImagePickDialog();
            }
        });

        categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pick Category
                categoryDialog();
            }
        });

        updateProudctBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });
    }

    private void loadProductDetails() {

        DatabaseReference reference = FirebaseDatabase.getInstance("https://groceryapp-bb972-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Get data
                        String id = ""+snapshot.child("productId").getValue();
                        String productTitle = ""+snapshot.child("productTitle").getValue();
                        String productDescription = ""+snapshot.child("productDescription").getValue();
                        String productCategory = ""+snapshot.child("productCategory").getValue();
                        String productQuantity = ""+snapshot.child("productQuantity").getValue();
                        String productIcon = ""+snapshot.child("productIcon").getValue();
                        String originalPrice = ""+snapshot.child("originalPrice").getValue();
                        String discountPrice = ""+snapshot.child("discountPrice").getValue();
                        String discountNote = ""+snapshot.child("discountNote").getValue();
                        String discountAvailable = ""+snapshot.child("discountAvailable").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();

                        if(discountAvailable.equals("true")){
                            switcSc.setChecked(true);

                            dpriceEt.setVisibility(View.VISIBLE);
                            dpercenEt.setVisibility(View.VISIBLE);
                        }
                        else{
                            switcSc.setChecked(false);

                            dpriceEt.setVisibility(View.GONE);
                            dpercenEt.setVisibility(View.GONE);
                        }

                        titleEt.setText(productTitle);
                        descriptionEt.setText(productDescription);
                        categoryTv.setText(productCategory);
                        dpercenEt.setText(discountNote);
                        quantityEt.setText(productQuantity);
                        priceEt.setText(originalPrice);
                        dpercenEt.setText(discountPrice);

                        try{
                            Picasso.get().load(productIcon).placeholder(R.drawable.ic_baseline_shopping_cart_24).into(producticonIv);
                        }catch (Exception e){
                            producticonIv.setImageResource(R.drawable.ic_baseline_add_shopping_cart_primary);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String productTitle,productDesc,productcate,quantity,oPrice,dPrice,dNote;
    private boolean discountAvailable = false;

    private void inputData() {
        productTitle = titleEt.getText().toString().trim();
        productDesc = descriptionEt.getText().toString().trim();
        quantity = quantityEt.getText().toString().trim();
        oPrice = priceEt.getText().toString().trim();
        dPrice = dpriceEt.getText().toString().trim();
        dNote = dpercenEt.getText().toString().trim();
        productcate = categoryTv.getText().toString().trim();
        discountAvailable = switcSc.isChecked();

        if(TextUtils.isEmpty(productTitle)){
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }


        if(TextUtils.isEmpty(quantity)){
            Toast.makeText(this, "Quantity  is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(oPrice)){
            Toast.makeText(this, "Price is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(productcate)){
            Toast.makeText(this, "Category is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if(discountAvailable){
            dPrice=dpriceEt.getText().toString().trim();
            dNote=dpercenEt.getText().toString().trim();

            if(TextUtils.isEmpty(dPrice)){
                Toast.makeText(this, "Discount Price is required", Toast.LENGTH_SHORT).show();
                return;
            }

        }
        else {
            dPrice="0";
            dNote="";
        }
        updateProduct();
    }

    private void updateProduct() {

        progressDialog.setMessage("Updating product");
        progressDialog.show();

        if(image_uri == null){
            HashMap<String , Object> hashMap = new HashMap<>();

            hashMap.put("productTitle",""+productTitle);
            hashMap.put("productDescription",""+productDesc);
            hashMap.put("productCategory",""+productcate);
            hashMap.put("productQuantity",""+quantity);
            hashMap.put("productIcon","");
            hashMap.put("originalPrice",""+oPrice);
            hashMap.put("discountPrice",""+dPrice);
            hashMap.put("discountNote",""+dNote);
            hashMap.put("discountAvailable",""+discountAvailable);
            hashMap.put("uid",""+firebaseAuth.getUid());

            DatabaseReference reference = FirebaseDatabase.getInstance("https://groceryapp-bb972-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Products").child(productId).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, "Product Updated Successfully", Toast.LENGTH_SHORT).show();
//                            clearData();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });



        }
        else{

            String filePatnandName = "product_images/"+""+productId;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePatnandName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();


                            if(uriTask.isSuccessful()){

                                HashMap<String , Object> hashMap = new HashMap<>();
                                hashMap.put("productTitle",""+productTitle);
                                hashMap.put("productDescription",""+productDesc);
                                hashMap.put("productCategory",""+productcate);
                                hashMap.put("productQuantity",""+quantity);
                                hashMap.put("productIcon",""+downloadImageUri);
                                hashMap.put("originalPrice",""+oPrice);
                                hashMap.put("discountPrice",""+dPrice);
                                hashMap.put("discountNote",""+dNote);
                                hashMap.put("discountAvailable",""+discountAvailable);
                                hashMap.put("uid",""+firebaseAuth.getUid());

                                DatabaseReference reference = FirebaseDatabase.getInstance("https://groceryapp-bb972-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Products").child(productId).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProductActivity.this, "Product Updated Successfully", Toast.LENGTH_SHORT).show();
//                                                clearData();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });



        }
    }

    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String category = Constants.productCategories[which];
                        categoryTv.setText(category);
                    }
                }).show();
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

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStroagePermissions() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);

    }

    private boolean checkCameraPermissions() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){

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
                producticonIv.setImageURI(image_uri);
            }
            else  if(requestCode == IMAGE_PICK_CAMERA_CODE){
                producticonIv.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}

