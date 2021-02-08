package com.genedev.retinalclassifierfull.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;

public class PinFingerprint extends AppCompatActivity {

    private Button set_pin, set_fingerprint, btn_cancel;

    private LottieAnimationView lottieAnimationView;

    private TextView headline;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_fingerprint);

        set_pin = findViewById(R.id.set_pin);
        set_fingerprint = findViewById(R.id.set_fingerprint);
        btn_cancel = findViewById(R.id.cancel);
        headline = findViewById(R.id.headline);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        lottieAnimationView = findViewById(R.id.animation_view);
        lottieAnimationView.setImageAssetsFolder("images/");
        lottieAnimationView.playAnimation();

        Typeface productFontBold = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Bold.ttf");
        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        headline.setTypeface(productFontBold);

        set_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setPin = new Intent(PinFingerprint.this, PinSetup.class);
                startActivity(setPin);
                PinFingerprint.this.finish();
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
        text.setText("Set up is not yet completed, please set at least one security authentication method. Are you sure you want to go back? ");
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
                Intent i = new Intent(PinFingerprint.this, SignUp.class);
                startActivity(i);
                PinFingerprint.this.finish();

            }
        });

        dialog.show();
    }

    public void back(View v){
        alertDialog();
    }
}
