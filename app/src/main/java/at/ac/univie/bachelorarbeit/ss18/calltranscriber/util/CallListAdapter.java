package at.ac.univie.bachelorarbeit.ss18.calltranscriber.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import at.ac.univie.bachelorarbeit.ss18.calltranscriber.R;
import at.ac.univie.bachelorarbeit.ss18.calltranscriber.model.CallInfo;

/**
 * This class is needed to populate the list with all calls in MainActivity.
 */
public class CallListAdapter extends ArrayAdapter<CallInfo> {

    /**
     * Sets the layout to the one specified in res/layout/custom_row.
     * @param context
     * @param objects
     */
    public CallListAdapter(@NonNull Context context, @NonNull ArrayList<CallInfo> objects) {
        super(context, R.layout.custom_row, objects);
    }

    /**
     * Populates the layout specified in res/layout/custom_row with the information of the call.
     * In particular with the name and number of the called person and the date and time of the call.
     * @param position Position in the list.
     * @param convertView The converted view.
     * @param parent The parent view.
     * @return The converted view.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String name = getItem(getCount() - position - 1).getName();
        String number = getItem(getCount() - position - 1).getNumber();
        String date = getItem(getCount() - position - 1).getDate();
        String time = getItem(getCount() - position - 1).getTime();

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        convertView = layoutInflater.inflate(R.layout.custom_row, parent, false);

        TextView textViewName = (TextView) convertView.findViewById(R.id.custom_row_name);
        TextView textViewNumber = (TextView) convertView.findViewById(R.id.custom_row_number);
        TextView textViewDateTime = (TextView) convertView.findViewById(R.id.custom_row_datetime);

        textViewName.setText(name);
        textViewNumber.setText(number);
        textViewDateTime.setText(date + " - " + time);

        return convertView;
    }
}



