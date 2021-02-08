package com.genedev.retinalclassifierfull.Fragments.PatientDetailsTab;


import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.Util.TypeFaceUtil;

import java.util.List;

/**
 * Created by Gene on 6/25/2018.
 */

public class PatientsFeaturesAdapter extends ArrayAdapter<Features> {
    private static final String LOG_TAG = PatientsFeaturesAdapter.class.getSimpleName();

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param features A List of AndroidFlavor objects to display in a list
     */
    public PatientsFeaturesAdapter(Activity context, List<Features> features ) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, features );
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        Features features = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_model_patients, parent, false);
        }
        TypeFaceUtil.overrideFont(getContext(),"SERIF", "fonts/Product Sans Regular.ttf");
        ImageView iconView = (ImageView) convertView.findViewById(R.id.listview_img);
        iconView.setImageResource(features.image);

        TextView headlineView = (TextView) convertView.findViewById(R.id.headline_intents);
        headlineView.setText(features.versionName);

        TextView descView = (TextView) convertView.findViewById(R.id.subtitle_intents);
        descView.setText(features.versionNumber);

        Typeface productFontRegular = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Product Sans Regular.ttf");
        Typeface productFontBold = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Product Sans Bold.ttf");
        headlineView.setTypeface(productFontBold);
        descView.setTypeface(productFontRegular);

        return convertView;
    }
}