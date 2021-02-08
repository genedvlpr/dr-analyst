package com.genedev.retinalclassifierfull.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.genedev.retinalclassifierfull.Fragments.DoctorsInfoFragment;
import com.genedev.retinalclassifierfull.Fragments.PatientAddInfoFragment;

/**
 * Created by Gene on 4/12/2018.
 */

public class PatientAddInfoFragmentAdapter extends FragmentPagerAdapter {

    public PatientAddInfoFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PatientAddInfoFragment();
            case 1:
                return new DoctorsInfoFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }


    //@Override
    //public CharSequence getPageTitle(int position) {
      //  switch (position) {
            //
            //Your tab titles
            //
        //    case 0:
                //return "Patient";
          //  case 1:
                //return "Doctor";
            //default:
              //  return null;
       // }
   // }
}