package org.shingo.shingoeventsapp.data.speakers;

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

import java.util.List;

/**
 * Created by dustinehoman on 2/1/16.
 */
public class SpeakersListAdapter extends BaseAdapter {

    private List<Speakers.Speaker> data;
    private Context context;
    private static LayoutInflater inflater;
    private boolean highlight;

    public SpeakersListAdapter(Context context, List<Speakers.Speaker> data, boolean highlight){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.highlight = highlight;
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
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.img_adapter_row, parent, false);
        }

        ImageView img = (ImageView) convertView.findViewById(R.id.list_image);
        TextView name = (TextView) convertView.findViewById(R.id.list_name);
        TextView title = (TextView) convertView.findViewById(R.id.list_title);
        Speakers.Speaker item = (Speakers.Speaker) getItem(position);
        img.setImageDrawable(new BitmapDrawable(context.getResources(), item.getRoundPicture()));
        name.setText(item.displayName);
        if (!item.company.equals(""))
            title.setText(item.title + ", " + item.company);
        else if(!item.title.equals(""))
            title.setText(item.title);
        else if(!item.company.equals(""))
            title.setText(item.company);

        if (highlight) {
            convertView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        v.setBackgroundColor(context.getResources().getColor(R.color.colorTransAccent));
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                        v.setBackgroundColor(Color.TRANSPARENT);

                    return false;
                }
            });
        }

        return convertView;
    }
}
