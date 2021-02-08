package com.genedev.retinalclassifierfull.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.genedev.retinalclassifierfull.R;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(About.this,Home.class);
        startActivity(back);
        About.this.finish();
    }
    public void back(View view){
        Intent back = new Intent(About.this,Home.class);
        startActivity(back);
        About.this.finish();
    }
}
