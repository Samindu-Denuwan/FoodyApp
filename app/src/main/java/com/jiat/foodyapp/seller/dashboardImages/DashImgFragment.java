package com.jiat.foodyapp.seller.dashboardImages;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.RegisterUserActivity;
import com.jiat.foodyapp.UserNaviActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class DashImgFragment extends Fragment {

    public DashImgFragment() {
        // Required empty public constructor
    }


    private ImageSlider imageSlider;
    private Button BtnSave;
    private ImageButton img1, img2,img3,img4;
    private Uri image_uri1, image_uri2, image_uri3, image_uri4;
    private ProgressDialog progressDialog;
    private FirebaseAuth  firebaseAuth;
    private Uri uri1, uri2, uri3, uri4;

    private FirebaseFirestore firestore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dash_img, container, false);
        imageSlider = view.findViewById(R.id.image_slider);
        BtnSave= view.findViewById(R.id.btnSave);
        img1 = view.findViewById(R.id.imgBtn1);
        img2 = view.findViewById(R.id.imgBtn2);
        img3 = view.findViewById(R.id.imgBtn3);
        img4 = view.findViewById(R.id.imgBtn4);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore= FirebaseFirestore.getInstance();


        ArrayList<SlideModel> slideModels = new ArrayList<>();

        /*slideModels.add(new SlideModel("https://icms-image.slatic.net/images/ims-web/5264c754-632f-47c8-8fdd-59eec28d61ad.jpg_1200x1200.jpg", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/foody-1fe4d.appspot.com/o/profile_images%2FQzQCdKWienZ4PeDejxKQmO1RWVk2?alt=media&token=caafad5f-8bc6-4159-a88d-7a7d7d1ef28e", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://i.im.ge/2022/08/29/OPNJHc.4426510.png", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://img.freepik.com/free-photo/butterfly-wild_53876-90200.jpg?w=1380&t=st=1660152951~exp=1660153551~hmac=cf3600731da410ea4e9332b6717bf7dd1cb0f002a0c36eca02a91e5a15c160f9", ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);*/

        firestore.collection("Slider_Images").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        slideModels.add(new SlideModel(documentSnapshot.getString("url"), ScaleTypes.FIT));
                        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                    }
                }else{
                    Toast.makeText(getActivity(), "Can't Load Images...", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        BtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(image_uri1 != null && image_uri2 != null && image_uri3 != null && image_uri4 != null){
                    saveDb();
                }else{
                    Toast.makeText(getActivity(), "Please Select Images..", Toast.LENGTH_SHORT).show();
                }

            }
        });

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //image choose
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher.launch(Intent.createChooser(intent, "Select Image1"));
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //image choose
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher2.launch(Intent.createChooser(intent, "Select Image2"));
            }
        });


        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //image choose
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher3.launch(Intent.createChooser(intent, "Select Image3"));
            }
        });

        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //image choose
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher4.launch(Intent.createChooser(intent, "Select Image4"));
            }
        });



        return view;
    }


    private void saveDb() {
        progressDialog.setMessage("Saving Images...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String imageId = UUID.randomUUID().toString();
        String filePathAndName = "slider_images/" + ""+imageId;
        //upload img
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(image_uri1)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        uri1 = uriTask.getResult();

                        if(uriTask.isSuccessful()){

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("name", "" + "image_1");
                            hashMap.put("url", "" + uri1);

                            firestore.collection("Slider_Images").document("slide_1").update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploading " + (int) progress + "%");
                    }
                });

        final String imageId2 = UUID.randomUUID().toString();
        String filePathAndName2 = "slider_images/" + ""+imageId2;
        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference(filePathAndName2);
        storageReference1.putFile(image_uri2)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        uri2 = uriTask.getResult();
                        if(uriTask.isSuccessful()){

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("name", "" + "image_2");
                            hashMap.put("url", "" + uri2);

                            firestore.collection("Slider_Images").document("slide_2").update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploading " + (int) progress + "%");
                    }
                });


        final String imageId3 = UUID.randomUUID().toString();
        String filePathAndName3 = "slider_images/" + ""+imageId3;
        StorageReference storageReference3 = FirebaseStorage.getInstance().getReference(filePathAndName3);
        storageReference3.putFile(image_uri3)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        uri3 = uriTask.getResult();
                        if(uriTask.isSuccessful()){

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("name", "" + "image_3");
                            hashMap.put("url", "" + uri3);

                            firestore.collection("Slider_Images").document("slide_3").update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploading " + (int) progress + "%");
                    }
                });

        final String imageId4 = UUID.randomUUID().toString();
        String filePathAndName4 = "slider_images/" + ""+imageId4;
        StorageReference storageReference4 = FirebaseStorage.getInstance().getReference(filePathAndName4);
        storageReference4.putFile(image_uri4)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        uri4 = uriTask.getResult();
                        if(uriTask.isSuccessful()){
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("name", "" + "image_4");
                            hashMap.put("url", "" + uri4);

                            firestore.collection("Slider_Images").document("slide_4").update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploading " + (int) progress + "%");
                    }
                });



    }



    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        image_uri1 = result.getData().getData();
                        Glide.with(getActivity()).load(image_uri1).into(img1);
                        Toast.makeText(getActivity(), "Image No1 Added...", Toast.LENGTH_SHORT).show();

                    }
                }
            }
    );

    ActivityResultLauncher<Intent> activityResultLauncher2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        image_uri2 = result.getData().getData();
                        Glide.with(getActivity()).load(image_uri2).into(img2);
                        Toast.makeText(getActivity(), "Image No2 Added...", Toast.LENGTH_SHORT).show();

                    }
                }
            }
    );

    ActivityResultLauncher<Intent> activityResultLauncher3 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        image_uri3 = result.getData().getData();
                        Glide.with(getActivity()).load(image_uri3).into(img3);
                        Toast.makeText(getActivity(), "Image No3 Added...", Toast.LENGTH_SHORT).show();

                    }
                }
            }
    );

    ActivityResultLauncher<Intent> activityResultLauncher4 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        image_uri4 = result.getData().getData();
                        Glide.with(getActivity()).load(image_uri4).into(img4);
                        Toast.makeText(getActivity(), "Image No4 Added...", Toast.LENGTH_SHORT).show();

                    }
                }
            }
    );
}