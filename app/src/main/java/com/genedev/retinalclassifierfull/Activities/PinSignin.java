package com.genedev.retinalclassifierfull.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PinSignin extends AppCompatActivity {

    PinLockView mPinLockView;
    IndicatorDots mIndicatorDots;

    private FirebaseAuth mAuth;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private TextView headline;

    Context context = this;

    private Snackbar snackbar;
    int retries = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_setup);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        mPinLockView = findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);

        mIndicatorDots = findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);

        mAuth = FirebaseAuth.getInstance();

        headline = findViewById(R.id.headline);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        snapshotDoExist();
        headline  = findViewById(R.id.headline);

        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");
        Typeface productFontBold = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Bold.ttf");

        headline.setTypeface(productFontBold);
    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(final String pin) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            final String userId = mAuth.getUid();
            databaseReference.child("users").child(userId).child("Security").child("pincode").child("pin").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String cloudPin = snapshot.getValue().toString().trim();
                    Log.d("Pin ",""+cloudPin);//prints "Do you have data? You'll love Firebase."
                    if(pin.equals(cloudPin)){
                        alertDialog();
                    }
                    if(!pin.equals(cloudPin)) {
                        Toast.makeText(PinSignin.this,"Pin not matched. Try again.",Toast.LENGTH_SHORT).show();
                        ++retries;
                        if (retries == 5){
                            Toast.makeText(PinSignin.this,"You've reached the maximum retries. Try again later.",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(PinSignin.this, PinFingerprintSignin.class);
                            startActivity(i);
                            PinSignin.this.finish();
                        }
                    }
                    if(!snapshot.exists()){
                        View v = findViewById(R.id.pin_signin_container);
                        Snackbar.make(v,"There's no pin set in the cloud.",Snackbar.LENGTH_INDEFINITE).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        @Override
        public void onEmpty() {
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
        }
    };

    private void snapshotDoExist(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        final String userId = mAuth.getUid();
        databaseReference.child("users").child(userId).child("Security").child("pincode").child("pin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(!snapshot.exists()){
                    alertDialogSet();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        alertDialogNot();

    }

    // Navigation bar key events.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            alertDialogNot();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void back(View v){
        alertDialogNot();
    }
    private void alertDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container);

        TextView text = dialog.findViewById(R.id.headline);
        text.setText("Pin matched.");
        ImageView image = dialog.findViewById(R.id.img_warn);
        image.setImageResource(R.drawable.ic_info_outline_white_48dp);

        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        text.setTypeface(productFontRegular);

        Button dialogButton = dialog.findViewById(R.id.dialog_btn);
        dialogButton.setText("Ok");
        dialogButton.setTypeface(productFontRegular);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PinSignin.this, Home.class);
                startActivity(i);
                PinSignin.this.finish();
            }
        });
        dialog.show();
    }

    private void alertDialogNot(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container);

        TextView text = dialog.findViewById(R.id.headline);
        text.setText("Are you sure you want to go back?");
        ImageView image = dialog.findViewById(R.id.img_warn);
        image.setImageResource(R.drawable.ic_info_outline_white_48dp);

        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        text.setTypeface(productFontRegular);

        Button dialogButton = dialog.findViewById(R.id.dialog_btn);
        dialogButton.setText("Yes");
        dialogButton.setTypeface(productFontRegular);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PinSignin.this, PinFingerprintSignin.class);
                startActivity(i);
                PinSignin.this.finish();
            }
        });
        dialog.show();
    }

    private void alertDialogSet(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container);

        TextView text = dialog.findViewById(R.id.headline);
        text.setText("You didn't set any pin, try to set a 4-pin pass code that can be use to protect your patient's data.");
        ImageView image = dialog.findViewById(R.id.img_warn);
        image.setImageResource(R.drawable.ic_info_outline_white_48dp);

        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        text.setTypeface(productFontRegular);

        Button dialogButton = dialog.findViewById(R.id.dialog_btn);
        Button dialogButton_cancel = dialog.findViewById(R.id.dialog_btn_cancel);
        dialogButton_cancel.setVisibility(View.VISIBLE);
        dialogButton_cancel.setText("Cancel");
        dialogButton.setText("Ok");
        dialogButton.setTypeface(productFontRegular);
        dialogButton_cancel.setTypeface(productFontRegular);

        dialogButton_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PinSignin.this, Splashscreen.class);
                startActivity(i);
                PinSignin.this.finish();
            }
        });
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PinSignin.this, PinSetup.class);
                startActivity(i);
                PinSignin.this.finish();
            }
        });
        dialog.show();
    }


}