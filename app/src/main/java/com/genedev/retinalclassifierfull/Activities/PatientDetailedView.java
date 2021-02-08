package com.genedev.retinalclassifierfull.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PatientDetailedView extends AppCompatActivity {

    private TextView pName,pAge,pGender,pAddress,pResults,pClassifyType,pBirthday,pCheckupDate,doctorsName,clinicsName,clinicsAddress,pcontact;
    private String name, address, age, birthday,checkupDate,diagnosis,gender,type,contact;
    private String t_name, t_address, t_age, t_birthday, t_checkupDate, t_diagnosis, t_gender, t_type, t_contact;
    private DatabaseReference mDatabase;
    private Context context = this;
    private FirebaseAuth mAuth;
    private PatientEntry patientEntry;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private Calendar calendar;
    private Date currentTime;
    private int year,month,day;
    private String year_date,month_date,day_date,time_date;
    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;
    String doctorsName1,clinicsName1,clinicsAddress1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detailed_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

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
        pcontact = findViewById(R.id.pContact);

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

        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPDF();

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_container);

                TextView text = dialog.findViewById(R.id.headline);
                text.setText("Do you want to open the exported PDF?");
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
                        openPdf();
                        dialog.hide();
                    }
                });

                dialog.show();

            }
        });

    }
    public void back(View view){
        Intent intent = new Intent(PatientDetailedView.this, AllPatients.class);
        startActivity(intent);
        PatientDetailedView.this.finish();
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
        pcontact.setText(contact);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();
        databaseReference.child("users").child(userId).child("accountDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    AccountEntry accountEntry = dataSnapshot.getValue(AccountEntry.class);
                    doctorsName1 = accountEntry.getName();
                    clinicsName1 = accountEntry.getHostname();
                    clinicsAddress1 = accountEntry.getHostadd();

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.patient_detailed_view_delete_patient, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeFragment/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.delete_patient) {
            alertDialog();
        }
        return super.onOptionsItemSelected(item);
    }
    private void alertDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container);

        TextView text = dialog.findViewById(R.id.headline);
        text.setText("Are you sure you mwant to delete his/her information and diagnosis?");
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
                Toast.makeText(PatientDetailedView.this,"Patient details has been deleted, go to trash to recover his/her information.",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(PatientDetailedView.this, AllPatients.class);
                startActivity(i);
                PatientDetailedView.this.finish();
                String id = mAuth.getUid();
                patientEntry = new PatientEntry();

                mDatabase.child("users").child(id).child("patients").child(pName.getText().toString()).removeValue();
            }
        });

        dialog.show();
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
        t_contact = pcontact.getText().toString();

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
        String action = "Deleted A Patient Info From 'Patient Lists'";

        writeToHistory(id,action,date,time);


    }
    private void writeNewPatient(String userId,String pname,String a_name, String a_address,String a_age,String a_birthday,String a_checkupDate, String a_gender,String a_diagnosis,String a_type,String a_contact) {

        PatientEntry patientEntry = new PatientEntry(a_name,a_address,a_age,a_birthday,a_checkupDate,a_gender,a_diagnosis,a_type,a_contact);

        mDatabase.child("users").child(userId).child("trash").child(pname).setValue(patientEntry);
    }
    private void writeToHistory(String id,String action, String date, String time) {

        HistoryHelper historyHelper = new HistoryHelper(action,date,time);

        mDatabase.child("users").child(id).child("history").child(time).setValue(historyHelper);
    }

    public void createPDF()
    {
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();
        final Document doc = new Document();
        databaseReference.child("users").child(userId).child("accountDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    AccountEntry accountEntry = dataSnapshot.getValue(AccountEntry.class);
                    doctorsName1 = accountEntry.getName();
                    clinicsName1 = accountEntry.getHostname();
                    clinicsAddress1 = accountEntry.getHostadd();

                    doctorsName.setText(doctorsName1);
                    clinicsName.setText(clinicsName1);
                    clinicsAddress.setText(clinicsAddress1);
                    try {
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DRAnalyst" + "/Patient Records";

                        File dir = new File(path);
                        if(!dir.exists())
                            dir.mkdirs();

                        Log.d("PDFCreator", "PDF Path: " + path);

                        File file = new File(dir, pName.getText().toString() +".pdf");
                        file.createNewFile();
                        FileOutputStream fOut = new FileOutputStream(file);


                        PdfWriter.getInstance(doc, fOut);

                        doc.open();

                        Paragraph p1 = new Paragraph(accountEntry.getHostname());

                        Font paraFont= new Font();
                        paraFont.setSize(28);
                        p1.setAlignment(Paragraph.ALIGN_LEFT);
                        p1.setFont(paraFont);

                        doc.add(p1);


                        Paragraph p2 = new Paragraph("Address: "+accountEntry.getHostadd());

                        Font paraFont2= new Font();
                        paraFont.setSize(22);
                        p2.setAlignment(Paragraph.ALIGN_LEFT);
                        p2.setFont(paraFont2);

                        doc.add(p2);

                        Paragraph p3 = new Paragraph("Doctor In-charge: "+accountEntry.getName());

                        Font paraFont3= new Font();
                        paraFont.setSize(22);
                        p3.setAlignment(Paragraph.ALIGN_LEFT);
                        p3.setFont(paraFont3);

                        doc.add(p3);

                        Paragraph p4 = new Paragraph("\n\nPatient Name: "+pName.getText().toString());

                        Font paraFont4= new Font();
                        paraFont.setSize(22);
                        p4.setAlignment(Paragraph.ALIGN_LEFT);
                        p4.setFont(paraFont4);

                        doc.add(p4);

                        Paragraph p5 = new Paragraph("\nAddress: "+pAddress.getText().toString());

                        Font paraFont5= new Font();
                        paraFont.setSize(22);
                        p5.setAlignment(Paragraph.ALIGN_LEFT);
                        p5.setFont(paraFont5);

                        doc.add(p5);

                        Paragraph p6 = new Paragraph("\nAge: "+pAge.getText().toString());

                        Font paraFont6= new Font();
                        paraFont.setSize(22);
                        p6.setAlignment(Paragraph.ALIGN_LEFT);
                        p6.setFont(paraFont6);

                        doc.add(p6);

                        Paragraph p7 = new Paragraph("\nBirthday: "+pBirthday.getText().toString());

                        Font paraFont7= new Font();
                        paraFont.setSize(22);
                        p7.setAlignment(Paragraph.ALIGN_LEFT);
                        p7.setFont(paraFont7);

                        doc.add(p7);

                        Paragraph p8 = new Paragraph("\nContact No.: "+pcontact.getText().toString());

                        Font paraFont8= new Font();
                        paraFont.setSize(22);
                        p8.setAlignment(Paragraph.ALIGN_LEFT);
                        p5.setFont(paraFont8);

                        doc.add(p8);

                        Paragraph p9 = new Paragraph("\nCheckup Date: "+pCheckupDate.getText().toString());

                        Font paraFont9= new Font();
                        paraFont.setSize(22);
                        p9.setAlignment(Paragraph.ALIGN_LEFT);
                        p9.setFont(paraFont9);

                        doc.add(p9);

                        Paragraph p10 = new Paragraph("\nResults: "+pResults.getText().toString());

                        Font paraFont10 = new Font();
                        paraFont.setSize(22);
                        p10.setAlignment(Paragraph.ALIGN_LEFT);
                        p10.setFont(paraFont10);

                        doc.add(p10);

                        Paragraph p11 = new Paragraph("\nClassification Type: "+pClassifyType.getText().toString());

                        Font paraFont11 = new Font();
                        paraFont.setSize(22);
                        p11.setAlignment(Paragraph.ALIGN_LEFT);
                        p11.setFont(paraFont11);

                        doc.add(p11);

                        Calendar calendar;

                        calendar = Calendar.getInstance();

                        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
                        String month_name = month_date.format(calendar.getTime());

                        int year = calendar.get(Calendar.YEAR);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        Phrase footerText = new Phrase("Date Generated: "+month_name+" "+day+", "+year);

                        doc.add(footerText);

                        Toast.makeText(getApplicationContext(), "Exported as PDF", Toast.LENGTH_LONG).show();

                    } catch (DocumentException de) {
                        Log.e("PDFCreator", "DocumentException:" + de);
                    } catch (IOException e) {
                        Log.e("PDFCreator", "ioException:" + e);
                    }
                    finally
                    {
                        doc.close();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    void openPdf()
    {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/DRAnalyst" + "/Patient Records/"+pName.getText().toString() +".pdf");

        if (file.exists()) {

            Uri path = Uri.fromFile(file);

            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.setDataAndType(path, "application/pdf");

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {

                startActivity(intent);

            }

            catch (ActivityNotFoundException e) {

                Toast.makeText(PatientDetailedView.this,

                        "No Application Available to View PDF",

                        Toast.LENGTH_SHORT).show();

            }

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch (requestCode) {
            case 7:
                if (resultCode == RESULT_OK) {
                    String PathHolder = data.getData().getPath();
                    Toast.makeText(PatientDetailedView.this, PathHolder, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(PatientDetailedView.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(PatientDetailedView.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(PatientDetailedView.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(PatientDetailedView.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
}

