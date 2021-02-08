package com.genedev.retinalclassifierfull.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.genedev.retinalclassifierfull.R;
import com.genedev.retinalclassifierfull.classes.PatientEntry;

import java.util.ArrayList;

/**
 * Created by Gene on 4/29/2018.
 */

public class ListingAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<PatientEntry> patients;
    public ListingAdapter(Context con, ArrayList<PatientEntry> patientEntries)
    {
        context=con;
        layoutInflater = LayoutInflater.from(context);
        this.patients=patients;
    }
    @Override
    public int getCount() {
        return patients.size();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_listing, null, false);
            holder = new ViewHolder();
            holder.fullname = (TextView) convertView.findViewById(R.id.fullname);
            holder.email = (TextView) convertView.findViewById(R.id.diagnosis);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PatientEntry user=patients.get(position);
        holder.fullname.setText(user.getName()+user.getDiagnosis());
        return convertView;
    }
    public class ViewHolder {
        TextView fullname, email;
    }
    @Override
    public Object getItem(int position) {
        return patients.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
}