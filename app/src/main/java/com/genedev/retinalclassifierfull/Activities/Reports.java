package com.genedev.retinalclassifierfull.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.genedev.retinalclassifierfull.classes.AccountEntry;
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
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Gene on 4/29/2018.
 */

public class Reports extends Activity {

    // Variable declaration.
    private ListView allPatients;
    private ProgressDialog mProgressDialog;

    // Firebase database declaration and initialization.
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // Show patients data using array from PatientEntry class.
    private ArrayList<AccountEntry> accountEntries=new ArrayList<>();

    // Firebase Auth declaration.
    private FirebaseAuth mAuth;

    private ImageButton sort;

    private TextView subtitle;
    private Context context = this;

    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;

    public static String doctorsName, clinicsName, clinicsAddress;

    File path;
    ListView list;
    static ArrayList<String> pdf_paths=new ArrayList<String>();
    static ArrayList<String> pdf_names=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);

        list = findViewById(R.id.allpatients);
        getDataFromServer();

        ImageButton imgButton = findViewById(R.id.backBtn2);
        imgButton.setVisibility(View.INVISIBLE);

        FloatingActionButton fab = findViewById(R.id.export);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateReport();
            }
        });

        pdf_paths.clear();
        pdf_names.clear();

        //Access External storage
        path = new File(Environment.getExternalStorageDirectory()+"/DRAnalyst/"+"Reports");
        searchFolderRecursive1(path);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.content_reports, R.id.file_name, pdf_names);

        //Log.e("aaaaaaaaaa", ""+pdf_names);
        Log.e("aaaaaaaaaa", ""+pdf_paths);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
            // TODO Auto-generated method stub
                String path = pdf_paths.get(arg2);
                File file = new File(path);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }

    // Getting the data from Users node, users id and patients at Firebase and then adding the patients in Arraylist and setting it to a Listview.
    public void getDataFromServer()
    {
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();
        databaseReference.child("users").child(userId).child("accountDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    AccountEntry accountEntry = dataSnapshot.getValue(AccountEntry.class);

                        doctorsName = accountEntry.getName();
                        clinicsName = accountEntry.getHostname();
                        clinicsAddress = accountEntry.getHostadd();

                    }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void populateReport(){

        mAuth = FirebaseAuth.getInstance();
        final String userId = mAuth.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = rootRef.child("users").child(userId).child("patients");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> names= new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.child("name").getValue(String.class);

                    String address = ds.child("address").getValue(String.class);
                    names.add("\nPatient Name: "+name+"\nPatient Address: "+address+getString(R.string.pdf_data_divider));

                    final Document doc = new Document();
                    try {
                        Calendar calendar;

                        calendar = Calendar.getInstance();

                        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
                        String month_name = month_date.format(calendar.getTime());
                        int year = calendar.get(Calendar.YEAR);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DRAnalyst" + "/Reports";

                        File dir = new File(path);
                        if(!dir.exists())
                            dir.mkdirs();

                        Log.d("PDFCreator", "PDF Path: " + path);

                        File file = new File(dir, "Report ("+month_name+" "+day+", "+year+").pdf");
                        file.createNewFile();
                        FileOutputStream fOut = new FileOutputStream(file);


                        PdfWriter.getInstance(doc, fOut);

                        doc.open();



                        Paragraph p4 = new Paragraph("This is a report for the month of "+String.valueOf(month_name) +" that includes all the\npatients that have been checked up. \nThis includes their name and address.");

                        Font paraFont4= new Font(Font.FontFamily.COURIER);
                        paraFont4.setSize(12);
                        p4.setAlignment(Paragraph.ALIGN_LEFT);
                        p4.setFont(paraFont4);
                        p4.setSpacingAfter(1);

                        doc.add(p4);

                        Paragraph p1 = new Paragraph(clinicsName);

                        Font paraFont= new Font();
                        paraFont.setSize(28);
                        p1.setAlignment(Paragraph.ALIGN_LEFT);
                        p1.setFont(paraFont);

                        doc.add(p1);

                        Paragraph p2 = new Paragraph("Address: "+clinicsAddress);

                        Font paraFont2= new Font();
                        paraFont.setSize(22);
                        p2.setAlignment(Paragraph.ALIGN_LEFT);
                        p2.setFont(paraFont2);

                        doc.add(p2);

                        Paragraph p3 = new Paragraph("Doctor In-charge: "+doctorsName);

                        Font paraFont3= new Font();
                        paraFont.setSize(22);
                        p3.setAlignment(Paragraph.ALIGN_LEFT);
                        p3.setFont(paraFont3);

                        doc.add(p3);



                        Paragraph p5 = new Paragraph(names+"");

                        Font paraFont5= new Font(Font.FontFamily.COURIER);
                        paraFont5.setSize(12);
                        p5.setAlignment(Paragraph.ALIGN_LEFT);
                        p5.setFont(paraFont5);
                        p5.setSpacingAfter(1);

                        doc.add(p5);





                        Phrase footerText = new Phrase("\n\n\nDate Generated: "+month_name+" "+day+", "+year);

                        doc.add(footerText);

                        Toast.makeText(getApplicationContext(), "Exported as PDF", Toast.LENGTH_SHORT).show();
                        recreate();

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
            public void onCancelled(DatabaseError databaseError) {}
        };
        ref.addListenerForSingleValueEvent(eventListener);
    }

    // Show a progress dialog upon loading patient's data.
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(Reports.this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    // Hide progress dialog when patient's data has been loaded.
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Intent back = new Intent(Reports.this,Home.class);
            startActivity(back);
            Reports.this.finish();
        }
        if ((keyCode == KeyEvent.KEYCODE_HOME))
        {
            Reports.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    public void back(View view){
        Intent back = new Intent(Reports.this,Home.class);
        startActivity(back);
        Reports.this.finish();
    }

    public void info_db(View view){
        alertDialog();
    }
    private void alertDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container);

        TextView text = dialog.findViewById(R.id.headline);
        text.setText(R.string.patient_list_info);
        ImageView image = dialog.findViewById(R.id.img_warn);
        image.setImageResource(R.drawable.ic_info_outline_white_48dp);

        Typeface productFontRegular = Typeface.createFromAsset(getAssets(),  "fonts/Product Sans Regular.ttf");

        text.setTypeface(productFontRegular);

        Button dialogButton = dialog.findViewById(R.id.dialog_btn);
        dialogButton.setText("Okay");
        dialogButton.setTypeface(productFontRegular);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private static void searchFolderRecursive1(File folder)
    {
        if (folder != null)
        {
            if (folder.listFiles() != null)
            {
                for (File file : folder.listFiles())
                {
                    if (file.isFile())
                    {
                        //.pdf files
                        if(file.getName().contains(".pdf"))
                        {
                            Log.e("ooooooooooooo", "path__="+file.getName());
                            file.getPath();
                            pdf_names.add(file.getName());
                            pdf_paths.add(file.getPath());
                            Log.e("pdf_paths", ""+pdf_names);
                        }
                    }
                    else
                    {
                        searchFolderRecursive1(file);
                    }
                }
            }
        }
    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(Reports.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Reports.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(Reports.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(Reports.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
}
