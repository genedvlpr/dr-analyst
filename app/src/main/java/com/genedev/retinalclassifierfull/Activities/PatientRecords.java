package com.genedev.retinalclassifierfull.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.genedev.retinalclassifierfull.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Gene on 4/29/2018.
 */

public class PatientRecords extends Activity {

    File path;
    ListView list;
    static ArrayList<String> pdf_paths=new ArrayList<String>();
    static ArrayList<String> pdf_names=new ArrayList<String>();
    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_records);

        TextView headline = findViewById(R.id.headline);
        headline.setText("PATIENT RECORDS");
        TextView subhead = findViewById(R.id.subtitle);
        subhead.setText("CLICK ONE OF THE PATIENT RECORDS BELOW\nTO VIEW IT AS PDF AND PRINT IT.");

        ImageButton imgButton = findViewById(R.id.backBtn2);
        imgButton.setVisibility(View.INVISIBLE);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        list=(ListView)findViewById(R.id.allpatients);
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);
        pdf_paths.clear();
        pdf_names.clear();

        //Access External storage
        path = new File(Environment.getExternalStorageDirectory()+"/DRAnalyst/"+"Patient Records");
        searchFolderRecursive1(path);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.content_patient_records, R.id.file_name, pdf_names);

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
        if (ContextCompat.checkSelfPermission(PatientRecords.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(PatientRecords.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(PatientRecords.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(PatientRecords.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
}