package com.genedev.retinalclassifierfull.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.genedev.retinalclassifierfull.classes.PatientEntry;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.view.View.INVISIBLE;

public class PatientFinalInfo extends Activity {

    private ProgressDialog progressDialog;

    private DialogInterface progressDialogFinal;
    private TextView pName,pAge,pGender,pAddress,pResults,pClassifyType,pBirthday,pCheckupDate,pContact;

    private static int TIME_OUT = 3000;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_final_info);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        progressDialog = new ProgressDialog(PatientFinalInfo.this);
        progressDialog.setMessage("Loading Patient Details");
        progressDialog.show();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        pName = findViewById(R.id.pName2);
        pAge = findViewById(R.id.pAge1);
        pGender = findViewById(R.id.pGender1);
        pAddress = findViewById(R.id.pAddress1);
        pCheckupDate = findViewById(R.id.pCheckUpDate);
        pBirthday = findViewById(R.id.pBirthday);
        pResults = findViewById(R.id.pResults1);
        pClassifyType = findViewById(R.id.pClassifyType);

        pName.setVisibility(INVISIBLE);
        pAge.setVisibility(INVISIBLE);
        pGender.setVisibility(INVISIBLE);
        pBirthday.setVisibility(INVISIBLE);
        pCheckupDate.setVisibility(INVISIBLE);
        pAddress.setVisibility(INVISIBLE);
        pResults.setVisibility(INVISIBLE);
        pClassifyType.setVisibility(INVISIBLE);
        pContact.setVisibility(INVISIBLE);

        pName.setText(getIntent().getStringExtra("name"));
        pAddress.setText(getIntent().getStringExtra("address"));
        pAge.setText(getIntent().getStringExtra("age"));
        pBirthday.setText(getIntent().getStringExtra("birthday"));
        pCheckupDate.setText(getIntent().getStringExtra("checkupDate"));
        pGender.setText(getIntent().getStringExtra("gender"));
        pResults.setText(getIntent().getStringExtra("result"));
        pClassifyType.setText(getIntent().getStringExtra("classifyType"));
        pContact.setText(getIntent().getStringExtra("contact"));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                pName.setVisibility(View.VISIBLE);
                pAge.setVisibility(View.VISIBLE);
                pBirthday.setVisibility(View.VISIBLE);
                pCheckupDate.setVisibility(View.VISIBLE);
                pGender.setVisibility(View.VISIBLE);
                pAddress.setVisibility(View.VISIBLE);
                pResults.setVisibility(View.VISIBLE);
                pClassifyType.setVisibility(View.VISIBLE);
                pContact.setVisibility(View.VISIBLE);
            }
        }, TIME_OUT);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                PatientEntry patientEntry = dataSnapshot.getValue(PatientEntry.class);
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Patient data has been uploaded.", Snackbar.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent finishedSaving = new Intent(PatientFinalInfo.this, Home.class);
                        startActivity(finishedSaving);
                        PatientFinalInfo.this.finish();
                    }
                }, TIME_OUT);

                String id = mAuth.getUid();
                String c_name = getIntent().getStringExtra("name");
                String c_address = getIntent().getStringExtra("address");
                String c_checkupDate = getIntent().getStringExtra("checkupDate");
                String c_age = getIntent().getStringExtra("age");
                String c_gender = getIntent().getStringExtra("gender");
                String c_birthday = getIntent().getStringExtra("birthday");
                String c_results = getIntent().getStringExtra("result");
                String c_type = getIntent().getStringExtra("classifyType");
                String c_contact = getIntent().getStringExtra("contact");

                writeNewPatient(id,c_name,c_name,c_address,c_age,c_birthday,c_checkupDate,c_gender,c_results,c_type, c_contact);
            }
        });
    }
    private void writeNewPatient(String userId,String pname,String a_name, String a_address,String a_age,String a_birthday,String a_checkupDate, String a_gender,String a_diagnosis,String a_type, String a_contact) {

        PatientEntry patientEntry = new PatientEntry(a_name,a_address,a_age,a_birthday,a_checkupDate,a_gender,a_diagnosis,a_type,a_contact);

        mDatabase.child("users").child(userId).child("patients").child(pname).setValue(patientEntry);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, you want to revert patient data and diagnosis?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        progressDialog = new ProgressDialog(PatientFinalInfo.this);
                        progressDialog.setMessage("Reverting Patient's Data and Diagnosis.");
                        progressDialog.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent home= new Intent(PatientFinalInfo.this, Home.class);
                                startActivity(home);

                                finish();
                            }
                        }, TIME_OUT);

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
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure, you want to revert patient data and diagnosis?");
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            progressDialog = new ProgressDialog(PatientFinalInfo.this);
                            progressDialog.setMessage("Reverting Patient's Data and Diagnosis.");
                            progressDialog.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent home= new Intent(PatientFinalInfo.this, PatientAddInfo.class);
                                    startActivity(home);

                                    finish();
                                }
                            }, TIME_OUT);

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
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure, you want to revert patient data and diagnosis?");
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            progressDialog = new ProgressDialog(PatientFinalInfo.this);
                            progressDialog.setMessage("Reverting Patient's Data and Diagnosis.");
                            progressDialog.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, TIME_OUT);

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
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Are you sure, you want to revert patient data and diagnosis?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                progressDialog = new ProgressDialog(PatientFinalInfo.this);
                                progressDialog.setMessage("Reverting Patient's Data and Diagnosis.");
                                progressDialog.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent home= new Intent(PatientFinalInfo.this, PatientAddInfo.class);
                                        startActivity(home);
                                        PatientFinalInfo.this.finish();
                                    }
                                }, TIME_OUT);

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
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
    public void back(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, you want to revert patient data and diagnosis?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        progressDialog = new ProgressDialog(PatientFinalInfo.this);
                        progressDialog.setMessage("Reverting Patient's Data and Diagnosis.");
                        progressDialog.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent home= new Intent(PatientFinalInfo.this, PatientAddInfo.class);
                                startActivity(home);
                                PatientFinalInfo.this.finish();
                            }
                        }, TIME_OUT);

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
}
