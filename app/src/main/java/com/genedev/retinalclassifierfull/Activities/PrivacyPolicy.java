package com.genedev.retinalclassifierfull.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.genedev.retinalclassifierfull.R;

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        WebView simpleWebView = findViewById(R.id.privacy_policy_view);
        // specify the url of the web page in loadUrl function
        simpleWebView.loadUrl("https://retinal-image-classifier.firebaseapp.com/dr_analyst_privacy_policy_new.1.html");
    }
}
