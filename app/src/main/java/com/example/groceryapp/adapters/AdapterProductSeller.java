package com.example.groceryapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groceryapp.FilterProduct;
import com.example.groceryapp.R;
import com.example.groceryapp.activities.EditProductActivity;
import com.example.groceryapp.models.ModelProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList,filterList;
    private FilterProduct filter;

    public AdapterProductSeller(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate Layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_seller,parent,false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeller holder, int position) {

        //getdata
        final ModelProduct modelProduct = productList.get(position);
        String id = modelProduct.getProductId();
        String uid= modelProduct.getUid();
        String discountAvailable= modelProduct.getDiscountAvailable();
        String discountNote= modelProduct.getDiscountNote();
        String discountPrice= modelProduct.getDiscountPrice();
        String productCategory= modelProduct.getProductCategory();
        String productDescription= modelProduct.getProductDescription();
        String icon= modelProduct.getProductIcon();
        String quantity= modelProduct.getProductQuantity();
        String title= modelProduct.getProductTitle();
        String Oprice= modelProduct.getOriginalPrice();
        String timestamp = modelProduct.getTimestamp();

        holder.titleTv.setText(title);
        holder.quantityTv.setText(quantity);
        holder.discountedNoteTv.setText(discountNote);
        holder.discountedPriceTv.setText("Rs." + discountPrice);
        holder.OriginalPriceTv.setText("Rs."+ Oprice);

        if(discountAvailable.equals("true")){
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.discountedNoteTv.setVisibility(View.VISIBLE);
            holder.OriginalPriceTv.setPaintFlags(holder.OriginalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountedNoteTv.setVisibility(View.GONE);
            holder.OriginalPriceTv.setPaintFlags(0);
        }
        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_baseline_shopping_cart_24).into(holder.producticonIv);
        }catch(Exception e){
            holder.producticonIv.setImageResource(R.drawable.ic_baseline_shopping_cart_24);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsBottomSheet(modelProduct);
            }
        });


    }

    private void detailsBottomSheet(ModelProduct modelProduct) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bs_product_details_seller,null);
        bottomSheetDialog.setContentView(view);



        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageButton deleteBtn = view.findViewById(R.id.deleteBtn);
        ImageButton editBtn = view.findViewById(R.id.editBtn);
        ImageView producticonIv = view.findViewById(R.id.producticonIv);
        TextView discounteNoteTv = view.findViewById(R.id.discounteNoteTv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView categoryTv = view.findViewById(R.id.categoryTv);
        TextView quantityTv = view.findViewById(R.id.quantityTv);
        TextView discountedPriceTv = view.findViewById(R.id.discountedPriceTv);
        TextView OriginalPriceTv = view.findViewById(R.id.OriginalPriceTv);

//        get data
        String id = modelProduct.getProductId();
        String uid= modelProduct.getUid();
        String discountAvailable= modelProduct.getDiscountAvailable();
        String discountNote= modelProduct.getDiscountNote();
        String discountPrice= modelProduct.getDiscountPrice();
        String productCategory= modelProduct.getProductCategory();
        String productDescription= modelProduct.getProductDescription();
        String icon= modelProduct.getProductIcon();
        String quantity= modelProduct.getProductQuantity();
        String title= modelProduct.getProductTitle();
        String Oprice= modelProduct.getOriginalPrice();
        String timestamp = modelProduct.getTimestamp();

//        setData

        titleTv.setText(title);
        descriptionTv.setText(productDescription);
        categoryTv.setText(productCategory);
        quantityTv.setText(quantity);
        discountedPriceTv.setText(discountPrice);
        discounteNoteTv.setText("Rs."+discountNote);
        OriginalPriceTv.setText("Rs."+Oprice);

        if(discountAvailable.equals("true")){
          discountedPriceTv.setVisibility(View.VISIBLE);
          discounteNoteTv.setVisibility(View.VISIBLE);
          OriginalPriceTv.setPaintFlags(OriginalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            discountedPriceTv.setVisibility(View.GONE);
            discounteNoteTv.setVisibility(View.GONE);


        }

        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_baseline_shopping_cart_24).into(producticonIv);
        }catch(Exception e){
            producticonIv.setImageResource(R.drawable.ic_baseline_shopping_cart_24);
        }

        bottomSheetDialog.show();

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("productId",id);
                context.startActivity(intent);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you want delete Product"+title+"?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteProduct(id);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

    }

    private void deleteProduct(String id) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
         DatabaseReference reference = FirebaseDatabase.getInstance("https://groceryapp-bb972-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
         reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void unused) {
                         Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show();
                     }
                 }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(context,""+e.getMessage(), Toast.LENGTH_SHORT).show();

             }
         });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {

        if(filter == null){
            filter =new FilterProduct(this,filterList);
        }
        return filter;
    }

    class  HolderProductSeller extends RecyclerView.ViewHolder{

        private ImageView producticonIv;
        private TextView discountedNoteTv,titleTv,quantityTv,discountedPriceTv,OriginalPriceTv;

        public HolderProductSeller(@NonNull View itemView) {
            super(itemView);

            producticonIv = itemView.findViewById(R.id.producticonIv);
            discountedNoteTv = itemView.findViewById(R.id.discountedNoteTv);
            titleTv = itemView.findViewById(R.id.titleTv);
            quantityTv = itemView.findViewById(R.id.quantityTv);
            discountedPriceTv = itemView.findViewById(R.id.discountedPriceTv);
            OriginalPriceTv = itemView.findViewById(R.id.OriginalPriceTv);

        }
    }
}
