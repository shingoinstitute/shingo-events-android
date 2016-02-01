package org.shingo.shingoeventsapp.data.speakers;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.shingo.shingoeventsapp.R;

import java.util.List;

/**
 * Created by dustinehoman on 2/1/16.
 */
public class SpeakersListAdapter extends BaseAdapter {

    private List<Speakers.Speaker> data;
    private Context context;
    private static LayoutInflater inflater;

    public SpeakersListAdapter(Context context, List<Speakers.Speaker> data){
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
        TextView title = (TextView)row.findViewById(R.id.list_title);
        Speakers.Speaker item = (Speakers.Speaker) getItem(position);
        img.setImageDrawable(new BitmapDrawable(context.getResources(), item.getRoundPicture()));
        name.setText(item.displayName);
        title.setText(item.title);

        return row;
    }
}
