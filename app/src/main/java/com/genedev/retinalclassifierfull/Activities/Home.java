package com.genedev.retinalclassifierfull.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.genedev.retinalclassifierfull.Adapter.HomeFragmentAdapter;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Variable declarations.
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FirebaseAuth mAuth;
    NavigationView navigationView;
    private TextView userName, emailAdd;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        navigationView.getHeaderView(0);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        // Firebase Authentication initialization that gets the current user.
        mAuth = FirebaseAuth.getInstance();

        userName = navigationView.getHeaderView(0).findViewById(R.id.userName);
        emailAdd = navigationView.getHeaderView(0).findViewById(R.id.emailAdd);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "DR Analyst Version 2.3.14", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        ViewPager vp_pages = (ViewPager) findViewById(R.id.vp_pages);
        PagerAdapter pagerAdapter=new HomeFragmentAdapter(getSupportFragmentManager());
        vp_pages.setAdapter(pagerAdapter);

        TabLayout tbl_pages = (TabLayout) findViewById(R.id.tbl_pages);
        tbl_pages.setupWithViewPager(vp_pages);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Home.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeFragment/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Diabetic Retinopathy Analyst (v2.3.14)");
            alertDialogBuilder.setNegativeButton("Okay",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        // Get current user
        setDataToView(user);
        //alertDialog();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id;
        id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent home = new Intent(Home.this,Home.class);
            startActivity(home);
            Home.this.finish();
        }

        else if (id == R.id.nav_classifier) {
            Intent home = new Intent(Home.this,ClassificationModes.class);
            startActivity(home);
        }

        else if (id == R.id.nav_patient_details) {
            Intent patients = new Intent(Home.this,AllPatients.class);
            startActivity(patients);
            Home.this.finish();
        }

        else if (id == R.id.nav_account) {
            Intent account = new Intent(Home.this,AccountDetails.class);
            startActivity(account);
            Home.this.finish();

        }

        else if (id == R.id.nav_about) {
            Intent about = new Intent(Home.this,About.class);
            startActivity(about);
        }

        else if (id == R.id.nav_privacy_policy) {
            Intent privacy_policy = new Intent(Home.this,PrivacyPolicy.class);
            startActivity(privacy_policy);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Navigation bar key events.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Home.this.finish();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME)
        {
            Home.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // Set username at email to the navigation header.
    @SuppressLint("SetTextI18n")
    private void setDataToView(FirebaseUser user) {
        // Get the user's email using Firebase Authentication.
        emailAdd.setText(user.getEmail());
        // Get the user's username using Firebase Authentication.
        userName.setText(user.getDisplayName());
    }
    private void alertDialog(){
        FirebaseUser user = mAuth.getCurrentUser();
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container_beta_tester);

        // set the custom dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.headline);
        text.setText("Welcome " + user.getDisplayName() + ", you are a Beta tester.");

        Typeface productFontBold = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Bold.ttf");
        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        text.setTypeface(productFontRegular);

        Button dialogButton = dialog.findViewById(R.id.dialog_btn);
        dialogButton.setTypeface(productFontRegular);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
