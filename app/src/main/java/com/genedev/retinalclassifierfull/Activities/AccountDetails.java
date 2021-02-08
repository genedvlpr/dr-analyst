package com.genedev.retinalclassifierfull.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.genedev.retinalclassifierfull.classes.AccountEntry;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountDetails extends Activity {

    private FirebaseAuth mAuth;
    private TextView email,name,doctorsName,clinicsName,clinicsAddress;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");

        email = findViewById(R.id.diagnosis);
        name = findViewById(R.id.user_name);
        doctorsName = findViewById(R.id.doctors_name);
        clinicsName = findViewById(R.id.clinics_name);
        clinicsAddress = findViewById(R.id.clinics_address);
        Button btnLogout = findViewById(R.id.logout_btn);
        Button btnEdit = findViewById(R.id.edit_btn);
        //get firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(AccountDetails.this);

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setDataToView(user);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Loading");
                progressDialog.show();
                Intent edit = new Intent(AccountDetails.this,EditAccount.class);
                startActivity(edit);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Signing Out");
                progressDialog.show();
                mAuth.signOut();
                Intent login = new Intent(AccountDetails.this,Login.class);
                startActivity(login);
                AccountDetails.this.finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
                Intent back = new Intent(AccountDetails.this,Home.class);
                startActivity(back);
                AccountDetails.this.finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }


    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(AccountDetails.this,Home.class);
        startActivity(back);
        AccountDetails.this.finish();
    }

    @SuppressLint("SetTextI18n")
    private void setDataToView(FirebaseUser user) {
        email.setText(user.getEmail());
        name.setText(user.getDisplayName());
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();
        databaseReference.child("users").child(userId).child("accountDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    AccountEntry accountEntry = dataSnapshot.getValue(AccountEntry.class);
                    String doctorsName1 = accountEntry.getName();
                    String clinicsName1 = accountEntry.getHostname();
                    String clinicsAddress1 = accountEntry.getHostadd();

                    doctorsName.setText(doctorsName1);
                    clinicsName.setText(clinicsName1);
                    clinicsAddress.setText(clinicsAddress1);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void back(View view){
        Intent back = new Intent(AccountDetails.this,Home.class);
        startActivity(back);
        AccountDetails.this.finish();
    }
}
