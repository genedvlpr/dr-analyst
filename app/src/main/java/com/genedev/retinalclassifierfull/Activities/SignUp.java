package com.genedev.retinalclassifierfull.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.genedev.retinalclassifierfull.classes.AccountEntry;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private EditText signup_name, signup_password, signup_email,conpass,hos_name,host_add;
    private Button signup, cancel;

    private LottieAnimationView lottieAnimationView;

    @SuppressLint("StaticFieldLeak")
    private static TextView error_message;
    @SuppressLint("StaticFieldLeak")
    private static TextView sign_retry, headline, subtitle;

    private FirebaseAuth mAuth;

    private static int TIME_OUT = 4000;

    private String fname,email,password, hostname,hostadd,conpassword;

    private ImageView refresh;

    private DatabaseReference mDatabase;

    private FirebaseUser user;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        lottieAnimationView = findViewById(R.id.animation_view);
        lottieAnimationView.setImageAssetsFolder("images/");
        lottieAnimationView.playAnimation();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        signup = findViewById(R.id.signupBtn);
        cancel = findViewById(R.id.cancel);

        error_message = findViewById(R.id.error_message);
        sign_retry = findViewById(R.id.sign_retry);
        signup_name = findViewById(R.id.signup_name);
        signup_email = findViewById(R.id.signup_email);
        signup_password = findViewById(R.id.signup_password);

        conpass = findViewById(R.id.signup_conpassword);
        hos_name = findViewById(R.id.signup_clinic);
        host_add = findViewById(R.id.signup_address);

        headline = findViewById(R.id.headline);
        subtitle = findViewById(R.id.subtitle);

        refresh = findViewById(R.id.refreshImg);
        refresh.setVisibility(View.GONE);

        Typeface productFontBold = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Bold.ttf");
        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        headline.setTypeface(productFontBold);
        subtitle.setTypeface(productFontRegular);
        signup.setTypeface(productFontRegular);
        cancel.setTypeface(productFontRegular);
        error_message.setTypeface(productFontRegular);
        sign_retry.setTypeface(productFontRegular);
        signup_name.setTypeface(productFontRegular);
        signup_email.setTypeface(productFontRegular);
        signup_password.setTypeface(productFontRegular);
        conpass.setTypeface(productFontRegular);
        hos_name.setTypeface(productFontRegular);
        host_add.setTypeface(productFontRegular);

        progressDialog = new ProgressDialog(SignUp.this);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        checkConnection();
    }

    private void alertDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container);

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.headline);
        text.setText("All fileds must be filled up.");
        ImageView image = (ImageView) dialog.findViewById(R.id.img_warn);
        image.setImageResource(R.drawable.ic_info_outline_white_48dp);


        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_btn);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }



    private void checkConnection(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                error_message.setText(R.string.wifiConnection);
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                error_message.setText(R.string.dataConnection);
            }
        } else {
            refresh.setVisibility(View.VISIBLE);
            sign_retry.setVisibility(View.VISIBLE);
            error_message.setText(R.string.noConnection);
            signup.setEnabled(false);
            sign_retry.setText(R.string.retryConnection);
            sign_retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });
        }
    }

    private void registerUser(){
        final String displayName = signup_name.getText().toString().trim();
        final String hostname = hos_name.getText().toString().trim();
        final String hostadd = host_add.getText().toString().trim();
        final String conpassword = conpass.getText().toString().trim();
        final String email = signup_email.getText().toString().trim();
        final String password = signup_password.getText().toString().trim();

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (displayName.isEmpty()){
            alertDialog();
            error_message.setText(R.string.emailEmpty);
            signup_name.requestFocus();
            return;
        }
        if (hostname.isEmpty()){
            alertDialog();
            error_message.setText("Hospital / Clinic name is necessary.");
            signup_name.requestFocus();
            return;
        }
        if (hostadd.isEmpty()){
            alertDialog();
            error_message.setText("Hospital / Clinic address is needed.");
            signup_name.requestFocus();
            return;
        }

        if (email.isEmpty()){
            alertDialog();
            error_message.setText(R.string.emailRequired);
            signup_email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            error_message.setText(R.string.setValidEmail);
            signup_email.requestFocus();
            return;
        }
        if (password.isEmpty()){
            alertDialog();
            error_message.setText(R.string.passwordRequired);
            signup_password.requestFocus();
            return;
        }
        if (password.length()<=6){
            error_message.setText(R.string.passwordCount);
            signup_password.requestFocus();
            return;
        }

        if (conpassword.isEmpty()){
            alertDialog();
            error_message.setText(R.string.passwordRequired);
            signup_password.requestFocus();
            return;
        }
        if (!conpassword.matches(password)){
            error_message.setText(R.string.conpass);
            signup_password.requestFocus();
            return;
        }

        else {
            progressDialog.setMessage("Signing Up");
            progressDialog.show();
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser();
                            userProfile();
                            String id = mAuth.getUid();
                            accountDetails(id,displayName,email,hostname,hostadd);
                            Toast.makeText(getApplicationContext(),"Registered Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Sign Up Failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });

    }


    private void userProfile(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(signup_name.getText().toString().trim()).build();

            user.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Display name successfully configured.", Toast.LENGTH_SHORT).show();
                                Intent set_pin = new Intent(SignUp.this, PinFingerprint.class);
                                startActivity(set_pin);
                                SignUp.this.finish();
                            }
                        }
                    });
        }

    }

    private void accountDetails(String userId,String fname,String email, String hostname, String hostaddress) {

        AccountEntry accEntry = new AccountEntry(fname,email,hostname,hostaddress);

        mDatabase.child("users").child(userId).child("accountDetails").setValue(accEntry);
    }

    public void animate(View v) {
        if (lottieAnimationView.isAnimating()) {
            lottieAnimationView.cancelAnimation();
        } else {
            lottieAnimationView.playAnimation();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(SignUp.this,Login.class);
        startActivity(back);
        SignUp.this.finish();
    }
    public void back(View v){
        Intent back = new Intent(SignUp.this,Login.class);
        startActivity(back);
        SignUp.this.finish();
    }
}
