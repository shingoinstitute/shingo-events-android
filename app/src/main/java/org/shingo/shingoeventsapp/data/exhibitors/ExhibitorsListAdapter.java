package org.shingo.shingoeventsapp.data.exhibitors;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.speakers.Speakers;

import java.util.List;

/**
 * Created by dustinehoman on 2/1/16.
 */
public class ExhibitorsListAdapter extends BaseAdapter {

    private List<Exhibitors.Exhibitor> data;
    private Context context;
    private static LayoutInflater inflater;

    public ExhibitorsListAdapter(Context context, List<Exhibitors.Exhibitor> data){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.img_adapter_row, parent, false);
        ImageView img = (ImageView)row.findViewById(R.id.list_image);
        TextView name = (TextView)row.findViewById(R.id.list_name);
        Exhibitors.Exhibitor item = (Exhibitors.Exhibitor) getItem(position);
        img.setImageDrawable(new BitmapDrawable(context.getResources(), item.logo));
        name.setText(item.name);

        return row;
    }
}
