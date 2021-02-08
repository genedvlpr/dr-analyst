package com.genedev.retinalclassifierfull.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;

import java.io.File;

import static android.view.View.VISIBLE;

public class ImageLibrary extends AppCompatActivity {

    // Variable declarations.
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] listFile;
    GridView grid;
    ListAdapter adapter;
    File file;

    private static int TIME_OUT = 3000;

    private GridView gridView;

    private ProgressDialog progressDialog;

    String passName,passAddress,passAge,passGender,passBirthday,passCheckupDate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridview_main);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        // Get patient's info filled from PatientAddInfo class.
        passName = getIntent().getStringExtra("name");
        passAddress = getIntent().getStringExtra("address");
        passAge = getIntent().getStringExtra("age");
        passGender = getIntent().getStringExtra("gender");
        passBirthday = getIntent().getStringExtra("birthday");
        passCheckupDate = getIntent().getStringExtra("checkupDate");

        gridView = findViewById(R.id.gridview);

        gridView.setVisibility(View.INVISIBLE);

        progressDialog = new ProgressDialog(ImageLibrary.this);
        progressDialog.setMessage("Loading Images");
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                gridView.setVisibility(VISIBLE);
            }
        }, TIME_OUT);

        // Check for SD Card.
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "Error! No SDCARD Found!", Toast.LENGTH_LONG)
                    .show();
        } else {
            // Locate the image folder in your SD Card.
            file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Retinal Images");
            // Create a new folder if no folder named SDImageTutorial exist
            file.mkdirs();
        }

        if (file.isDirectory()) {
            listFile = file.listFiles();
            // Create a String array for FilePathStrings
            FilePathStrings = new String[listFile.length];
            // Create a String array for FileNameStrings
            FileNameStrings = new String[listFile.length];

            for (int i = 0; i < listFile.length; i++) {
                // Get the path of the image file.
                FilePathStrings[i] = listFile[i].getAbsolutePath();
                // Get the name image file.
                FileNameStrings[i] = listFile[i].getName();
            }
        }

        // Locate the GridView in gridview_main.xml
        grid = (GridView) findViewById(R.id.gridview);
        // Pass String arrays to LazyAdapter Class.
        adapter = new GridViewAdapter(this, FilePathStrings, FileNameStrings);
        // Set the LazyAdapter to the GridView.
        grid.setAdapter(adapter);

        // Capture gridview item click
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                Intent i = new Intent(ImageLibrary.this, ClassifierImport.class);
                // Pass String arrays FilePathStrings.
                i.putExtra("filepath", FilePathStrings);
                // Pass String arrays FileNameStrings.
                i.putExtra("filename", FileNameStrings);
                // Pass click position.
                i.putExtra("position", position);
                i.putExtra("name", passName);
                i.putExtra("address", passAddress);
                i.putExtra("age", passAge);
                i.putExtra("gender", passGender);
                i.putExtra("birthday", passBirthday);
                i.putExtra("checkupDate", passCheckupDate);
                startActivity(i);
                ImageLibrary.this.finish();
            }

        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        passStrings();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            passStrings();
        }
        if (keyCode == KeyEvent.KEYCODE_HOME)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure, you want discard patient information?");
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            progressDialog = new ProgressDialog(ImageLibrary.this);
                            progressDialog.setMessage("Discarding Patient's Data.");
                            progressDialog.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ImageLibrary.this.finish();
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
                passStrings();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void passStrings(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, you want to go back?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent back = new Intent(ImageLibrary.this,ClassifierCaptured.class);
                        back.putExtra("name", passName);
                        back.putExtra("address", passAddress);
                        back.putExtra("age", passAge);
                        back.putExtra("gender", passGender);
                        back.putExtra("birthday", passBirthday);
                        back.putExtra("checkupDate", passCheckupDate);
                        startActivity(back);
                        ImageLibrary.this.finish();
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