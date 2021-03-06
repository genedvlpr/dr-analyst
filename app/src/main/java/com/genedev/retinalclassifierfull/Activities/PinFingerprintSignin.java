package com.genedev.retinalclassifierfull.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;

public class PinFingerprintSignin extends AppCompatActivity {

    private Button set_pin, set_fingerprint, btn_cancel;

    private LottieAnimationView lottieAnimationView;

    private TextView headline;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_fingerprint_sigin);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        set_pin = findViewById(R.id.set_pin);
        set_fingerprint = findViewById(R.id.set_fingerprint);
        btn_cancel = findViewById(R.id.cancel);
        headline = findViewById(R.id.headline);

        lottieAnimationView = findViewById(R.id.animation_view);
        lottieAnimationView.setImageAssetsFolder("images/");
        lottieAnimationView.playAnimation();

        Typeface productFontBold = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Bold.ttf");
        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        headline.setTypeface(productFontBold);

        set_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setPin = new Intent(PinFingerprintSignin.this, PinSignin.class);
                startActivity(setPin);
                PinFingerprintSignin.this.finish();
            }
        });

        set_fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setPin = new Intent(PinFingerprintSignin.this, FingerprintSignin.class);
                startActivity(setPin);
                PinFingerprintSignin.this.finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });
    }

    private void alertDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container);

        TextView text = dialog.findViewById(R.id.headline);
        text.setText("You must enter your pin or unlock the app by your fingerprint to access your patient's data or take an analysis. Are you sure you want to exit?");
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
                finish();
            }
        });

        dialog.show();
    }
    public void back(View v){
        alertDialog();
    }
}
