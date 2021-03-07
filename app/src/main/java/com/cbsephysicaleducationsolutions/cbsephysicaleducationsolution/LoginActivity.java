package com.cbsephysicaleducationsolutions.cbsephysicaleducationsolution;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();

        button = findViewById(R.id.loginbutton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());

                Intent intent =AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.pelogo)
                        .setAlwaysShowSignInMethodScreen(true)
                        .build();

                startActivityForResult(intent,1);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK){

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()){
                    Toast.makeText(getApplicationContext(),"WELCOME!",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"WELCOME BACK!",Toast.LENGTH_LONG).show();
                }

                Intent i = new Intent(getApplicationContext(),FirstActivity.class);
                startActivity(i);
                finish();

            }
        }

    }
}