package org.shingo.shingoeventsapp.data.affiliates;

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
 * A custom list adapter for {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate}s.
 * Extends {@link BaseAdapter}.
 *
 * @see org.shingo.shingoeventsapp.R.layout#img_adapter_row
 */
public class AffiliatesListAdapter extends BaseAdapter {

    private List<Affiliates.Affiliate> data;
    private Context context;
    private static LayoutInflater inflater;

    /**
     * The constructor for the list adapter.
     * @param context the context of the Activity the {@link android.widget.ListView} belongs to
     * @param data the data to display in the list, {@link Affiliates#AFFILIATES}
     */
    public AffiliatesListAdapter(Context context, List<Affiliates.Affiliate> data){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Get the size of the source data.
     *
     * @return {@link AffiliatesListAdapter#data}.size()
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     *
     * @param position an index to {@link AffiliatesListAdapter#data}
     * @return the {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate}
     *  at {@link AffiliatesListAdapter#data}.get(position)
     */
    @Override
    public Affiliates.Affiliate getItem(int position) {
        return data.get(position);
    }

    /**
     *
     * @param position an index to {@link AffiliatesListAdapter#data}
     * @return position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     *
     * @param position an index to {@link AffiliatesListAdapter#data}
     * @param convertView a view already initialized, enables view recycling
     * @param parent the containing {@link ViewGroup}
     * @return a {@link View} inflated with {@link org.shingo.shingoeventsapp.R.layout#img_adapter_row}
     *  and populated with info from the {@link org.shingo.shingoeventsapp.data.affiliates.Affiliates.Affiliate} at
     *  position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.img_adapter_row, parent, false);
        }

        ImageView img = (ImageView) convertView.findViewById(R.id.list_image);
        TextView name = (TextView) convertView.findViewById(R.id.list_name);
        Affiliates.Affiliate item = getItem(position);
        img.setImageDrawable(new BitmapDrawable(context.getResources(), item.logo));
        name.setText(item.name);

        return convertView;
    }
}
