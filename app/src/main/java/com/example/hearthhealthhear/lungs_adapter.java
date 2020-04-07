package com.example.hearthhealthhear;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class lungs_adapter extends ArrayAdapter<recorded_file> {
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     */
    public lungs_adapter(@NonNull Context context, int resource, List<recorded_file> objects) {
        super(context, resource,objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.lungs_listview, parent, false);
        }

        recorded_file prop = getItem(position);
        String name = prop.getFile_name();

        TextView filename = (TextView) convertView.findViewById(R.id.record_file_names_lungs);
        filename.setText(name);





        return convertView;
    }
}
