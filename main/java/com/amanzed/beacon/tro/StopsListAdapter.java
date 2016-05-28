package com.amanzed.beacon.tro;

/**
 * Created by Amanze on 5/28/2016.
 */

        import java.util.List;
        import java.util.Random;

        import android.app.Activity;
        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.TextView;

        import com.amanzed.beacon.R;

public class StopsListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Stop> stopItems;

    public StopsListAdapter(Activity activity, List<Stop> stopItems) {
        this.activity = activity;
        this.stopItems = stopItems;
    }

    @Override
    public int getCount() {
        return stopItems.size();
    }

    @Override
    public Object getItem(int location) {
        return stopItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.stops_list_item, null);
        TextView name = (TextView) convertView.findViewById(R.id.nameTV);
        TextView amount = (TextView) convertView.findViewById(R.id.amountTV);

        // getting hymn data for the row
        Stop m = stopItems.get(position);

        Random rand = new Random();
        name.setText(String.valueOf(m.getStop_name()));
        amount.setText(String.valueOf(rand.nextInt((15 - 0) + 1) + 0));

        return convertView;
    }


}