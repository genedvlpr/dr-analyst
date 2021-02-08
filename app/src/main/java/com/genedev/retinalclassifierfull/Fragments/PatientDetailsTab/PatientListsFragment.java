package com.genedev.retinalclassifierfull.Fragments.PatientDetailsTab;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.genedev.retinalclassifierfull.Activities.AllPatients;
import com.genedev.retinalclassifierfull.Activities.Trash;
import com.genedev.retinalclassifierfull.classes.HistoryHelper;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PatientListsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PatientListsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientListsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button next;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseReference mDatabase;
    final Context context = getActivity();
    private InfoAdapter infoAdapter;
    private PatientsFeaturesAdapter patientsFeaturesAdapter;
    private FirebaseAuth mAuth;
    Info [] info = {
            new Info("You can view your patient details and information as well as their diagnosis. " +
                    "You can now view their contact details for you to send to them their diagnosis by exporting it as PDF, " +
                    "or contact them for another consultation and checkup."),
            new Info("Your patient details is secured and cannot be editable, this lets you " +
                    "access and retrieve your patients information in a way that it is secured and reliable."),
            new Info("Be sure that all the patient information's that have been uploaded is true " +
                    "and reliable for you to contact them with their true contact details."),};

    Features [] features = {
            new Features(
                    "View Patient Details",
                    "Retrieve  patient details and information's, also their record.",
                    R.mipmap.ic_view_patients),
            new Features(
                    "Trash",
                    "Patient details that are deleted will go to the trash, retrieve those patient's details for accessing it again.",
                    R.mipmap.ic_delete_patients),
            new Features(
                    "Export",
                    "Export patient records as pdf to send it to the patient and for printing purposes.",
                    R.mipmap.ic_export_reports)};
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private OnFragmentInteractionListener mListener;

    private FirebaseUser user;


    private Calendar calendar;
    private Date currentTime;
    private int year,month,day;
    private String year_date,month_date,day_date,time_date;
    public PatientListsFragment() {
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
    public static PatientListsFragment newInstance(String param1, String param2) {
        PatientListsFragment fragment = new PatientListsFragment();
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
        View v = inflater.inflate(R.layout.listview_adapter_patients, container, false);

        infoAdapter = new InfoAdapter(getActivity(), Arrays.asList(info));
        patientsFeaturesAdapter = new PatientsFeaturesAdapter(getActivity(), Arrays.asList(features));

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listViewInfo = (ListView) v.findViewById(R.id.subtitle_list);
        listViewInfo.setAdapter(infoAdapter);

        ListView listView = (ListView) v.findViewById(R.id.patients_listview);
        listView.setAdapter(patientsFeaturesAdapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        Intent patientDetails = new Intent(getActivity(),AllPatients.class);
                        startActivity(patientDetails);
                        getActivity().finish();
                        break;
                    case 1:
                        Intent trash = new Intent(getActivity(),Trash.class);
                        startActivity(trash);
                        getActivity().finish();
                        break;
                    case 2:
                        Intent export = new Intent(getActivity(),AllPatients.class);
                        startActivity(export);
                        getActivity().finish();
                        break;
                }
            }

        });


        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart(){
       super.onStart();
        TypeFaceUtil.overrideFont(getContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        TextView header = getActivity().findViewById(R.id.patient_list_header);
        TextView headline = getActivity().findViewById(R.id.headline);
        TextView sub = getActivity().findViewById(R.id.subtitle);

        Typeface productFontRegular = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Product Sans Regular.ttf");
        Typeface productFontBold = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Product Sans Bold.ttf");

        header.setTypeface(productFontBold);
        headline.setTypeface(productFontBold);
        sub.setTypeface(productFontRegular);

        RelativeLayout rel3 = getActivity().findViewById(R.id.rel3);
        rel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void alertDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_container);

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.headline);
        text.setText("Are you sure you want to delete all your patient's info? This can't be reverted. Bulk delete doesn't support recovery.");
        ImageView image = (ImageView) dialog.findViewById(R.id.img_warn);
        image.setImageResource(R.drawable.ic_info_outline_white_48dp);


        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_btn);
        dialogButton.setText("Proceed");
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mAuth = FirebaseAuth.getInstance();
                String userId = mAuth.getUid();
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
                String action = "Bulk Delete";

                writeToHistory(userId,action,date,time);

                databaseReference.child("users").child(userId).child("patients").removeValue();
                Snackbar.make(getView(),"All patient inforamtions have been deleted.",Snackbar.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void writeToHistory(String id,String action, String date, String time) {

        HistoryHelper historyHelper = new HistoryHelper(action,date,time);

        mDatabase.child("users").child(id).child("history").child(time).setValue(historyHelper);
    }
}
