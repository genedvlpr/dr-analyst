package com.genedev.retinalclassifierfull.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.genedev.retinalclassifierfull.classes.PatientEntry;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Gene on 4/29/2018.
 */

public class AllPatientsContacts extends Activity {

    // Variable declaration.
    private ListView allPatients;
    private ProgressDialog mProgressDialog;

    // Firebase database declaration and initialization.
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private ListingAdapter adapter;
    PatientEntry patientEntry;
    // Show patients data using array from PatientEntry class.
    private ArrayList<PatientEntry> patients = new ArrayList<>();

    // Firebase Auth declaration.
    private FirebaseAuth mAuth;

    private ImageButton sort;

    private TextView subtitle;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_patients);

        TextView headline = findViewById(R.id.headline);
        headline.setText("PATIENT CONTACTS");
        TextView subhead = findViewById(R.id.subtitle);
        subhead.setText("SELECT THE PATIENT THAT YOU WANT TO CALL OR SEND A TEXT MESSAGE.");

        TypeFaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Product Sans Regular.ttf");

        allPatients = findViewById(R.id.allpatients);
        adapter = new ListingAdapter(AllPatientsContacts.this, patients);
        allPatients.setAdapter(adapter);
        getDataFromServer();
    }

    // Getting the data from Users node, users id and patients at Firebase and then adding the patients in Arraylist and setting it to a Listview.
    public void getDataFromServer() {
        showProgressDialog();
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();
        databaseReference.child("users").child(userId).child("patients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        PatientEntry patientEntry = postSnapShot.getValue(PatientEntry.class);
                        patients.add(patientEntry);
                        adapter.notifyDataSetChanged();
                    }
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    // Show a progress dialog upon loading patient's data.
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(AllPatientsContacts.this);
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

    // Set a list adapter to show list of patient's to a listview.
    private class ListingAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;
        ArrayList<PatientEntry> patients;

        public ListingAdapter(Context con, ArrayList<PatientEntry> patients) {
            context = con;
            layoutInflater = LayoutInflater.from(context);
            this.patients = patients;

        }

        @Override
        public int getCount() {
            return patients.size();
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.content_patient_list_new, null, false);
                holder = new ViewHolder();
                holder.fullname = (TextView) convertView.findViewById(R.id.pName);
                holder.phone = (TextView) convertView.findViewById(R.id.pAddress);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            patientEntry = patients.get(position);
            holder.fullname.setText(patientEntry.getName());
            holder.phone.setText(patientEntry.getPhone());
            convertView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", patients.get(position).getPhone(), null));
                    startActivity(intent);
                }
            });

            return convertView;
        }

        public class ViewHolder {
            TextView fullname, phone;
        }

        @Override
        public Object getItem(int position) {
            return patients.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private void openDetailActivity(String... details) {


            //context.startActivity(i);
        }
    }
    // Navigation bar key events.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Intent back = new Intent(AllPatientsContacts.this,Home.class);
            startActivity(back);
            AllPatientsContacts.this.finish();
        }
        if ((keyCode == KeyEvent.KEYCODE_HOME))
        {
            AllPatientsContacts.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    public void back(View view){
        Intent back = new Intent(AllPatientsContacts.this,Home.class);
        startActivity(back);
        AllPatientsContacts.this.finish();
    }

    // Info menu contents shown in an alert dialog.
    public void info_db(View view){
        alertDialog();
    }
    private void alertDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_container);

        TextView text = dialog.findViewById(R.id.headline);
        text.setText("You can see here all of your patient's phone number for you to reach them by sending a text message or by calling them.");
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
}
