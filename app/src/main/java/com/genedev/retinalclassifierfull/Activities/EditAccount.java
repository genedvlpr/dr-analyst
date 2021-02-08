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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.genedev.retinalclassifierfull.classes.AccountEntry;
import com.genedev.retinalclassifierfull.classes.HistoryHelper;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class EditAccount extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private EditText signup_name, signup_old_password, signup_email,conpass,hos_name,host_add, signup_new_password;
    private Button signup, cancel;


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
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private Calendar calendar;
    private Date currentTime;
    private int year,month,day;
    private String year_date,month_date,day_date,time_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        signup = findViewById(R.id.signupBtn);
        cancel = findViewById(R.id.cancel);

        error_message = findViewById(R.id.error_message);
        sign_retry = findViewById(R.id.sign_retry);
        signup_name = findViewById(R.id.signup_name);
        signup_old_password = findViewById(R.id.signup_password);
        signup_new_password = findViewById(R.id.signup_newpassword);

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
        signup.setTypeface(productFontRegular);
        cancel.setTypeface(productFontRegular);
        error_message.setTypeface(productFontRegular);
        sign_retry.setTypeface(productFontRegular);
        signup_name.setTypeface(productFontRegular);
        signup_old_password.setTypeface(productFontRegular);
        conpass.setTypeface(productFontRegular);
        hos_name.setTypeface(productFontRegular);
        host_add.setTypeface(productFontRegular);

        progressDialog = new ProgressDialog(EditAccount.this);

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
        final String password = signup_new_password.getText().toString().trim();
        final String old_pass = signup_old_password.getText().toString();
        String old_email;
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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

        if (old_pass.isEmpty()){
            alertDialog();
            error_message.setText(R.string.passwordRequired);
            signup_old_password.requestFocus();
            return;
        }
        if (old_pass.length()<=6){
            error_message.setText(R.string.passwordCount);
            signup_new_password.requestFocus();
            return;
        }

        if (password.isEmpty()){
            alertDialog();
            error_message.setText(R.string.passwordRequired);
            signup_old_password.requestFocus();
            return;
        }
        if (password.length()<=6){
            error_message.setText(R.string.passwordCount);
            signup_new_password.requestFocus();
            return;
        }

        if (conpassword.isEmpty()){
            alertDialog();
            error_message.setText(R.string.passwordRequired);
            conpass.requestFocus();
            return;
        }
        if (!conpassword.matches(password)){
            error_message.setText(R.string.conpass);
            conpass.requestFocus();
            return;
        }

        else {
            progressDialog.setMessage("Processing");
            progressDialog.show();
            mAuth.getCurrentUser();
            old_email = user.getEmail();

            userProfile();
            String id = mAuth.getUid();
            accountDetails(id,displayName,email,hostname,hostadd);

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
            String action = "Account Has Been Edited";

            writeToHistory(id,action,date,time);

            Toast.makeText(getApplicationContext(),"Registered Successfully", Toast.LENGTH_SHORT).show();
        }

        AuthCredential credential = EmailAuthProvider.getCredential(old_email,old_pass);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("New Password", "updated");
                            }
                            else {
                                Log.d("Error Password", "updated");
                            }
                        }
                    });
                }
                else {
                    Log.d("Error Password", "updated");
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
                                Intent set_pin = new Intent(EditAccount.this, AccountDetails.class);
                                startActivity(set_pin);
                                EditAccount.this.finish();
                            }
                        }
                    });
        }
    }

    private void accountDetails(String userId,String fname,String email, String hostname, String hostaddress) {

        AccountEntry accEntry = new AccountEntry(fname,email,hostname,hostaddress);

        mDatabase.child("users").child(userId).child("accountDetails").setValue(accEntry);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(EditAccount.this,AccountDetails.class);
        startActivity(back);
        EditAccount.this.finish();
    }

    private void writeToHistory(String id,String action, String date, String time) {

        HistoryHelper historyHelper = new HistoryHelper(action,date,time);

        mDatabase.child("users").child(id).child("history").child(time).setValue(historyHelper);
    }
    public void back(View view){
        Intent back = new Intent(EditAccount.this,AccountDetails.class);
        startActivity(back);
        EditAccount.this.finish();
    }
}
