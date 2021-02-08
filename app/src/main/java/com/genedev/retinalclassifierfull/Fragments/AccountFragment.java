package com.genedev.retinalclassifierfull.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.genedev.retinalclassifierfull.Activities.AccountDetails;
import com.genedev.retinalclassifierfull.Activities.EditAccount;
import com.genedev.retinalclassifierfull.Activities.History;
import com.genedev.retinalclassifierfull.Activities.Login;
import com.genedev.retinalclassifierfull.classes.HistoryHelper;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button next;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView header_headline, header_subtitle, logout_header,logout_subtitle,editAcc_headline,editAcc_subtitle, editAcc_warn,
                     recentction_headline,recentAction_subtitle,powered_headline, powered_subtitle;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Calendar calendar;
    private Date currentTime;
    private int year,month,day;
    private String year_date,month_date,day_date,time_date;
    public AccountFragment() {
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
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        TypeFaceUtil.overrideFont(getContext(),"SERIF", "fonts/Product Sans Regular.ttf");

        header_headline = getActivity().findViewById(R.id.home_header);
        header_subtitle = getActivity().findViewById(R.id.subtitle);
        logout_header = getActivity().findViewById(R.id.headlineLogout);
        logout_subtitle = getActivity().findViewById(R.id.subtitleLogout);
        editAcc_headline = getActivity().findViewById(R.id.editAcc_headline);
        editAcc_subtitle = getActivity().findViewById(R.id.editAcc_sub);
        editAcc_warn = getActivity().findViewById(R.id.subtitle2);
        recentction_headline = getActivity().findViewById(R.id.headline_intents2);
        recentAction_subtitle = getActivity().findViewById(R.id.subtitle_intents2);
        powered_headline = getActivity().findViewById(R.id.headline_intents3);
        powered_subtitle = getActivity().findViewById(R.id.subtitle_intents3);

        CardView logout = getActivity().findViewById(R.id.crd2);
        RelativeLayout logoutRel = getActivity().findViewById(R.id.relLogout);
        CardView editAccount = getActivity().findViewById(R.id.crd3);
        CardView history = getActivity().findViewById(R.id.crd4);
        Button viewAcc = getActivity().findViewById(R.id.view);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Typeface productFontRegular = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Product Sans Regular.ttf");
        Typeface productFontBold = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Product Sans Bold.ttf");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        header_headline.setTypeface(productFontBold);
        header_subtitle.setTypeface(productFontRegular);

        logout_header.setTypeface(productFontBold);
        logout_subtitle.setTypeface(productFontRegular);

        editAcc_headline.setTypeface(productFontBold);
        editAcc_subtitle.setTypeface(productFontRegular);
        editAcc_warn.setTypeface(productFontRegular);

        recentction_headline.setTypeface(productFontBold);
        recentAction_subtitle.setTypeface(productFontRegular);

        powered_headline.setTypeface(productFontBold);
        powered_subtitle.setTypeface(productFontRegular);

        logoutRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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
                String action = "Logged Out";

                String id = mAuth.getUid();

                writeToHistory(id,action,date,time);

                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent i = new Intent(getActivity(), Login.class);
                startActivity(i);
                getActivity().finish();
                Toast.makeText(getActivity(),"You've just signed out.",Toast.LENGTH_SHORT).show();
            }
        });

        editAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditAccount.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), History.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        viewAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountDetails.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
    private void writeToHistory(String id,String action, String date, String time) {

        HistoryHelper historyHelper = new HistoryHelper(action,date,time);

        mDatabase.child("users").child(id).child("history").child(time).setValue(historyHelper);
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
