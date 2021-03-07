package com.cbsephysicaleducationsolutions.cbsephysicaleducationsolution;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

public class DetailsActivity extends AppCompatActivity {

    TextView title,desc,link,grade,created;
    ImageView imageView;
    ImageButton imageButton;
    ProgressBar progressBar;
    Button edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().hide();

        title = findViewById(R.id.title2);
        desc = findViewById(R.id.desc2);
        link = findViewById(R.id.link);
        grade = findViewById(R.id.grade);
        created = findViewById(R.id.created);
        imageView = findViewById(R.id.imageView);
        imageButton = findViewById(R.id.pdf);
        progressBar = findViewById(R.id.progress_load_photo1);
        edit = findViewById(R.id.edit);

        final Intent intent = getIntent();

        final int position = intent.getIntExtra("pos",0);

        progressBar.setVisibility(View.VISIBLE);

        title.setText(MainActivity.item.get(position).getTitle());
        desc.setText(MainActivity.item.get(position).getDesc());
        link.setText(MainActivity.item.get(position).getLink());
        grade.setText(MainActivity.item.get(position).getGrade() + "th Standard");

        CharSequence dateChar = DateFormat.format("EEEE, MMM d,yyyy h:mm a",MainActivity.item.get(position).getCreated().toDate());

        created.setText("Created - " + dateChar);
        Picasso.with(this).load(MainActivity.item.get(position).getImage()).error(R.drawable.pelogo).fit().into(imageView, new Callback() {

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

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(MainActivity.item.get(position).getPdf().isEmpty()){

                    Toast.makeText(getApplicationContext(),"No PDF for this topic",Toast.LENGTH_SHORT).show();

                }else{

                    Intent intent1 = new Intent(getApplicationContext(),PDFActivity.class);

                    intent1.putExtra("s",1);

                    intent1.putExtra("pos",position);

                    startActivity(intent1);


                }

            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(DetailsActivity.this,EditActivity.class);

                intent1.putExtra("pos",position);

                startActivity(intent1);

                finish();

            }
        });

    }
}