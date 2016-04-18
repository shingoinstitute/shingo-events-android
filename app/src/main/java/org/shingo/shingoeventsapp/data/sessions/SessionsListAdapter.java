package org.shingo.shingoeventsapp.data.sessions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.speakers.Speakers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

/**
 * @author Dustin Homan
 * A custom list adapter for {@link org.shingo.shingoeventsapp.data.sessions.Sessions.Session}s.
 * Extends {@link BaseAdapter}.
 *
 * @see org.shingo.shingoeventsapp.R.layout#img_adapter_row
 */
public class SessionsListAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private List<Object> data = new ArrayList<>();
    private TreeSet<Integer> sectionHeader = new TreeSet<>();
    private static LayoutInflater inflater;
    private Context context;

    /**
     * The constructor for the list adapter.
     * @param context the context of the Activity the {@link android.widget.ListView} belongs to
     */
    public SessionsListAdapter(Context context){
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

//    /**
//     * Adds a single item to {@link SessionsListAdapter#data}
//     * @param item expecting {@link org.shingo.shingoeventsapp.data.sessions.Sessions.Session} or {@link String}
//     */
//    public void addItem(Object item){
//        data.add(item);
//        notifyDataSetChanged();
//    }

    /**
     * Adds all items in a {@link List} to {@link SessionsListAdapter#data}
     * @param items expecting a {@link List} of {@link org.shingo.shingoeventsapp.data.sessions.Sessions.Session}
     */
    public void addAllItems(List items){
        data.addAll(items);
    }

    /**
     * Adds a section header to {@link SessionsListAdapter#data}
     *
     * @param item Header name
     */
    public void addSectionHeaderItem(String item){
        data.add(item);
        sectionHeader.add(data.size() - 1);
        notifyDataSetChanged();
    }

    /**
     * Returns whether the position at {@link SessionsListAdapter#data}
     * @param position index into {@link SessionsListAdapter#data}
     * @return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
     */
    @Override
    public int getItemViewType(int position){
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    /**
     * Returns the type count
     * @return 2
     */
    @Override
    public int getViewTypeCount(){
        return 2;
    }

    /**
     * Get the size of the source data.
     *
     * @return {@link SessionsListAdapter#data}.size()
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     *
     * @param position an index to {@link SessionsListAdapter#data}
     * @return the {@link Object}
     *  at {@link SessionsListAdapter#data}.get(position)
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     *
     * @param position an index to {@link SessionsListAdapter#data}
     * @return position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get view utilizing the holder/recycling method
     * @param position an index to {@link SessionsListAdapter#data}
     * @param convertView a view already initialized, enables view recycling
     * @param parent the containing {@link ViewGroup}
     * @return a {@link View} inflated with {@link org.shingo.shingoeventsapp.R.layout#img_adapter_row}
     *  and populated with info from the item at position in {@link SessionsListAdapter#data}
     */
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object holder = null;
        int rowType = getItemViewType(position);

        if(convertView == null){
            switch (rowType){
                case TYPE_ITEM:
                    holder = new ContentHolder();
                    convertView = inflater.inflate(R.layout.agenda_list_row, parent, false);
                    ((ContentHolder)holder).time = (TextView)convertView.findViewById(R.id.time_row);
                    ((ContentHolder)holder).info = (TextView)convertView.findViewById(R.id.info_row);
                    ((ContentHolder)holder).title = (TextView)convertView.findViewById(R.id.title_row);
                    break;
                case TYPE_SEPARATOR:
                    holder = new HeaderHolder();
                    convertView = inflater.inflate(R.layout.list_header, parent, false);
                    ((HeaderHolder)holder).header = (TextView)convertView.findViewById(R.id.list_header);
                    break;
            }
            assert convertView != null;
            convertView.setTag(holder);
        } else {
            holder = convertView.getTag();
        }

        if(holder instanceof ContentHolder) {
            Sessions.Session item = (Sessions.Session)getItem(position);

            DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
            String start = null;
            String end = null;
            try {
                start = format.format(formatter.parse(item.startTime).getTime());
                end = format.format(formatter.parse(item.endTime).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ((ContentHolder)holder).time.setText(context.getString(R.string.date_range, new Object[]{start, end}));
            if(item.format.equals("Social"))
                ((ContentHolder) holder).info.setText(item.format);
            else if(item.speakers.size() > 0) {
                Speakers.Speaker speaker = item.speakers.get(0);
                ((ContentHolder) holder).info.setText(item.format +
                        ((item.format.equals("") || (speaker.name == null && speaker.title == null && speaker.company == null)) ? "" : ": ") +
                        speaker.name + ((speaker.title == null && speaker.company == null) ? "" : ", ") +
                        speaker.title + (speaker.company == null ? "" : ", ") + speaker.company);
            }
            ((ContentHolder) holder).title.setText(item.name);
            if(item.speakers.size() < 1) ((ContentHolder)holder).info.setVisibility(View.GONE);
        } else {
            assert (holder) != null;
            ((HeaderHolder)holder).header.setText((String) getItem(position));
        }

        return convertView;
    }

    /**
     * Content holder class
     */
    public static class ContentHolder{
        public TextView time;
        public TextView info;
        public TextView title;
    }

    /**
     * Header holder class
     */
    public static class HeaderHolder{
        public TextView header;
    }
}
