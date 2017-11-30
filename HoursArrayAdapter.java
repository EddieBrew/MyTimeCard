package com.apps.robertbrewer.stanfordtimecard;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Robert Brewer on 11/15/2017.
 */

public class HoursArrayAdapter extends ArrayAdapter<DailyInfoModel> {


    private static  class DailyPayViewHolder {
        DailyPayViewHolder(){}
        ImageView imgIconH;
        TextView textViewDateH;
        TextView textViewEventNumberH;
        TextView textViewEventNameH;
        TextView textViewTimeH;
        TextView textViewBHourH;
        TextView textViewExtraHourH;

    }

    private final List<DailyInfoModel> objects;
    private final Integer[] pics;
    private final Activity context;

    public HoursArrayAdapter(Activity context, List<DailyInfoModel> objects, Integer[] pics) {
        super(context, R.layout.row_itemlist, objects);
       // res = resourceLocal;
        this.context = context;
        this.pics = pics;
        this.objects = objects;

    }



    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        // Get the data item for this position
        DailyInfoModel user = getItem(position);

         // Check if an existing view is being reused, otherwise inflate the view
        DailyPayViewHolder holder; // view lookup cache stored in tag

        if(convertView == null)
        {
            // If there's no view to re-use, inflate a brand new view for row
            holder = new DailyPayViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_itemlist, parent, false);

            holder.imgIconH = (ImageView)convertView.findViewById(R.id.imageView);
            holder.textViewDateH = (TextView)convertView.findViewById(R.id.textViewDate);
            holder.textViewEventNameH = (TextView)convertView.findViewById(R.id.textViewEventName);
            holder.textViewEventNumberH = (TextView)convertView.findViewById(R.id.textViewEventNumber);
            holder.textViewTimeH = (TextView)convertView.findViewById(R.id.textViewTime);
            holder.textViewBHourH = (TextView)convertView.findViewById(R.id.textViewrHours);//regular hours
            holder.textViewExtraHourH = (TextView)convertView.findViewById(R.id.textViewoHours);//overtime hours
            convertView.setTag(holder);
        }
        else
        {
            holder = (DailyPayViewHolder) convertView.getTag();
        }

        // Populate the data from the data object via the holder object
        // into the template view (row_itemlist.xml layout).
        holder.imgIconH.setImageResource(pics[getImageInt(user.getDay())]);
        holder.textViewDateH.setText(user.getDate());
        holder.textViewEventNumberH.setText( "Event Number: " + user.getEventNumber());
        holder.textViewEventNameH.setText("Event Name: " + user.getEventName());
        holder.textViewTimeH.setText("Hours: " + user.getTime());
        holder.textViewBHourH.setText( "Regular Hours = " + String.format("%.2f", user.getRhours()));
        holder.textViewExtraHourH.setText("OT Hours = " + String.format("%.2f", user.getOhours()));
        // Return the completed view to render on screen
        return convertView;
    }

    public int getImageInt(String day){

        if(day.equals("SUN")) return 0 ;
        if(day.equals("MON")) return 1 ;
        if(day.equals("TUE")) return 2 ;
        if(day.equals("WED")) return 3 ;
        if(day.equals("THU")) return 4 ;
        if(day.equals("FRI")) return 5 ;
        if(day.equals("SAT")) return 6 ;

            return 6;
    }

}
