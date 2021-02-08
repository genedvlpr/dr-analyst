package com.genedev.retinalclassifierfull.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.genedev.retinalclassifierfull.classes.AccountEntry;
import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DoctorsInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DoctorsInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoctorsInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button next;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private AccountEntry accountEntry;
    private EditText doctorsName,clinicsName,clinicsAddress;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    public DoctorsInfoFragment() {
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
    public static DoctorsInfoFragment newInstance(String param1, String param2) {
        DoctorsInfoFragment fragment = new DoctorsInfoFragment();
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
        View v = inflater.inflate(R.layout.fragment_doctors_info, container, false);

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        TypeFaceUtil.overrideFont(getContext(),"SERIF", "fonts/Product Sans Regular.ttf");

        doctorsName = getActivity().findViewById(R.id.doctors_name);
        clinicsName = getActivity().findViewById(R.id.clinics_name);
        clinicsAddress = getActivity().findViewById(R.id.clinics_address);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();
        databaseReference.child("users").child(userId).child("accountDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                        AccountEntry accountEntry = dataSnapshot.getValue(AccountEntry.class);
                        String doctorsName1 = accountEntry.getName();
                        String clinicsName1 = accountEntry.getHostname();
                        String clinicsAddress1 = accountEntry.getHostadd();

                        doctorsName.setText(doctorsName1);
                        clinicsName.setText(clinicsName1);
                        clinicsAddress.setText(clinicsAddress1);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
