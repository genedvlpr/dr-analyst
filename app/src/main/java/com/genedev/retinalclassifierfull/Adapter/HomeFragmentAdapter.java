package com.genedev.retinalclassifierfull.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.genedev.retinalclassifierfull.Fragments.AccountFragment;
import com.genedev.retinalclassifierfull.Fragments.HomeTab.HomeFragment;
import com.genedev.retinalclassifierfull.Fragments.PatientDetailsTab.PatientListsFragment;

/**
 * Created by Gene on 4/12/2018.
 */

public class HomeFragmentAdapter extends FragmentPagerAdapter {

    public HomeFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new PatientListsFragment();
            case 2:
                return new AccountFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            //
            //Your tab titles
            //
            case 0:
                return "Home";
            case 1:
                return "Patients";
            case 2:
                return "Account";
            default:
                return null;
        }
    }
}