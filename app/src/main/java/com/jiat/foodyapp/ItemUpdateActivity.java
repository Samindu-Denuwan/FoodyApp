package com.jiat.foodyapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

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
import android.widget.ImageButton;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jiat.foodyapp.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class ItemUpdateActivity extends AppCompatActivity {


    private static final String TAG = "owner";
    private String productId;
    private ProgressDialog dialog;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private Uri image_uri;
    private ArrayList<String > categories_array;
    private ArrayAdapter<String> adapter;
    private QuerySnapshot categorySnapshot;
    public  String selectedCategory;
    private String itemStatus;

    private ImageView btnItemImg;
    private EditText EtItemName, EtItemDescrip, EtItemPrice, EtItemDiscount, EtItemDisNote;
    private Spinner spinnerCategory;
    private SwitchCompat switchDis, switchStatus;
    private Button btnUpdateItem;
    private String itemImg;
    private String cateSeleted;

    private TextView CATEGORY;
    private Item item;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_update);

        //getId from intent
        productId = getIntent().getStringExtra("productId");

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
        btnUpdateItem = findViewById(R.id.btnItemUpdate);
        spinnerCategory = findViewById(R.id.categorySpinner);
        switchDis = findViewById(R.id.discountSwitch);
        switchStatus = findViewById(R.id.switchStatusItem);
        CATEGORY = findViewById(R.id.textViewCate);
        CATEGORY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CATEGORY.setText(null);
                spinnerCategory.setVisibility(View.VISIBLE);

            }
        });


        EtItemDiscount.setVisibility(View.GONE);
        EtItemDisNote.setVisibility(View.GONE);

        spinnerCategory.setVisibility(View.GONE);
        //set details
        loadProductDetails();
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
                CATEGORY.setText(selectedCategory);


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

        switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //checked, Active
                    itemStatus = "active";
                  //  Toast.makeText(ItemUpdateActivity.this, "Product Activated...", Toast.LENGTH_SHORT).show();

                }else{
                    //unchecked, inactive
                    itemStatus = "inactive";
                   // Toast.makeText(ItemUpdateActivity.this, "Product Inactivated...", Toast.LENGTH_SHORT).show();
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

        btnUpdateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  1) input data
                2) validate data
                3) add to db   */
                spinnerCategory.setVisibility(View.GONE);
                Log.i( TAG,"Save Button click");
                inputData();


            }
        });

    }


    private void loadProductDetails() {

        firestore.collection("Products").document(productId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){

                            String productId = documentSnapshot.getString("productId");
                            String itemName = documentSnapshot.getString("itemName");
                            String details = documentSnapshot.getString("details");
                            String price = documentSnapshot.getString("price");
                            String checkedCategory = documentSnapshot.getString("checkedCategory");
                            String image = documentSnapshot.getString("image");
                            String discount = documentSnapshot.getString("discount");
                            String discountNote = documentSnapshot.getString("discountNote");
                            String status = documentSnapshot.getString("status");
                            String discountAvailable = documentSnapshot.getString("discountAvailable");






                            EtItemName.setText(itemName);
                            EtItemDescrip.setText(details);
                            EtItemPrice.setText(price);
                            //cateSeleted = documentSnapshot.getString("checkedCategory");
                            CATEGORY.setText(checkedCategory);
                            //itemImg = documentSnapshot.getString("image");

                            storage.getReference("Product_Images/"+image)
                                    .getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Glide.with(ItemUpdateActivity.this).load(uri).circleCrop()
                                                    .into(btnItemImg);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ItemUpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Glide.with(ItemUpdateActivity.this).load(R.drawable.plus).circleCrop()
                                                    .into(btnItemImg);
                                        }
                                    });




                            if(discountAvailable.equals("true")){
                                switchDis.setChecked(true);
                                /*EtItemDiscount.setVisibility(View.VISIBLE);
                                EtItemDisNote.setVisibility(View.VISIBLE);*/
                                EtItemDiscount.setText(discount);
                                EtItemDisNote.setText(discountNote);
                            }else{
                                switchDis.setChecked(false);
                                EtItemDiscount.setVisibility(View.GONE);
                                EtItemDisNote.setVisibility(View.GONE);
                                EtItemDiscount.setText("");
                                EtItemDisNote.setText("");

                            }

                            if(status.equals("active")){
                                switchStatus.setChecked(true);
                                itemStatus = "active";
                            }else {
                                switchStatus.setChecked(false);
                                itemStatus = "inactive";
                            }


                        }else{
                            Toast.makeText(ItemUpdateActivity.this, "Document Does not exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ItemUpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });




    }


    private boolean discountAvailable = false;
    private String discountPrice,discountNote ;

    private void inputData() {

        //input data

        String  productTitle = EtItemName.getText().toString().trim();
        String productDescription = EtItemDescrip.getText().toString().trim();
        //String productCategory = selectedCategory;
        String originalPrice = EtItemPrice.getText().toString().trim();
        discountAvailable = switchDis.isChecked();//true / false
        String AvailableDiscount = String.valueOf(discountAvailable);
        final String imageId = UUID.randomUUID().toString();
        String categoryChosen = CATEGORY.getText().toString().trim();


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
        if(TextUtils.isEmpty(categoryChosen)){
            Toast.makeText(this, "Please Select a Category...", Toast.LENGTH_SHORT).show();
            return;
        }
       /* if (image_uri == null) {
            Toast.makeText(this, "Please Select a Image", Toast.LENGTH_SHORT).show();
            return;
        }*/
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


        //save to db

        //Item item = new Item(productTitle, productDescription,categoryChosen, originalPrice, AvailableDiscount, discountPrice, discountNote, imageId, itemStatus);




        dialog.setMessage("Updating Item..");
        dialog.setCancelable(false);
        dialog.show();

        if(image_uri != null) {
            HashMap updateItems = new HashMap<>();
            updateItems.put("itemName", productTitle);
            updateItems.put("details", productDescription);
            updateItems.put("checkedCategory", categoryChosen);
            updateItems.put("price", originalPrice);
            updateItems.put("discountAvailable", AvailableDiscount);
            updateItems.put("discount", discountPrice);
            updateItems.put("discountNote", discountNote);
            updateItems.put("image", imageId);
            updateItems.put("status", itemStatus);

            firestore.collection("Products").document(productId).update(updateItems).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {


                    dialog.setMessage("Uploading Image..");

                    StorageReference reference = storage.getReference("Product_Images").child(imageId);
                    reference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(ItemUpdateActivity.this, "Item Updated...", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "Updated ");

                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(ItemUpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            dialog.setMessage("Uploading " + (int) progress + "%");
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(ItemUpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }else{
            HashMap updateItems = new HashMap<>();
            updateItems.put("itemName", productTitle);
            updateItems.put("details", productDescription);
            updateItems.put("checkedCategory", categoryChosen);
            updateItems.put("price", originalPrice);
            updateItems.put("discountAvailable", AvailableDiscount);
            updateItems.put("discount", discountPrice);
            updateItems.put("discountNote", discountNote);
            /*updateItems.put("image", imageId);*/
            updateItems.put("status", itemStatus);

            firestore.collection("Products").document(productId).update(updateItems).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {

                    dialog.dismiss();
                    Toast.makeText(ItemUpdateActivity.this, "Item Updated...", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Updated ");


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(ItemUpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

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
                        Toast.makeText(ItemUpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Glide.with(ItemUpdateActivity.this.getApplicationContext()).load(image_uri).circleCrop().into(btnItemImg);
                        Log.i(TAG, image_uri.getPath());
                    }
                }
            }
    );



}