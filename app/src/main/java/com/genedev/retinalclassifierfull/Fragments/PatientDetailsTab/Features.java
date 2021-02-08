package com.genedev.retinalclassifierfull.Fragments.PatientDetailsTab;

/**
 * Created by Gene on 6/25/2018.
 */

public class Features {
    String versionName;
    String versionNumber;
    int image; // drawable reference id

    public Features(String vName, String vDesc, int image)
    {
        this.versionName = vName;
        this.versionNumber = vDesc;
        this.image = image;
    }

}