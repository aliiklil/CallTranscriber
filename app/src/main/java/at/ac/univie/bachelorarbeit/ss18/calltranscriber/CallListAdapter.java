package at.ac.univie.bachelorarbeit.ss18.calltranscriber;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CallListAdapter extends ArrayAdapter<CallInfo> {

    public CallListAdapter(@NonNull Context context, @NonNull ArrayList<CallInfo> objects) {
        super(context, R.layout.custom_row, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String name = getItem(getCount() - position - 1).getName();
        String number = getItem(getCount() - position - 1).getNumber();
        String dateTime = getItem(getCount() - position - 1).getDateTime();

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        convertView = layoutInflater.inflate(R.layout.custom_row, parent, false);

        TextView textViewName = (TextView) convertView.findViewById(R.id.custom_row_name);
        TextView textViewNumber = (TextView) convertView.findViewById(R.id.custom_row_number);
        TextView textViewDareTime = (TextView) convertView.findViewById(R.id.custom_row_datetime);

        textViewName.setText(name);
        textViewNumber.setText(number);
        textViewDareTime.setText(dateTime);

        return convertView;
    }
}



