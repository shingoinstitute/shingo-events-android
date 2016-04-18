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
 * A custom list adapter for {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker}s.
 * Extends {@link BaseAdapter}.
 *
 * @author Dustin Homan
 * @see org.shingo.shingoeventsapp.R.layout#img_adapter_row
 */
public class SpeakersListAdapter extends BaseAdapter {

    private List<Speakers.Speaker> data;
    private Context context;
    private static LayoutInflater inflater;
    private boolean highlight;

    /**
     * The constructor for the list adapter.
     * @param context the context of the Activity the {@link android.widget.ListView} belongs to
     * @param data the data to display in the list, {@link Speakers#SPEAKERS}
     * @param highlight highlight the speaker row?
     */
    public SpeakersListAdapter(Context context, List<Speakers.Speaker> data, boolean highlight){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.highlight = highlight;
    }

    /**
     * Get the size of the source data.
     *
     * @return {@link SpeakersListAdapter#data}.size()
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     *
     * @param position an index to {@link SpeakersListAdapter#data}
     * @return the {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker}
     *  at {@link SpeakersListAdapter#data}.get(position)
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     *
     * @param position an index to {@link SpeakersListAdapter#data}
     * @return position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get view utilizing the holder/recycling method
     * @param position an index to {@link SpeakersListAdapter#data}
     * @param convertView a view already initialized, enables view recycling
     * @param parent the containing {@link ViewGroup}
     * @return a {@link View} inflated with {@link org.shingo.shingoeventsapp.R.layout#img_adapter_row}
     *  and populated with info from the {@link org.shingo.shingoeventsapp.data.speakers.Speakers.Speaker} at
     *  position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.img_adapter_row, parent, false);
            holder = new Holder();
            holder.img = (ImageView) convertView.findViewById(R.id.list_image);
            holder.name = (TextView) convertView.findViewById(R.id.list_name);
            holder.title = (TextView) convertView.findViewById(R.id.list_title);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        Speakers.Speaker item = (Speakers.Speaker) getItem(position);
        holder.img.setImageDrawable(new BitmapDrawable(context.getResources(), item.getRoundPicture(context)));
        holder.name.setText(item.displayName);
        if (!item.company.equals(""))
            holder.title.setText(context.getString(R.string.comma, item.title, item.company));
        else if(!item.title.equals(""))
            holder.title.setText(item.title);
        else if(!item.company.equals(""))
            holder.title.setText(item.company);

        if (highlight) {
            convertView.setClickable(true);
            convertView.setOnTouchListener(new View.OnTouchListener() {
                @SuppressWarnings("deprecation")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        v.setBackgroundColor(context.getResources().getColor(R.color.colorTransAccent));
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                        v.setBackgroundColor(Color.TRANSPARENT);

                    return false;
                }
            });
        } else {
            convertView.setClickable(false);
        }

        return convertView;
    }

    /**
     * Used to hold views for recycling
     */
    private static class Holder {
        ImageView img;
        TextView name;
        TextView title;
    }
}
