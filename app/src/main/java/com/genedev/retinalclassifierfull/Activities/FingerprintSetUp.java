package com.genedev.retinalclassifierfull.Activities;

import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import com.genedev.retinalclassifierfull.classes.FingerprintHandler;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class FingerprintSetUp extends AppCompatActivity {

    // Declare a string variable for the key we’re going to use in our fingerprint authentication
    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private TextView textView;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    private TextView headline, subtitle;

    private static final int FINGERPRINT_PERMISSION_REQUEST_CODE = 1337;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_set_up);

        requestPermissions(new String[]{Manifest.permission.USE_FINGERPRINT}, FINGERPRINT_PERMISSION_REQUEST_CODE);
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.USE_FINGERPRINT);

        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");
        Typeface productFontBold = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Bold.ttf");
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        headline = findViewById(R.id.headline);
        subtitle = findViewById(R.id.subtitle);

        headline.setTypeface(productFontBold);
        subtitle.setTypeface(productFontRegular);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            keyguardManager =
                    (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager =
                    (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            textView = (TextView) findViewById(R.id.warningText);

            FingerprintHandler handler  = new FingerprintHandler(this);

            if (!fingerprintManager.isHardwareDetected()) {

                textView.setText("Your device doesn't support fingerprint authentication");

            }


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                textView.setText("Please enable the fingerprint permission");

            }


            if (!fingerprintManager.hasEnrolledFingerprints()) {
                textView.setText("No fingerprint configured. Please register at least one fingerprint in your device's Settings");

            }

            if (!keyguardManager.isKeyguardSecure()) {
                textView.setText("Please enable lockscreen security in your device's Settings");
            } else {
                try {

                    generateKey();
                } catch (FingerprintException e) {
                    e.printStackTrace();
                }
                if (initCipher()) {
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler helper = new FingerprintHandler(this);
                    helper.startAuth(fingerprintManager, cryptoObject);
                }
            }


        }


    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] state) {
        if (requestCode == FINGERPRINT_PERMISSION_REQUEST_CODE && state[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }


    private void generateKey() throws FingerprintException {
        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore");


            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }


    }

    public boolean initCipher() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }



    private class FingerprintException extends Exception {

        public FingerprintException(Exception e) {
            super(e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(FingerprintSetUp.this,SignUp.class);
        startActivity(back);
        FingerprintSetUp.this.finish();
    }
}