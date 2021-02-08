package com.genedev.retinalclassifierfull.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.genedev.retinalclassifierfull.Activities.Home;
import com.genedev.retinalclassifierfull.classes.HistoryHelper;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.classes.PatientEntry;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PatientFinalInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PatientFinalInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientFinalInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button next;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ProgressDialog progressDialog;

    private DialogInterface progressDialogFinal;
    private TextView pName,pAge,pGender,pAddress,pResults,pClassifyType,pBirthday,pCheckupDate,pContact;

    private static int TIME_OUT = 3000;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    private Calendar calendar;
    private Date currentTime;
    private int year,month,day;
    private String year_date,month_date,day_date,time_date;

    public PatientFinalInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PatientFinalInfoFragment newInstance(String param1, String param2) {
        PatientFinalInfoFragment fragment = new PatientFinalInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_patient_final_info, container, false);

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();

        TypeFaceUtil.overrideFont(getContext(),"SERIF", "fonts/Product Sans Regular.ttf");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Patient Details");
        progressDialog.show();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        pName = getActivity().findViewById(R.id.pName2);
        pAge = getActivity().findViewById(R.id.pAge1);
        pGender = getActivity().findViewById(R.id.pGender1);
        pAddress = getActivity().findViewById(R.id.pAddress1);
        pCheckupDate = getActivity().findViewById(R.id.pCheckUpDate);
        pBirthday = getActivity().findViewById(R.id.pBirthday);
        pResults = getActivity().findViewById(R.id.pResults1);
        pClassifyType = getActivity().findViewById(R.id.pClassifyType);
        pContact = getActivity().findViewById(R.id.pContact);

        pName.setVisibility(View.INVISIBLE);
        pAge.setVisibility(View.INVISIBLE);
        pGender.setVisibility(View.INVISIBLE);
        pBirthday.setVisibility(View.INVISIBLE);
        pCheckupDate.setVisibility(View.INVISIBLE);
        pAddress.setVisibility(View.INVISIBLE);
        pResults.setVisibility(View.INVISIBLE);
        pClassifyType.setVisibility(View.INVISIBLE);
        pContact.setVisibility(View.INVISIBLE);

        pName.setText(getActivity().getIntent().getStringExtra("name"));
        pAddress.setText(getActivity().getIntent().getStringExtra("address"));
        pAge.setText(getActivity().getIntent().getStringExtra("age"));
        pBirthday.setText(getActivity().getIntent().getStringExtra("birthday"));
        pCheckupDate.setText(getActivity().getIntent().getStringExtra("checkupDate"));
        pGender.setText(getActivity().getIntent().getStringExtra("gender"));
        pResults.setText(getActivity().getIntent().getStringExtra("result"));
        pClassifyType.setText(getActivity().getIntent().getStringExtra("classifyType"));
        pContact.setText(getActivity().getIntent().getStringExtra("contact"));

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

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent finishedSaving = new Intent(getActivity(), Home.class);
                        startActivity(finishedSaving);
                        getActivity().finish();
                    }
                }, TIME_OUT);

                final String id = mAuth.getUid();
                final String c_name = getActivity().getIntent().getStringExtra("name");
                final String c_address = getActivity().getIntent().getStringExtra("address");
                final String c_checkupDate = getActivity().getIntent().getStringExtra("checkupDate");
                final String c_age = getActivity().getIntent().getStringExtra("age");
                final String c_gender = getActivity().getIntent().getStringExtra("gender");
                final String c_birthday = getActivity().getIntent().getStringExtra("birthday");
                final String c_results = getActivity().getIntent().getStringExtra("result");
                final String c_type = getActivity().getIntent().getStringExtra("classifyType");
                final String c_contact = getActivity().getIntent().getStringExtra("contact");

                try{
                    writeNewPatient(id,c_name,c_name,c_address,c_age,c_birthday,c_checkupDate,c_gender,c_results,c_type, c_contact);
                    Snackbar.make(view, "Patient data has been uploaded.", Snackbar.LENGTH_LONG).show();
                }
                catch (Exception e){
                    Snackbar.make(getView(),"Check your internet connection",Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            writeNewPatient(id,c_name,c_name,c_address,c_age,c_birthday,c_checkupDate,c_gender,c_results,c_type, c_contact);
                        }
                    }).show();
                }

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
                String action = "Added new patient to the 'Patient List'";

                writeToHistory(id,action,date,time);
            }
        });
    }
    private void writeToHistory(String id,String action, String date, String time) {

        HistoryHelper historyHelper = new HistoryHelper(action,date,time);

        mDatabase.child("users").child(id).child("history").child(time).setValue(historyHelper);
    }

    private void writeNewPatient(String userId,String pname,String a_name, String a_address,String a_age,String a_birthday,String a_checkupDate, String a_gender,String a_diagnosis,String a_type, String a_contact) {

        PatientEntry patientEntry = new PatientEntry(a_name,a_address,a_age,a_birthday,a_checkupDate,a_gender,a_diagnosis,a_type,a_contact);

        mDatabase.child("users").child(userId).child("patients").child(pname).setValue(patientEntry);
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
