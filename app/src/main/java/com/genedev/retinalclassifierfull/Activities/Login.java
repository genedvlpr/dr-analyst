package com.genedev.retinalclassifierfull.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.genedev.retinalclassifierfull.classes.HistoryHelper;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class Login extends Activity {

    private ProgressDialog progressDialog;

    private Button login, register;

    private EditText login_password, login_email;

    private TextView message,login_retry, headline, subtitle;

    private LottieAnimationView lottieAnimationView;

    private FirebaseAuth mAuth;

    private ImageView refresh;

    private String email,password;

    private CoordinatorLayout coordinatorLayout;
    final Context context = this;

    private Snackbar snackbar;
    private static int TIME_OUT = 3000;

    private Calendar calendar;
    private Date currentTime;
    private int year,month,day;
    private String year_date,month_date,day_date,time_date;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        lottieAnimationView = findViewById(R.id.animation_view);

        message = findViewById(R.id.message);
        login_retry = findViewById(R.id.login_retry);

        login_retry.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        refresh = findViewById(R.id.refreshImg);
        refresh.setVisibility(View.GONE);

        login = findViewById(R.id.loginBtn);
        register = findViewById(R.id.registerBtn);

        coordinatorLayout = findViewById(R.id.coordinator);

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);

        headline = findViewById(R.id.headline);
        subtitle = findViewById(R.id.subtitle);

        Typeface productFontBold = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Bold.ttf");
        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        headline.setTypeface(productFontBold);
        subtitle.setTypeface(productFontRegular);
        message.setTypeface(productFontRegular);
        login_email.setTypeface(productFontRegular);
        login_password.setTypeface(productFontRegular);
        login_retry.setTypeface(productFontRegular);
        login.setTypeface(productFontRegular);
        register.setTypeface(productFontRegular);

        progressDialog = new ProgressDialog(Login.this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginUser();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(Login.this, SignUp.class);
                Login.this.startActivity(mainIntent);
                Login.this.finish();
            }
        });
        lottieAnimationView.setImageAssetsFolder("images/");
        lottieAnimationView.playAnimation();

        checkConnection();
    }

    private void checkConnection(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                message.setText(R.string.wifiConnection);
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                message.setText(R.string.dataConnection);
            }
        } else {
            message.setText(R.string.noConnection);
            login.setEnabled(false);
            register.setEnabled(false);
            login_retry.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.VISIBLE);
            login_retry.setText(R.string.retryConnection);
            login_retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });
        }
    }
    private void alertDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container);

        // set the custom dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.headline);
        text.setText("All fields must be filled up.");
        ImageView image = dialog.findViewById(R.id.img_warn);
        image.setImageResource(R.drawable.ic_info_outline_white_48dp);

        Typeface productFontBold = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Bold.ttf");
        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        text.setTypeface(productFontRegular);

        Button dialogButton = dialog.findViewById(R.id.dialog_btn);
        dialogButton.setTypeface(productFontRegular);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void loginUser(){

        email = login_email.getText().toString().trim();
        password = login_password.getText().toString().trim();

        if (email.isEmpty()){
            alertDialog();
            message.setText(R.string.emailRequired);
            login_email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            message.setText(R.string.setValidEmail);
            login_email.requestFocus();
            return;
        }
        if (password.isEmpty()){
            alertDialog();
            message.setText(R.string.passwordRequired);
            login_password.requestFocus();
            return;
        }
        if (login_password.length()<8){
            message.setText(R.string.passwordCount);
            login_password.requestFocus();
            return;
        }

        else{
            progressDialog.setMessage("Logging in");
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, TIME_OUT);
        }

        signInWithEmailPassword();
    }
    private void signInWithEmailPassword(){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            View v = findViewById(R.id.content_login);
                            snackbar.make(v,"Successfully logged in",snackbar.LENGTH_SHORT).show();
                            mAuth.getCurrentUser();

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
                            String action = "Logged In";
                            String id = mAuth.getUid();

                            writeToHistory(id,action,date,time);

                            Intent mainIntent = new Intent(Login.this, Home.class);
                            Login.this.startActivity(mainIntent);
                            Login.this.finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            message.setText(R.string.notValidAcc);
                        }
                    }
                });
    }
    private void writeToHistory(String id,String action, String date, String time) {

        HistoryHelper historyHelper = new HistoryHelper(action,date,time);

        mDatabase.child("users").child(id).child("history").child(time).setValue(historyHelper);
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Login.this.finish();
        }
        if (keyCode == KeyEvent.KEYCODE_HOME)
        {
            Login.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
