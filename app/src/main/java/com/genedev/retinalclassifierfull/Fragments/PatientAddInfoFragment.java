package com.genedev.retinalclassifierfull.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.genedev.retinalclassifierfull.Activities.PatientFinalInfoNew;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PatientAddInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PatientAddInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientAddInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button next;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private DatePicker datePicker;
    private Calendar calendar,calendar1,calendar2;
    public EditText dateView, contact;
    private int year, month, day;

    private SeekBar seekBarAge;
    private EditText pName,pAddress;
    private RadioButton genderMale,genderFemale;
    TextView seekBarValue,checkupdateView,checkupdateView2,dateView2;
    private RadioGroup radioGroup;
    private String name,address,seekbarAge,gender,birthday,checkupDate,year1,year2, pContact;
    private FloatingActionButton fab;
    String pResults, pClassifyType;
    private static int TIME_OUT = 3000;
    int ageSum,yearBirth,yearCurrent;

    public PatientAddInfoFragment() {
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
    public static PatientAddInfoFragment newInstance(String param1, String param2) {
        PatientAddInfoFragment fragment = new PatientAddInfoFragment();
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
        View v = inflater.inflate(R.layout.fragment_patient_add_info, container, false);

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();

        radioGroup = getActivity().findViewById(R.id.genderRadioGroup);
        seekBarValue = getActivity().findViewById(R.id.pAgeLabel);
        pName = getActivity().findViewById(R.id.pName);
        pAddress = getActivity().findViewById(R.id.pAddress);
        seekBarAge = getActivity().findViewById(R.id.seekBarAge);
        genderMale = getActivity().findViewById(R.id.genderMale);
        genderFemale = getActivity().findViewById(R.id.genderfemale);
        contact = getActivity().findViewById(R.id.pContact);

        dateView = getActivity().findViewById(R.id.tvDate);
        dateView2 = getActivity().findViewById(R.id.tvDate2);
        checkupdateView = getActivity().findViewById(R.id.tvCheckupDate);
        checkupdateView2 = getActivity().findViewById(R.id.tvCheckupDate2);

        pResults = getActivity().getIntent().getStringExtra("result");
        pClassifyType = getActivity().getIntent().getStringExtra("classifyType");

        TypeFaceUtil.overrideFont(getContext(),"SERIF", "fonts/Product Sans Regular.ttf");

        calendar = Calendar.getInstance();

        calendar1 = Calendar.getInstance();
        calendar2 = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showCheckupDate(year, month+1, day);

        fab = getActivity().findViewById(R.id.fab);

        Button setBday = getActivity().findViewById(R.id.select_date);
        setBday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), myDateListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Select the date");
                datePicker.show();

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pName.getText().toString().isEmpty()) {
                    Snackbar.make(view, "All fields must not be empty.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (contact.getText().toString().isEmpty()) {
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
                if(!pName.getText().toString().isEmpty() || !pAddress.getText().toString().isEmpty() || !contact.getText().toString().isEmpty() ||  !seekBarValue.getText().toString().isEmpty() || genderMale.isChecked() || genderFemale.isChecked() || !dateView.getText().toString().isEmpty()) {

                    name = pName.getText().toString();
                    address = pAddress.getText().toString();
                    seekbarAge = seekBarValue.getText().toString();
                    gender = ((RadioButton) getActivity().findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                    birthday = dateView.getText().toString();
                    checkupDate = checkupdateView.getText().toString();
                    pContact = contact.getText().toString();
                    final String results = pResults;
                    final String type = pClassifyType;
                    Snackbar.make(view, "Patient Details filled up.", Snackbar.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getActivity(), PatientFinalInfoNew.class);
                            intent.putExtra("name", name);
                            intent.putExtra("address", address);
                            intent.putExtra("age", seekbarAge);
                            intent.putExtra("gender", gender);
                            intent.putExtra("birthday", birthday);
                            intent.putExtra("checkupDate", checkupDate);
                            intent.putExtra("result", results);
                            intent.putExtra("classifyType", type);
                            intent.putExtra("contact", pContact);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }, TIME_OUT);
                }
            }
        });

    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                    Toast.makeText(getContext(), "Birthday has been set.", Toast.LENGTH_SHORT).show();
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
