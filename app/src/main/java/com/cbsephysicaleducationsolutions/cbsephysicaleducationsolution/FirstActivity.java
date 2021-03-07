package com.cbsephysicaleducationsolutions.cbsephysicaleducationsolution;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class FirstActivity extends AppCompatActivity {

    ImageView imageView1,logout;

    FloatingActionButton syllab;

    Button b1, b2,s;
    Uri pdfUri = null;
    String imgUrl,url;
    AlertDialog.Builder builder;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        getSupportActionBar().hide();

        imageView1 = findViewById(R.id.imageView2);
        b1 = findViewById(R.id.button);
        b2 = findViewById(R.id.button2);
        s = findViewById(R.id.syllabus);
        syllab = findViewById(R.id.addSyllab);
        logout = findViewById(R.id.imageView6);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){

            startLogin();

            return;

        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("grade", 11);
                startActivity(i);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("grade", 12);
                startActivity(intent);
            }
        });



        syllab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });


        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore.getInstance()
                        .collection("syllabus")
                        .document("syllab")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                url = documentSnapshot.getString("sUrl");
                                Intent intent = new Intent(FirstActivity.this,PDFActivity.class);
                                intent.putExtra("url",url);
                                startActivity(intent);
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Unknown error occured",Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });

    }

    private void startLogin() {

        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();


    }

    public void showAlertDialog()
    {
        imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.uploadpdf);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 2);
            }
        });

        builder = new AlertDialog.Builder(FirstActivity.this);
        builder.setTitle("Add Syllabus");
        builder.setView(imageView);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {


                        if(pdfUri != null){
                            FirebaseFirestore.getInstance()
                                    .collection("syllabus")
                                    .document("syllab")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(final DocumentSnapshot documentSnapshot) {

                                            imgUrl = documentSnapshot.getString("sUrl");

                                            StorageReference mStorageRef;
                                            mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl);
                                            mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    String randomkey = UUID.randomUUID().toString();
                                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                                    StorageReference storageRef = storage.getReference();
                                                    final StorageReference ref = storageRef.child("Syllabus/" + randomkey);
                                                    UploadTask uploadTask = ref.putFile(pdfUri);

                                                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                        @Override
                                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                            if (!task.isSuccessful()) {
                                                                throw task.getException();
                                                            }

                                                            Log.d("This is test", "then: " + ref.getDownloadUrl());
                                                            // Continue with the task to get the download URL
                                                            return ref.getDownloadUrl();
                                                        }
                                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Uri> task) {

                                                            if (task.isSuccessful()) {
                                                                imgUrl = task.getResult().toString();

                                                                HashMap<String, String> hashMap = new HashMap<>();
                                                                hashMap.put("sUrl", imgUrl);

                                                                documentSnapshot.getReference().set(hashMap)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Toast.makeText(getApplicationContext(), "Syllabus Updated", Toast.LENGTH_LONG).show();
                                                                                dialog.dismiss();
                                                                            }
                                                                        });

                                                            }

                                                        }
                                                    });


                                                }
                                            });


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Unable to connect to Database", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });

                        }else{
                            Toast.makeText(getApplicationContext(),"Select PDF",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }



                    }
                });
               builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                   }
               });
               builder.show();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pdfUri = data.getData();
            imageView.setImageResource(R.drawable.pdfselected);
        }
    }


}