package org.shingo.shingoeventsapp.data.exhibitors;

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
 * @author Dustin Homan
 * A custom list adapter for {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor}s.
 * Extends {@link BaseAdapter}.
 *
 * @see org.shingo.shingoeventsapp.R.layout#img_adapter_row
 */
public class ExhibitorsListAdapter extends BaseAdapter {

    private List<Exhibitors.Exhibitor> data;
    private Context context;
    private static LayoutInflater inflater;

    /**
     * The constructor for the list adapter.
     * @param context the context of the Activity the {@link android.widget.ListView} belongs to
     * @param data the data to display in the list, {@link Exhibitors#EXHIBITORS}
     */
    public ExhibitorsListAdapter(Context context, List<Exhibitors.Exhibitor> data){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Get the size of the source data.
     *
     * @return {@link ExhibitorsListAdapter#data}.size()
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     *
     * @param position an index to {@link ExhibitorsListAdapter#data}
     * @return the {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor}
     *  at {@link ExhibitorsListAdapter#data}.get(position)
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     *
     * @param position an index to {@link ExhibitorsListAdapter#data}
     * @return position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get view utilizing the holder/recycling method
     * @param position an index to {@link ExhibitorsListAdapter#data}
     * @param convertView a view already initialized, enables view recycling
     * @param parent the containing {@link ViewGroup}
     * @return a {@link View} inflated with {@link org.shingo.shingoeventsapp.R.layout#img_adapter_row}
     *  and populated with info from the {@link org.shingo.shingoeventsapp.data.exhibitors.Exhibitors.Exhibitor} at
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
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }
        Exhibitors.Exhibitor item = (Exhibitors.Exhibitor) getItem(position);
        holder.img.setImageDrawable(new BitmapDrawable(context.getResources(), item.logo));
        holder.name.setText(item.name);

        return convertView;
    }

    /**
     * Used to hold views for recycling
     */
    private static class Holder {
        ImageView img;
        TextView name;
    }
}
