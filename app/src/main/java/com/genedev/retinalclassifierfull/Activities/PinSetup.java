package com.genedev.retinalclassifierfull.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.genedev.retinalclassifierfull.classes.PinHelper;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PinSetup extends AppCompatActivity {

    PinLockView mPinLockView;
    TextView headline;
    IndicatorDots mIndicatorDots;

    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_setup);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mPinLockView = findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);

        mIndicatorDots = findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);

        headline  = findViewById(R.id.headline);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");
        Typeface productFontBold = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Bold.ttf");

        headline.setTypeface(productFontBold);
    }
    private void pinCloudSave(String userId,String pin) {

        PinHelper pinSave = new PinHelper(pin);

        mDatabase.child("users").child(userId).child("Security").child("pincode").setValue(pinSave);
    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Toast.makeText(PinSetup.this, "Pin complete, you typed: " + pin, Toast.LENGTH_SHORT).show();
            if(pin.length()==4){
                String id = mAuth.getUid();
                pinCloudSave(id,pin);
                alertDialogSave();
            }
        }

        @Override
        public void onEmpty() {

        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {

        }
    };

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        alertDialog();

    }

    public void back(View v){
        alertDialog();
    }
    // Navigation bar key events.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            alertDialog();

        }
        if (keyCode == KeyEvent.KEYCODE_HOME)
        {
            alertDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void alertDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container);

        TextView text = dialog.findViewById(R.id.headline);
        text.setText("Pin setup is not yet completed, are you sure you want to go back?");
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

                Toast.makeText(PinSetup.this,"Pin saved to the cloud.",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(PinSetup.this, PinFingerprint.class);
                startActivity(i);
                PinSetup.this.finish();

            }
        });

        dialog.show();
    }



    private void alertDialogSave(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container);

        TextView text = dialog.findViewById(R.id.headline);
        text.setText("Are you sure you want to save this pin?");
        ImageView image = dialog.findViewById(R.id.img_warn);
        image.setImageResource(R.drawable.ic_dialpad_green_a700_48dp);

        Typeface productFontBold = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Bold.ttf");
        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        text.setTypeface(productFontRegular);

        Button dialogButton = dialog.findViewById(R.id.dialog_btn);
        dialogButton.setText("Yes");
        dialogButton.setTypeface(productFontRegular);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PinSetup.this, Home.class);
                startActivity(i);
                PinSetup.this.finish();
            }
        });

        dialog.show();
    }
}
