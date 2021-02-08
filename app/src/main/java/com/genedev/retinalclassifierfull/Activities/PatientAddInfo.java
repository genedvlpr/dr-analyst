package com.genedev.retinalclassifierfull.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;

import java.util.Calendar;

public class PatientAddInfo extends AppCompatActivity {

    // Variable declarations.
    private DatePicker datePicker;
    private Calendar calendar,calendar1,calendar2;
    private TextView dateView,dateView2;
    private int year, month, day;

    private SeekBar seekBarAge;
    private EditText pName,pAddress;
    private RadioButton genderMale,genderFemale;
    private TextView seekBarValue,checkupdateView,checkupdateView2;
    private RadioGroup radioGroup;
    private String name,address,seekbarAge,gender,birthday,checkupDate,year1,year2;
    private FloatingActionButton fab;

    private static int TIME_OUT = 3000;
    int ageSum,yearBirth,yearCurrent;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_add_info);
        TypeFaceUtil.overrideFont(getApplicationContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        radioGroup = findViewById(R.id.genderRadioGroup);
        seekBarValue = findViewById(R.id.pAgeLabel);
        pName = findViewById(R.id.pName);
        pAddress = findViewById(R.id.pAddress);
        seekBarAge = findViewById(R.id.seekBarAge);
        genderMale = findViewById(R.id.genderMale);
        genderFemale = findViewById(R.id.genderfemale);

        LottieAnimationView lottieAnimationView = findViewById(R.id.animation_view);

        lottieAnimationView.playAnimation();

        seekbarData();

        dateView = findViewById(R.id.tvDate);
        dateView2 = findViewById(R.id.tvDate2);
        checkupdateView = findViewById(R.id.tvCheckupDate);
        checkupdateView2 = findViewById(R.id.tvCheckupDate2);
        calendar = Calendar.getInstance();

        calendar1 = Calendar.getInstance();
        calendar2 = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showCheckupDate(year, month+1, day);

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pName.getText().toString().isEmpty()) {
                    Snackbar.make(view, "All fields must not be empty.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (pAddress.getText().toString().isEmpty()) {
                    Snackbar.make(view, "All fields must not be empty.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (seekBarValue.getText().toString().isEmpty()) {
                    Snackbar.make(view, "All fields must not be empty.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (!genderMale.isChecked() && !genderFemale.isChecked()) {
                    Snackbar.make(view, "All fields must not be empty.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (dateView.getText().toString().isEmpty()) {
                    Snackbar.make(view, "All fields must not be empty.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(!pName.getText().toString().isEmpty() || !pAddress.getText().toString().isEmpty() ||  !seekBarValue.getText().toString().isEmpty() || genderMale.isChecked() || genderFemale.isChecked() || !dateView.getText().toString().isEmpty()) {

                    name = pName.getText().toString();
                    address = pAddress.getText().toString();
                    seekbarAge = seekBarValue.getText().toString();
                    gender = ((RadioButton) findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                    birthday = dateView.getText().toString();
                    checkupDate = checkupdateView.getText().toString();
                    Snackbar.make(view, "Patient Details filled up.", Snackbar.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(PatientAddInfo.this, ClassifierCaptured.class);
                            intent.putExtra("name", name);
                            intent.putExtra("address", address);
                            intent.putExtra("age", seekbarAge);
                            intent.putExtra("gender", gender);
                            intent.putExtra("birthday", birthday);
                            intent.putExtra("checkupDate", checkupDate);
                            startActivity(intent);
                            PatientAddInfo.this.finish();
                        }
                    }, TIME_OUT);
                }
            }
        });


    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {

            return new DatePickerDialog(this,R.style.DatePicker, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                    Toast.makeText(getApplicationContext(), "Birthday has been set.", Toast.LENGTH_SHORT).show();
                    year1 = dateView2.getText().toString();
                    year2 = checkupdateView2.getText().toString();
                    yearBirth = Integer.parseInt(year1);
                    yearCurrent = Integer.parseInt(year2);
                    ageSum = yearCurrent - yearBirth;
                    seekBarValue.setText(String.valueOf(ageSum)+" years old");
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append(" / ")
                .append(month).append(" / ").append(year));
        dateView2.setText(new StringBuilder().append(year));


    }

    @SuppressLint("SetTextI18n")
    private void showCheckupDate(int year, int month, int day) {
        checkupdateView.setText(new StringBuilder().append(day).append(" / ")
                .append(month).append(" / ").append(year));
        checkupdateView2.setText(new StringBuilder().append(year));
    }

    private void seekbarData(){
        seekBarAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                seekBarValue.setText(String.valueOf(progress) + " years old");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!pName.getText().toString().isEmpty() || !pAddress.getText().toString().isEmpty() ||  !seekBarValue.getText().toString().isEmpty() || genderMale.isChecked() || genderFemale.isChecked() || !dateView.getText().toString().isEmpty()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure, you want to discard all patient data that you have entered?");
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent back = new Intent(PatientAddInfo.this,Home.class);
                            startActivity(back);
                            PatientAddInfo.this.finish();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(!pName.getText().toString().isEmpty() || !pAddress.getText().toString().isEmpty() ||  !seekBarValue.getText().toString().isEmpty() || genderMale.isChecked() || genderFemale.isChecked() || !dateView.getText().toString().isEmpty()) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Are you sure, you want to discard all patient data that you have entered?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent back = new Intent(PatientAddInfo.this,Home.class);
                                startActivity(back);
                                PatientAddInfo.this.finish();
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
        if (keyCode == KeyEvent.KEYCODE_HOME)
        {
            PatientAddInfo.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
                Intent back = new Intent(PatientAddInfo.this,Home.class);
                startActivity(back);
                PatientAddInfo.this.finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }


    public void back(View view){
        if(!pName.getText().toString().isEmpty() || !pAddress.getText().toString().isEmpty() ||  !seekBarValue.getText().toString().isEmpty() || genderMale.isChecked() || genderFemale.isChecked() || !dateView.getText().toString().isEmpty()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure, you want to discard all patient data that you have entered?");
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent back = new Intent(PatientAddInfo.this,Home.class);
                            startActivity(back);
                            PatientAddInfo.this.finish();
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
}

