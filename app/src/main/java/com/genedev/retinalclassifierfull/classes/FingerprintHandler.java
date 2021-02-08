package com.genedev.retinalclassifierfull.classes;

/**
 * Created by Gene on 6/21/2018.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.CancellationSignal;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.genedev.retinalclassifierfull.Activities.Home;


public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context context;

    public static boolean success= false;

    private Snackbar snackbar;
    public FingerprintHandler(Context mContext) {
        context = mContext;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId,
                                      CharSequence errString) {
        Toast.makeText(context,
                "Authentication error\n" + errString,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(context,
                "Authentication failed",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId,
                                     CharSequence helpString) {
        Toast.makeText(context,
                "Authentication help\n" + helpString,
                Toast.LENGTH_LONG).show();
    }


    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {
        Toast.makeText(context,
                "Success",
                Toast.LENGTH_LONG).show();
        context.startActivity(new Intent(context, Home.class));
        ((Activity)context).finish();
    }


}