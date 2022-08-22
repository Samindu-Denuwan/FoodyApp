package com.jiat.foodyapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import com.jiat.foodyapp.adapter.CategoryAdapter;

import java.util.ArrayList;
import java.util.UUID;

public class AddNewCategoryActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private ArrayList<ModelCategories> categories_m;
    private CategoryAdapter categoryAdapter;
    private static final String TAG = "owner";
    ImageView imgBtnCategory;
    EditText categoryTitle, categoryDesc;
    Button btnSaveCategory;
    RecyclerView cate_recycler;



    private Uri image_uri;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_category);

        loadCategories();


        dialog = new ProgressDialog(AddNewCategoryActivity.this);

        firebaseAuth = FirebaseAuth.getInstance();

        dialog.setTitle("Please Wait");
        dialog.setCanceledOnTouchOutside(false);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        imgBtnCategory = findViewById(R.id.categoryImageBtn);
        categoryTitle = findViewById(R.id.categoryTitleET);
        categoryDesc = findViewById(R.id.categoryDescET);
        btnSaveCategory = findViewById(R.id.btnSaveCate);

        Glide.with(this)
                .load(R.drawable.plus)
                .circleCrop()
                .into(imgBtnCategory);


        btnSaveCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = categoryTitle.getText().toString();
                String detail = categoryDesc.getText().toString();
                final String imageId = UUID.randomUUID().toString();

                //data Validate

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(AddNewCategoryActivity.this, "Enter Category Title..", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(detail)) {
                    Toast.makeText(AddNewCategoryActivity.this, "Enter Category Description..", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (image_uri == null) {
                    Toast.makeText(AddNewCategoryActivity.this, "Please Select a Image", Toast.LENGTH_SHORT).show();
                    return;
                }


                ModelCategories modelCategories = new ModelCategories(name, detail, imageId);

                dialog.setMessage("Saving New Category..");
                dialog.setCancelable(false);
                dialog.show();

                firestore.collection("Categories").add(modelCategories).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        if (image_uri != null) {
                            dialog.setMessage("Uploading Image..");

                            StorageReference reference = storage.getReference("Categories").child(imageId);
                            reference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    dialog.dismiss();
                                    Toast.makeText(AddNewCategoryActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                                    clearData();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(AddNewCategoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddNewCategoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        imgBtnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher.launch(Intent.createChooser(intent, "Select Image"));
            }
        });


    }



    private void clearData() {

        categoryTitle.setText("");
        categoryDesc.setText("");
        image_uri = null;
        Glide.with(this)
                .load(R.drawable.plus)
                .circleCrop()
                .into(imgBtnCategory);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void loadCategories() {
        cate_recycler = findViewById(R.id.category_recyclerView);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        categories_m = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(AddNewCategoryActivity.this, categories_m, storage);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddNewCategoryActivity.this, RecyclerView.HORIZONTAL, false);
        cate_recycler.setLayoutManager(linearLayoutManager);
        cate_recycler.setAdapter(categoryAdapter);

        firestore.collection("Categories").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                categories_m.clear();
                for(QueryDocumentSnapshot snapshot : task.getResult()){
                    ModelCategories modelCategories = snapshot.toObject(ModelCategories.class);
                    categories_m.add(modelCategories);
                }
                categoryAdapter.notifyDataSetChanged();
            }
        });

        firestore.collection("Categories").orderBy("name")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        categories_m.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()){
                            ModelCategories modelCategories = snapshot.toObject(ModelCategories.class);
                            categories_m.add(modelCategories);
                        }
                        categoryAdapter.notifyDataSetChanged();
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
                        Glide.with(AddNewCategoryActivity.this.getApplicationContext()).load(image_uri).circleCrop().into(imgBtnCategory);
                        Log.i(TAG, image_uri.getPath());
                    }
                }
            }
    );



}