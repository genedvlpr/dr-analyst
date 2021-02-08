package com.genedev.retinalclassifierfull.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.genedev.retinalclassifierfull.Adapter.PatientAddInfoFragmentAdapter;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.firebase.auth.FirebaseAuth;

public class PatientAddInfoNew extends AppCompatActivity  {

    // Variable declarations.
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private LottieAnimationView lottieAnimationView;

    private FirebaseAuth mAuth;

    final int[] ICONS = new int[]{
            R.mipmap.outline_assignment_ind_24px,
            R.mipmap.outline_local_hospital_24px,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_add_info_new);

        ViewPager vp_pages = findViewById(R.id.vp_pages);
        PagerAdapter pagerAdapter=new PatientAddInfoFragmentAdapter(getSupportFragmentManager());
        vp_pages.setAdapter(pagerAdapter);

        TabLayout tbl_pages = findViewById(R.id.tbl_pages);
        tbl_pages.setupWithViewPager(vp_pages);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        lottieAnimationView = findViewById(R.id.animation_view);
        lottieAnimationView.setImageAssetsFolder("images/");
        lottieAnimationView.playAnimation();

        tbl_pages.getTabAt(0).setIcon(ICONS[0]);
        tbl_pages.getTabAt(1).setIcon(ICONS[1]);
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
            alertDialogBuilder.setMessage("Retinal Classifier v2.3.13");
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
    public void onBackPressed() {
        super.onBackPressed();
         AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure, you want to discard all patient data that you have entered?");
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent back = new Intent(PatientAddInfoNew.this,ClassificationModes.class);
                            startActivity(back);
                            PatientAddInfoNew.this.finish();
                        }
                    });

            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

    }
    public void back(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, you want to discard all patient data that you have entered?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent back = new Intent(PatientAddInfoNew.this,ClassificationModes.class);
                        startActivity(back);
                        PatientAddInfoNew.this.finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onStart(){
        super.onStart();
    }


    // Navigation bar key events.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Are you sure, you want to discard all patient data that you have entered?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent back = new Intent(PatientAddInfoNew.this,Home.class);
                                startActivity(back);
                                PatientAddInfoNew.this.finish();
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

        }
        if (keyCode == KeyEvent.KEYCODE_HOME)
        {
            PatientAddInfoNew.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }



}
