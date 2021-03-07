package com.cbsephysicaleducationsolutions.cbsephysicaleducationsolution;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PDFActivity extends AppCompatActivity {

    public static ProgressBar progressBar;
    private static PDFView pdf;
    String TAG = "This is to test";
    String pdfUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_f);
        getSupportActionBar().hide();

        pdf = findViewById(R.id.ViewPdf);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();

        int s = intent.getIntExtra("s",0);

        if (s == 1) {

            int position = intent.getIntExtra("pos",0);

            pdfUrl = MainActivity.item.get(position).getPdf();

        }else{

            pdfUrl = intent.getStringExtra("url");

        }




        try{
            new RetrievePdfStream().execute(pdfUrl);

        }
        catch (Exception e){
            Toast.makeText(this, "Failed to load Url :" + e.toString(), Toast.LENGTH_SHORT).show();

        }

    }

    static class RetrievePdfStream extends AsyncTask<String, Void, InputStream> {


        @Override
        protected InputStream doInBackground(String... strings) {

            InputStream inputStream = null;


            try {
                URL url = new URL(strings[0]);


                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


                if (urlConnection.getResponseCode() == 200) {


                    inputStream = new BufferedInputStream(urlConnection.getInputStream());


                }

            } catch (IOException e) {
                return null;

            }


            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdf.fromStream(inputStream).onLoad(new OnLoadCompleteListener() {
                @Override
                public void loadComplete(int nbPages) {
                    progressBar.setVisibility(View.GONE);
                }
            }).load();


        }
    }
}