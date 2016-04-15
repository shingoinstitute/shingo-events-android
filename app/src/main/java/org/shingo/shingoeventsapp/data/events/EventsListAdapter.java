package org.shingo.shingoeventsapp.data.events;

import android.content.Context;
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
 * @author Dustin Homan
 * A custom list adapter for {@link org.shingo.shingoeventsapp.data.events.Events.Event}s.
 * Extends {@link BaseAdapter}.
 *
 * @see org.shingo.shingoeventsapp.R.layout#img_adapter_row
 */
public class EventsListAdapter extends BaseAdapter {

    private List<Events.Event> data;
    private Context context;
    private static LayoutInflater inflater;

    public EventsListAdapter(Context context, List<Events.Event> data){
        this.data = data;
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Get the size of the source data.
     *
     * @return {@link EventsListAdapter#data}.size()
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     *
     * @param position an index to {@link EventsListAdapter#data}
     * @return the {@link org.shingo.shingoeventsapp.data.events.Events.Event}
     *  at {@link EventsListAdapter#data}.get(position)
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     *
     * @param position an index to {@link EventsListAdapter#data}
     * @return position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get view utilizing the holder/recycling method
     * @param position an index to {@link EventsListAdapter#data}
     * @param convertView a view already initialized, enables view recycling
     * @param parent the containing {@link ViewGroup}
     * @return a {@link View} inflated with {@link org.shingo.shingoeventsapp.R.layout#img_adapter_row}
     *  and populated with info from the {@link org.shingo.shingoeventsapp.data.events.Events.Event} at
     *  position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.img_adapter_row, parent, false);
            holder = new Holder();
            holder.img = (ImageView)convertView.findViewById(R.id.list_image);
            holder.name = (TextView)convertView.findViewById(R.id.list_name);
            holder.detail = (TextView)convertView.findViewById(R.id.list_title);
            convertView.setTag(holder);
        } else {
            holder =(Holder) convertView.getTag();
        }

        Events.Event item = (Events.Event) getItem(position);
        holder.img.setVisibility(View.GONE);
        holder.name.setText(item.name);
        holder.name.setSingleLine(true);
        holder.name.setEllipsize(TextUtils.TruncateAt.END);
        holder.detail.setText(context.getString(R.string.date_range, new Object[]{item.startDate, item.endDate}));

        return convertView;
    }

    /**
     * Used to hold views for recycling
     */
    private static class Holder{
        ImageView img;
        TextView name;
        TextView detail;
    }
}
