package com.genedev.retinalclassifierfull.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.genedev.retinalclassifierfull.R;

public class ClassificationModes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classification_modes);
    }

    public void captureImage(View v){
        Intent captureImage = new Intent(ClassificationModes.this, ClassifierCaptured.class);
        startActivity(captureImage);
        ClassificationModes.this.finish();
    }

    public void importImage(View v){
        Intent importImage = new Intent(ClassificationModes.this, ClassifierImport.class);
        startActivity(importImage);
        ClassificationModes.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent i = new Intent(ClassificationModes.this,Home.class);
            startActivity(i);
            ClassificationModes.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void back(View view){
        Intent i = new Intent(ClassificationModes.this,Home.class);
        startActivity(i);
        ClassificationModes.this.finish();
    }
}
