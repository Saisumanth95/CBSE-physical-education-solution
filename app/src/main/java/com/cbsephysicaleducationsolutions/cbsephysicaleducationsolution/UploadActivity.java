package com.cbsephysicaleducationsolutions.cbsephysicaleducationsolution;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    public ImageView imageView;
    public Uri imageUri;
    public EditText title,desc,link;
    public TextView pdftext;
    public ImageButton pdf;
    public Uri pdfUri;
    public Button submit;
    public int grade;
    public static String prl = "";
    public static String irl = "";
    public ProgressBar progressBar;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAArXg395A:APA91bFawGLkGBo53bmvSJD2a5fVCnRU9smznREI-vAh2aRbNPIWRqhVW2zyOhgXf8Ysfs4HfKmaM3yMx3XjnsJ7ABjZ6RDK-_ci_DmXTbMyOefmml-jE7QaIW5W7Oy1VsPeoxBmrQWV";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        getSupportActionBar().hide();

        pdftext = findViewById(R.id.pdftext);
        imageView = findViewById(R.id.uploadimg);
        title = findViewById(R.id.uploadtitle);
        desc = findViewById(R.id.uploaddesc);
        link = findViewById(R.id.uploadlink);
        pdf = findViewById(R.id.uploadpdf);
        submit = findViewById(R.id.submit);
        progressBar = findViewById(R.id.uploadbar);
        progressBar.setVisibility(View.GONE);

        Intent i = getIntent();

        grade = i.getIntExtra("grade",11);


        imageView.setOnClickListener(new View.OnClickListener() {
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

                if(title.getText().toString().isEmpty() || desc.getText().toString().isEmpty()){

                    Toast.makeText(getApplicationContext(),"Enter Title and Description",Toast.LENGTH_LONG).show();

                }else{

                    if(imageUri == null){
                        Toast.makeText(getApplicationContext(),"Upload image",Toast.LENGTH_LONG).show();
                    }else{

                        progressBar.setVisibility(View.VISIBLE);

                        submit.setVisibility(View.GONE);

                        uploadImage();
                    }

                }

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
            imageView.setImageURI(imageUri);
        }

        if(requestCode==2 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            pdfUri = data.getData();
            pdftext.setText("PDF SELECTED");
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

    public void uploadImage()
    {
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

                Log.d("This is test", "then: " + ref.getDownloadUrl());
                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    irl = task.getResult().toString();

                    if(pdfUri == null){
                        Toast.makeText(getApplicationContext(),"PDF not uploaded",Toast.LENGTH_SHORT).show();
                        prl = "";
                        uploadAllFiles();
                        return;
                    }

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
                            if (task.isSuccessful()) {
                                prl = task.getResult().toString();
                                uploadAllFiles();
                            } else {
                                Toast.makeText(getApplicationContext(),"PDF not uploaded",Toast.LENGTH_SHORT).show();
                                prl = "";
                                uploadAllFiles();
                            }
                        }
                    });



                }else{
                    Toast.makeText(getApplicationContext(),"Unable to upload image",Toast.LENGTH_LONG).show();
                }
            }

        });

    }


    public void uploadAllFiles()
    {


        Item item = new Item(title.getText().toString(),
                desc.getText().toString(),
                link.getText().toString(),
                irl,prl,grade, Timestamp.now());
        FirebaseFirestore.getInstance().collection("notes").add(item)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        sendNotify();

                        Toast.makeText(getApplicationContext()," Data uploaded successfully!",Toast.LENGTH_LONG).show();

                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
                        i.putExtra("grade",grade);
                        startActivity(i);
                        finish();

                        progressBar.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext()," Data not uploaded",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                    }
                });


    }


    public void sendNotify()
    {

        TOPIC = "/topics/Student"; //topic must match with what the receiver subscribed to
        NOTIFICATION_TITLE = "CBSE Physical Education";
        NOTIFICATION_MESSAGE = "Added \"" + title.getText().toString() + "\" to class" + grade;

        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", NOTIFICATION_TITLE);
            notificationBody.put("message", NOTIFICATION_MESSAGE);

            notification.put("to", TOPIC);
            notification.put("data", notificationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        sendNotification(notification);


    }

    private void sendNotification(JSONObject notification) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        Toast.makeText(getApplicationContext(),"Message Sent",Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UploadActivity.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);


    }





}







