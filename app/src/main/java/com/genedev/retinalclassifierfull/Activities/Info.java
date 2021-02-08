package com.genedev.retinalclassifierfull.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;

public class Info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Info.this, Home.class);
                startActivity(intent);
                Info.this.finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Info.this, Home.class);
            startActivity(intent);
            Info.this.finish();
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Info.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(Info.this, Home.class);
                startActivity(intent);
                Info.this.finish();
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
