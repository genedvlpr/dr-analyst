package com.genedev.retinalclassifierfull.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.genedev.retinalclassifierfull.classes.AccountEntry;
import com.genedev.retinalclassifierfull.classes.HistoryHelper;
import com.genedev.retinalclassifierfull.classes.PatientEntry;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class PatientDeletedDetailedView extends AppCompatActivity {

    private TextView pName,pAge,pGender,pAddress,pResults,pClassifyType,pBirthday,pCheckupDate,doctorsName,clinicsName,clinicsAddress,pContact;
    private String name, address, age, birthday,checkupDate,diagnosis,gender,type,contact;
    private String t_name, t_address, t_age, t_birthday, t_checkupDate, t_diagnosis, t_gender, t_type, t_contact;
    private DatabaseReference mDatabase;
    private Context context = this;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    PatientEntry patientEntry;


    private Calendar calendar;
    private Date currentTime;
    private int year,month,day;
    private String year_date,month_date,day_date,time_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detailed_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");

        pName = findViewById(R.id.pName2);
        pAge = findViewById(R.id.pAge1);
        pGender = findViewById(R.id.pGender1);
        pAddress = findViewById(R.id.pAddress1);
        pCheckupDate = findViewById(R.id.pCheckUpDate);
        pBirthday = findViewById(R.id.pBirthday);
        pResults = findViewById(R.id.pResults1);
        pClassifyType = findViewById(R.id.pClassifyType);
        doctorsName = findViewById(R.id.doctors_name);
        clinicsName = findViewById(R.id.clinics_name);
        clinicsAddress = findViewById(R.id.clinics_address);
        pContact = findViewById(R.id.pContact);

        Intent i=this.getIntent();

        name = i.getExtras().getString("NAME_KEY");
        address = i.getExtras().getString("ADDRESS_KEY");
        age = i.getExtras().getString("AGE_KEY");
        birthday = i.getExtras().getString("BIRTHDAY");
        checkupDate = i.getExtras().getString("CHECKUP_DATE_KEY");
        diagnosis = i.getExtras().getString("DIAGNOSIS_KEY");
        gender= i.getExtras().getString("GENDER_KEY");
        type = i.getExtras().getString("TYPE_KEY");
        contact = i.getExtras().getString("CONTACT_KEY");

        populateData();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_history_white_24dp));
        //fab.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_history_white_24dp));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog();
            }
        });
    }

    private void populateData(){
        pName.setText(name);
        pAddress.setText(address);
        pAge.setText(age);
        pGender.setText(gender);
        pCheckupDate.setText(checkupDate);
        pResults.setText(diagnosis);
        pBirthday.setText(birthday);
        pClassifyType.setText(type);
        pContact.setText(contact);

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
        Intent intent = new Intent(PatientDeletedDetailedView.this, Trash.class);
        startActivity(intent);
        PatientDeletedDetailedView.this.finish();
    }

    private void alertDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container);

        TextView text = dialog.findViewById(R.id.headline);
        text.setText("Are you sure you want to recover his/her information and diagnosis?");
        ImageView image = dialog.findViewById(R.id.img_warn);
        image.setImageResource(R.drawable.ic_info_outline_white_48dp);

        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        text.setTypeface(productFontRegular);

        Button dialogButton = dialog.findViewById(R.id.dialog_btn);
        dialogButton.setText("Yes");
        dialogButton.setTypeface(productFontRegular);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePatientDetails();
                Toast.makeText(PatientDeletedDetailedView.this,"Patient details has been recovered, go to patient list to view his/her information.",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(PatientDeletedDetailedView.this, AllPatients.class);
                startActivity(i);
                PatientDeletedDetailedView.this.finish();
                String id = mAuth.getUid();
                patientEntry = new PatientEntry();

                mDatabase.child("users").child(id).child("trash").child(pName.getText().toString()).removeValue();
            }
        });

        dialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.patient_detailed_view_delete_info, menu);

        return true;
    }
    private void alertDialog1(String message){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container);

        TextView text = dialog.findViewById(R.id.headline);
        text.setText(message);
        ImageView image = dialog.findViewById(R.id.img_warn);
        image.setImageResource(R.drawable.ic_info_outline_white_48dp);

        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        text.setTypeface(productFontRegular);

        Button dialogButton = dialog.findViewById(R.id.dialog_btn);
        dialogButton.setText("Dismiss");
        dialogButton.setTypeface(productFontRegular);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void alertDialogDeletePermanent(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container);

        TextView text = dialog.findViewById(R.id.headline);
        text.setText("Delete this patient from your account permanently?");
        ImageView image = dialog.findViewById(R.id.img_warn);
        image.setImageResource(R.drawable.ic_info_outline_white_48dp);

        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        text.setTypeface(productFontRegular);

        Button dialogButton = dialog.findViewById(R.id.dialog_btn);
        dialogButton.setText("Dismiss");
        dialogButton.setTypeface(productFontRegular);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button dialogButton1 = dialog.findViewById(R.id.dialog_btn_cancel);
        dialogButton1.setText("Proceed");
        dialogButton1.setVisibility(View.VISIBLE);
        dialogButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePatientDetailsPermanent();
                Toast.makeText(PatientDeletedDetailedView.this,"Patient details has been deleted permanently.",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(PatientDeletedDetailedView.this, Trash.class);
                startActivity(i);
                PatientDeletedDetailedView.this.finish();
                String id = mAuth.getUid();
                patientEntry = new PatientEntry();

                mDatabase.child("users").child(id).child("trash").child(pName.getText().toString()).removeValue();
            }
        });
        dialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeFragment/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.delete_patient) {
            alertDialogDeletePermanent();
        }
        return super.onOptionsItemSelected(item);
    }
    private void deletePatientDetails(){

        t_name = pName.getText().toString();
        t_address = pAddress.getText().toString();
        t_age = pAge.getText().toString();
        t_birthday = pBirthday.getText().toString();
        t_checkupDate = pCheckupDate.getText().toString();
        t_diagnosis = pResults.getText().toString();
        t_gender = pGender.getText().toString();
        t_type = pClassifyType.getText().toString();
        t_contact = pContact.getText().toString();

        String id = mAuth.getUid();

        writeNewPatient(id,t_name,t_name,t_address,t_age,t_birthday,t_checkupDate,t_gender,t_diagnosis,t_type,t_contact);

        //get date and time for history
        currentTime = Calendar.getInstance().getTime();

        time_date = String.valueOf(currentTime);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        year_date = String.valueOf(year);
        month_date = String.valueOf(month);
        day_date = String.valueOf(day);
        //Set the value of date and time and  push to Firebase Database.

        String date = (month_date + "/" + day_date + "/" + year_date);
        String time = time_date;
        String action = "Restored A Patient Info Into 'Patient Lists'";

        writeToHistory(id,action,date,time);
    }
    private void deletePatientDetailsPermanent(){

        t_name = pName.getText().toString();
        t_address = pAddress.getText().toString();
        t_age = pAge.getText().toString();
        t_birthday = pBirthday.getText().toString();
        t_checkupDate = pCheckupDate.getText().toString();
        t_diagnosis = pResults.getText().toString();
        t_gender = pGender.getText().toString();
        t_type = pClassifyType.getText().toString();
        t_contact = pContact.getText().toString();

        String id = mAuth.getUid();
        //get date and time for history
        currentTime = Calendar.getInstance().getTime();

        time_date = String.valueOf(currentTime);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        year_date = String.valueOf(year);
        month_date = String.valueOf(month);
        day_date = String.valueOf(day);
        //Set the value of date and time and  push to Firebase Database.

        String date = (month_date + "/" + day_date + "/" + year_date);
        String time = time_date;
        String action = "Patient "+t_name+ " has been deleted from the database.";

        writeToHistory(id,action,date,time);
    }
    private void writeNewPatient(String userId,String pname,String a_name, String a_address,String a_age,String a_birthday,String a_checkupDate, String a_gender,String a_diagnosis,String a_type,String a_contact) {

        PatientEntry patientEntry = new PatientEntry(a_name,a_address,a_age,a_birthday,a_checkupDate,a_gender,a_diagnosis,a_type,a_contact);

        mDatabase.child("users").child(userId).child("patients").child(pname).setValue(patientEntry);
    }
    private void writeToHistory(String id,String action, String date, String time) {

        HistoryHelper historyHelper = new HistoryHelper(action,date,time);

        mDatabase.child("users").child(id).child("history").child(time).setValue(historyHelper);
    }
}
