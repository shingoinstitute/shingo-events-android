package org.shingo.shingoeventsapp.data.events;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
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
public class EventsListAdapter extends BaseAdapter {

    private List<Events.Event> data;
    private Context context;
    private static LayoutInflater inflater;

    public EventsListAdapter(Context context, List<Events.Event> data){
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
        img.setVisibility(View.GONE);
        TextView name = (TextView)row.findViewById(R.id.list_name);
        TextView detail = (TextView)row.findViewById(R.id.list_title);
        Events.Event item = (Events.Event) getItem(position);
        name.setText(item.name);
        name.setSingleLine(true);
        name.setEllipsize(TextUtils.TruncateAt.END);
        detail.setText(item.startDate + " - " + item.endDate);

//        row.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN)
//                    v.setBackgroundColor(context.getResources().getColor(R.color.colorTransAccent));
//                else if (event.getAction() == MotionEvent.ACTION_UP)
//                    v.setBackgroundColor(Color.TRANSPARENT);
//
//                return false;
//            }
//        });

        return row;
    }
}
