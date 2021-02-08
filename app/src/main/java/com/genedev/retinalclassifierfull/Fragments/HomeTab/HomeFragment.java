package com.genedev.retinalclassifierfull.Fragments.HomeTab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.genedev.retinalclassifierfull.Activities.AccountDetails;
import com.genedev.retinalclassifierfull.Activities.AllPatientsContacts;
import com.genedev.retinalclassifierfull.Activities.ClassificationModes;
import com.genedev.retinalclassifierfull.Activities.PatientRecords;
import com.genedev.retinalclassifierfull.Activities.Reports;
import com.genedev.retinalclassifierfull.AssistantHelpers.Assistant;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class HomeFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private LottieAnimationView lottieAnimationView;
    private LottieAnimationView lottieAnimationView1,lottieAnimationView2,lottieAnimationView3,lottieAnimationView4;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private HomeAdapter homeAdapter;

    private ChangeLogAdapter changeLogAdapter;

    Features [] features = {
            new Features(
                    "Analyze Retinal Image",
                    "Analyze taken or imported retinal image that will be diagnosed based on the stages of diabetic retinopathy.",
                    R.mipmap.ic_analyze),
            new Features(
                    "Patient Records",
                    "View your patient records, send it via email or contact the patient for another analysis.",
                    R.mipmap.ic_patients),
            new Features(
                    "Profile",
                    "Access, view and edit your account credentials such as email clinic/hospital name and address and your name.",
                    R.mipmap.ic_account),
            new Features(
                    "Assistant",
                    "If you need solutions about patient\\'s diagnosis, you can ask your health care assistant.",
                    R.mipmap.ic_assistant),
            new Features(
                    "Patient Contacts",
                    "Contact your patient, send an sms if he/she needs to have another checkup.",
                    R.mipmap.ic_patient_contacts),
            new Features(
                    "Reports",
                    "Save reports as PDF file for printing.",
                    R.mipmap.ic_reports)};

    ChangeLog [] changeLogs = {
            new ChangeLog("Improved reliability and accuracy with more than 30k image data set."),
            new ChangeLog("Redesigned theme for better experience."),
            new ChangeLog("New classification where in results and diagnosis is based on 4 stages of diabetic retinopathy."),
            new ChangeLog("New decision making support with machine learning built in by the use of your health care assistant."),
            new ChangeLog("Let's you access more information about the patient."),
            new ChangeLog("You can now send the diagnosis/report through email by exporting it as pdf file."),
            new ChangeLog("More patient details to be filled up.")};

    private  List list;

    private OnFragmentInteractionListener mListener;

    ArrayList<HashMap<String, String>> aList=new ArrayList<HashMap<String,String>>();
    SimpleAdapter adapter;
    public HomeFragment() {

        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        View rootView = inflater.inflate(R.layout.listview_adapter_home, container, false);
        TypeFaceUtil.overrideFont(getContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        changeLogAdapter = new ChangeLogAdapter(getActivity(), Arrays.asList(changeLogs));

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listViewLogs = (ListView) rootView.findViewById(R.id.subtitle_list);
        listViewLogs.setAdapter(changeLogAdapter);

        homeAdapter = new HomeAdapter(getActivity(), Arrays.asList(features));

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.home_listview);
        listView.setAdapter(homeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        Intent patientAdd = new Intent(getActivity(),ClassificationModes.class);
                        startActivity(patientAdd);
                        getActivity().finish();
                        break;
                    case 1:
                        Intent patientRecords = new Intent(getActivity(),PatientRecords.class);
                        startActivity(patientRecords);
                        break;
                    case 2:
                        Intent accDetails = new Intent(getActivity(),AccountDetails.class);
                        startActivity(accDetails);
                        getActivity().finish();
                        break;
                    case 3:
                        Intent assistant = new Intent(getContext(),Assistant.class);
                        startActivity(assistant);
                        break;
                    case 4:
                        Intent contacts = new Intent(getContext(),AllPatientsContacts.class);
                        startActivity(contacts);
                        getActivity().finish();
                        break;
                    case 5:
                        Intent a = new Intent(getContext(),Reports.class);
                        startActivity(a);
                        getActivity().finish();
                        break;
                }
            }

        });


        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        TextView logsHeader = getActivity().findViewById(R.id.home_header);
        Typeface productFontRegular = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Product Sans Regular.ttf");
        Typeface productFontBold = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Product Sans Bold.ttf");
        logsHeader.setTypeface(productFontBold);
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




}
