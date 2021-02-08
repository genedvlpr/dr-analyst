package com.genedev.retinalclassifierfull.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.genedev.retinalclassifierfull.classes.HistoryHelper;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class Splashscreen extends Activity {
    //variable declaration
    private FirebaseAuth mAuth;

    private Button next;
    private static int TIME_OUT = 1000;

    private LottieAnimationView lottieAnimationView;

    private Calendar calendar;
    private Date currentTime;
    private int year,month,day;
    private String year_date,month_date,day_date,time_date;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");

        TextView tv1 = findViewById(R.id.headerTitle);
        TextView tv2 = findViewById(R.id.subtitle);
        Button nextBtn =  findViewById(R.id.nextBtn);

        Typeface productFontBold = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Bold.ttf");
        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        tv1.setTypeface(productFontBold);
        tv2.setTypeface(productFontRegular);
        nextBtn.setTypeface(productFontRegular);

        lottieAnimationView = findViewById(R.id.animation_view);

        mAuth = FirebaseAuth.getInstance();

        // Obtain the Firebase Analytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Firebase Analytics initialization.
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        next = findViewById(R.id.nextBtn);
        next.setVisibility(View.INVISIBLE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (mAuth.getCurrentUser() != null) {
                    updateUI(currentUser);
                }
                if(mAuth.getCurrentUser() == null){
                    Intent login = new Intent(Splashscreen.this,Login.class);
                    startActivity(login);
                    Splashscreen.this.finish();
                }

            }
        });
        lottieAnimationView.setImageAssetsFolder("images/");
        lottieAnimationView.playAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(1000);
                next.setVisibility(View.VISIBLE);
            }
        }, TIME_OUT);
    }

    // Get current user and display the username in a toast.
    private void updateUI(FirebaseUser currentUser) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();
        //get date and time for history
        currentTime = Calendar.getInstance().getTime();

        time_date = String.valueOf(currentTime);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        year_date = String.valueOf(year);
        month_date = String.valueOf(month);
        day_date = String.valueOf(day);

        //Set the value of date and time and  push to Firebase Database.

        String date = (month_date + "/" + day_date + "/" + year_date);
        String time = time_date;
        String action = "App Launched";

        writeToHistory(userId,action,date,time);

        Toast.makeText(getApplicationContext(),"Logged in as "+currentUser.getDisplayName(), Toast.LENGTH_LONG).show();
        Intent mainIntent = new Intent(Splashscreen.this, PinFingerprintSignin.class);
        Splashscreen.this.startActivity(mainIntent);
        Splashscreen.this.finish();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onBackPressed(){
        Splashscreen.this.finish();
        super.onBackPressed();
    }

    // Navigation bar key events.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        if ((keyCode == KeyEvent.KEYCODE_HOME))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    private void writeToHistory(String id,String action, String date, String time) {

        HistoryHelper historyHelper = new HistoryHelper(action,date,time);

        mDatabase.child("users").child(id).child("history").child(time).setValue(historyHelper);
    }
}
