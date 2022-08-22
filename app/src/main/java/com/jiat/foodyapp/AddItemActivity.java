package com.jiat.foodyapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jiat.foodyapp.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.UUID;

public class AddItemActivity extends AppCompatActivity {

    private static final String TAG = "owner";
    private ImageView btnItemImg;
    private EditText EtItemName, EtItemDescrip, EtItemPrice, EtItemDiscount, EtItemDisNote;
    private Spinner spinnerCategory;
    private SwitchCompat switchDis;
    private Button btnSaveItem;
    private ProgressDialog dialog;

    private ArrayList<String > categories_array;
    private ArrayAdapter<String> adapter;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private QuerySnapshot categorySnapshot;
    private  String selectedCategory;





    private Uri image_uri;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        dialog = new ProgressDialog(this);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog.setTitle("Please Wait");
        dialog.setCanceledOnTouchOutside(false);

        btnItemImg = findViewById(R.id.itemImageBtn);
        EtItemName = findViewById(R.id.itemNameET);
        EtItemDescrip = findViewById(R.id.itemDescET);
        EtItemPrice = findViewById(R.id.itemPriceET);
        EtItemDiscount = findViewById(R.id.itemDiscountPriceET);
        EtItemDisNote = findViewById(R.id.itemDiscountNoteET);
        btnSaveItem = findViewById(R.id.btnItemSave);
        spinnerCategory = findViewById(R.id.categorySpinner);
        switchDis = findViewById(R.id.discountSwitch);

        //unchecked, hide
        EtItemDiscount.setVisibility(View.GONE);
        EtItemDisNote.setVisibility(View.GONE);

        Glide.with(this)
                .load(R.drawable.plus)
                .circleCrop()
                .into(btnItemImg);

       getCategories();
        //category dropdown
        categories_array = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, categories_array);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = adapter.getItem(i);

                Log.i( TAG,"ID"+categorySnapshot.getDocuments().get(i).getId());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        switchDis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //checked ,show
                    EtItemDiscount.setVisibility(View.VISIBLE);
                    EtItemDisNote.setVisibility(View.VISIBLE);
                }else{
                    //unchecked, hide
                    EtItemDiscount.setVisibility(View.GONE);
                    EtItemDisNote.setVisibility(View.GONE);
                }


            }
        });

        btnItemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher.launch(Intent.createChooser(intent, "Select Image"));
            }
        });

        btnSaveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  1) input data
                2) validate data
                3) add to db   */
                Log.i( TAG,"Save Button click");
                inputData();

            }
        });




    }



    private boolean discountAvailable = false;
    private String discountPrice,discountNote ;

    private void inputData() {
        //input data

        String  productTitle = EtItemName.getText().toString().trim();
        String productDescription = EtItemDescrip.getText().toString().trim();
        String productCategory = selectedCategory;
        String originalPrice = EtItemPrice.getText().toString().trim();
        discountAvailable = switchDis.isChecked();//true / false
        String AvailableDiscount = String.valueOf(discountAvailable);
        final String imageId = UUID.randomUUID().toString();
        final String status = "active";

        //validate

        if(TextUtils.isEmpty(productTitle)){
            Toast.makeText(this, "Item Name Required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(productDescription)){
            Toast.makeText(this, "Item Description Required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(originalPrice)){
            Toast.makeText(this, "Item Price Required...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (image_uri == null) {
            Toast.makeText(this, "Please Select a Image", Toast.LENGTH_SHORT).show();
            return;
        }
        if(discountAvailable){
             discountPrice = EtItemDiscount.getText().toString().trim();
             discountNote = EtItemDisNote.getText().toString().trim();

            if(TextUtils.isEmpty(discountPrice)){
                Toast.makeText(this, "Discount Price Required...", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(discountNote)){
                Toast.makeText(this, "Discount Note Required...", Toast.LENGTH_SHORT).show();
                return;
            }


        }else{
             discountPrice = "0";
             discountNote = "";
        }

        String timestamp = ""+System.currentTimeMillis();
        String productId = timestamp;
        //save to db

        Item item = new Item(productId, productTitle, productDescription,productCategory, originalPrice, AvailableDiscount, discountPrice, discountNote, imageId, status);
        dialog.setMessage("Saving New Item..");
        dialog.setCancelable(false);
        dialog.show();

        /*firestore.collection("Products").add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                if(image_uri != null){
                    dialog.setMessage("Uploading Image..");

                    StorageReference reference= storage.getReference("Product_Images").child(imageId);
                    reference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(AddItemActivity.this, "Item Saved...", Toast.LENGTH_SHORT).show();
                            clearData();
                            Log.i( TAG,"Saved item ");
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(AddItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            dialog.setMessage("Uploading " + (int) progress + "%");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(AddItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/

        firestore.collection("Products").document(productId).set(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(image_uri != null){
                    dialog.setMessage("Uploading Image..");

                    StorageReference reference= storage.getReference("Product_Images").child(imageId);
                    reference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(AddItemActivity.this, "Item Saved...", Toast.LENGTH_SHORT).show();
                            clearData();
                            Log.i( TAG,"Saved item ");
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(AddItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            dialog.setMessage("Uploading " + (int) progress + "%");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(AddItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





    }

    private void clearData() {
        EtItemName.setText("");
        EtItemDescrip.setText("");
        EtItemPrice.setText("");
        EtItemDiscount.setText("");
        EtItemDisNote.setText("");
        switchDis.setChecked(false);
        spinnerCategory.setAdapter(adapter);
        image_uri = null;
        Glide.with(this)
                .load(R.drawable.plus)
                .circleCrop()
                .into(btnItemImg);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getCategories(){
        firestore.collection("Categories").orderBy("name").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        categorySnapshot = queryDocumentSnapshots;
                        categories_array.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots){
                            categories_array.add(document.getString("name"));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        image_uri = result.getData().getData();
                        Glide.with(AddItemActivity.this.getApplicationContext()).load(image_uri).circleCrop().into(btnItemImg);
                        Log.i(TAG, image_uri.getPath());
                    }
                }
            }
    );



}