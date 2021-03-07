package com.cbsephysicaleducationsolutions.cbsephysicaleducationsolution;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class EditActivity extends AppCompatActivity {

    ImageView img;
    EditText title,desc,link;
    ImageButton pdf;
    ProgressBar progressBar;
    Button submit;
    Uri imageUri,pdfUri;
    public static int position;
    public static String imgUrl = "";
    public static String pdfUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().hide();

        Intent i = getIntent();

        position = i.getIntExtra("pos",0);

        img = findViewById(R.id.upimg);
        title = findViewById(R.id.uptitle);
        desc = findViewById(R.id.updesc);
        link = findViewById(R.id.uplink);
        pdf = findViewById(R.id.uppdf);
        progressBar = findViewById(R.id.upbar);
        submit = findViewById(R.id.sub);

        progressBar.setVisibility(View.GONE);
        title.setText(MainActivity.item.get(position).getTitle());
        desc.setText(MainActivity.item.get(position).getDesc());
        link.setText(MainActivity.item.get(position).getLink());

        Picasso.with(this).load(MainActivity.item.get(position).getImage()).error(R.drawable.pelogo).fit().into(img, new Callback() {

            @Override
            public void onSuccess() {


                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onError() {

                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onError: download failed");

            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });


        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePdf();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submit.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                deleteActivity();
            }
        });
    }

    public void choosePicture()
    {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(i.ACTION_GET_CONTENT);
        startActivityForResult(i,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            img.setImageURI(imageUri);
        }

        if(requestCode==2 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            pdfUri = data.getData();
            pdf.setImageResource(R.drawable.pdfselected);
        }

    }


    public void choosePdf()
    {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,2);
    }

    public void deleteActivity(){

        if(imageUri != null){

            StorageReference mStorageRef;
            String storageurl = MainActivity.item.get(position).getImage();
            mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(storageurl);
            mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(pdfUri != null){

                        StorageReference mStorageRef;
                        String storageurl = MainActivity.item.get(position).getPdf();

                        if(storageurl.isEmpty()){
                            uploadImage();
                        }else {

                            mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(storageurl);
                            mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    uploadImage();
                                }
                            });
                        }
                    }else{
                        uploadImage();
                    }
                }
            });
        }else{
            if(pdfUri != null){

                StorageReference mStorageRef;
                String storageurl = MainActivity.item.get(position).getPdf();

                if(storageurl.isEmpty()){
                    uploadImage();
                }else {

                    mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(storageurl);
                    mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            uploadImage();
                        }
                    });
                }
            }else {
                uploadImage();
            }
        }
    }

    public void uploadImage(){

        if(imageUri != null){

            String randomkey = UUID.randomUUID().toString();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            final StorageReference ref = storageRef.child("images/" + randomkey);
            UploadTask uploadTask = ref.putFile(imageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                        imgUrl = task.getResult().toString();
                    uploadPdf();
                }
            });

        }else{
            imgUrl = MainActivity.item.get(position).getImage();
            uploadPdf();
        }

    }

    public void uploadPdf()
    {
        if(pdfUri != null){

            String randomkey = UUID.randomUUID().toString();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            final StorageReference ref = storageRef.child("PDFs/" + randomkey);
            UploadTask uploadTask = ref.putFile(pdfUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                        pdfUrl = task.getResult().toString();
                    uploadAllFiles();
                }
            });

        }else{
            pdfUrl = MainActivity.item.get(position).getPdf();
            uploadAllFiles();
        }
    }

    public void uploadAllFiles(){

        FirebaseFirestore.getInstance()
                .collection("notes")
                .whereEqualTo("created",MainActivity.item.get(position).getCreated())
                .whereEqualTo("grade",MainActivity.item.get(position).getGrade())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                        WriteBatch batch = FirebaseFirestore.getInstance().batch();

                        for(DocumentSnapshot snapshot : snapshotList){
                            batch.delete(snapshot.getReference());
                        }

                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Item item = new Item(title.getText().toString(),
                                        desc.getText().toString(),
                                        link.getText().toString(),
                                        imgUrl,pdfUrl,MainActivity.item.get(position).getGrade(),
                                        MainActivity.item.get(position).getCreated());

                                FirebaseFirestore.getInstance()
                                        .collection("notes")
                                        .add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(getApplicationContext(),"Document updated",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        submit.setVisibility(View.VISIBLE);

                                        Intent i = new Intent(EditActivity.this,MainActivity.class);
                                        i.putExtra("grade",MainActivity.item.get(position).getGrade());
                                        startActivity(i);
                                        finish();
                                    }
                                });

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(),"Document failed to updated",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);

            }
        });

    }

}