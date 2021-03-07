package com.cbsephysicaleducationsolutions.cbsephysicaleducationsolution;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.okhttp.internal.DiskLruCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floatingActionButton,syllab;
    public static ArrayList<Item> item;
    public int garde;
    TextView titlebar,error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        item = new ArrayList<>();


        titlebar = findViewById(R.id.titlebar);
        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setHasFixedSize(true);
        adapter = new Adapter(item,this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        syllab = findViewById(R.id.addSyllab);
        error = findViewById(R.id.errorView);

        error.setVisibility(View.GONE);

        Intent i = getIntent();

        garde = i.getIntExtra("grade",11);

        titlebar.setText(garde + "th Standard");



        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent = new Intent(MainActivity.this,DetailsActivity.class);

                intent.putExtra("pos",position);

                startActivity(intent);


            }
        });

        adapter.setOnItemLongClickListener(new Adapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final int position) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete " + item.get(position).getTitle() + " ?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                FirebaseFirestore.getInstance()
                                        .collection("notes")
                                        .whereEqualTo("created",item.get(position).getCreated())
                                        .whereEqualTo("grade",garde)
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

                                                        StorageReference mStorageRef;
                                                        String storageurl = item.get(position).getImage();
                                                        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(storageurl);
                                                        mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                StorageReference mStorageRef;
                                                                String storageurl = item.get(position).getPdf();
                                                                if(storageurl.isEmpty()){
                                                                    Toast.makeText(getApplicationContext(),"Document Deleted",Toast.LENGTH_LONG).show();
                                                                    onRefresh();
                                                                    return;
                                                                }
                                                                mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(storageurl);
                                                                mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(getApplicationContext(),"Document Deleted",Toast.LENGTH_LONG).show();
                                                                        onRefresh();
                                                                    }
                                                                });

                                                            }
                                                        });


                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                        Toast.makeText(getApplicationContext(),"Unable to delete!",Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(getApplicationContext(),"Unable to select documents",Toast.LENGTH_LONG).show();
                                    }
                                });


                            }
                        }).setNegativeButton("NO",null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });



        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,UploadActivity.class);
                i.putExtra("grade",garde);
                startActivity(i);
                finish();

            }
        });


        onLoadingRefresh();

    }



    public void retrieveData()
    {

        swipeRefreshLayout.setRefreshing(true);
        FirebaseFirestore.getInstance().collection("notes")
                .orderBy("created", Query.Direction.ASCENDING)
                .whereEqualTo("grade",garde)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                item.add(new Item(document.getString("title"),
                                        document.getString("desc"),
                                        document.getString("link"),
                                        document.getString("image"),
                                        document.getString("pdf"),
                                        Math.toIntExact(document.getLong("grade")),
                                        document.getTimestamp("created")));

                                adapter.notifyDataSetChanged();

                            }

                            if(item.size() == 0){

                                error.setVisibility(View.VISIBLE);

                            }

                            swipeRefreshLayout.setRefreshing(false);


                        } else {
                            Log.d("This is test", "Error getting documents: ", task.getException());

                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });

    }

    @Override
    public void onRefresh() {

        item.clear();
        retrieveData();

    }

    private void onLoadingRefresh() {

        swipeRefreshLayout.post(

                new Runnable() {
                    @Override
                    public void run() {
                        item.clear();
                        retrieveData();
                    }
                }
        );

    }
 }
