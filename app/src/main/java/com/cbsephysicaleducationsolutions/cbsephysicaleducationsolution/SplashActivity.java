package com.cbsephysicaleducationsolutions.cbsephysicaleducationsolution;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        logo = findViewById(R.id.imageView3);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(SplashActivity.this,
                        FirstActivity.class);
                startActivity(i);
                finish();
            }
        }, 4000);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("This is Test", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token

                        // Log and toast
                        String msg = Objects.requireNonNull(task.getResult()).getToken();
                        Log.d("This is test", msg);

                    }
                });

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseMessaging.getInstance().subscribeToTopic("Lecturer");

        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.splashanimation);
        logo.startAnimation(myanim);
    }
}