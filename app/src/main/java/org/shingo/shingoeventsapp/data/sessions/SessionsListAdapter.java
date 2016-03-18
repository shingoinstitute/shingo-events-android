package org.shingo.shingoeventsapp.data.sessions;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.recipients.Recipients;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by dustinehoman on 2/1/16.
 */
public class SessionsListAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private List<Object> data = new ArrayList<>();
    private TreeSet<Integer> sectionHeader = new TreeSet<>();
    private Context context;
    private static LayoutInflater inflater;

    public SessionsListAdapter(Context context){
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(Object item){
        data.add(item);
        notifyDataSetChanged();
    }

    public void addAllItems(List items){
        data.addAll(items);
    }

    public void addSectionHeaderItem(String item){
        data.add(item);
        sectionHeader.add(data.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position){
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount(){
        return 2;
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
        Object holder = null;
        int rowType = getItemViewType(position);

        if(convertView == null){
            switch (rowType){
                case TYPE_ITEM:
                    holder = new ContentHolder();
                    convertView = inflater.inflate(R.layout.agenda_list_row, parent, false);
                    ((ContentHolder)holder).time = (TextView)convertView.findViewById(R.id.time_row);
                    ((ContentHolder)holder).info = (TextView)convertView.findViewById(R.id.info_row);
                    break;
                case TYPE_SEPARATOR:
                    holder = new HeaderHolder();
                    convertView = inflater.inflate(R.layout.list_header, parent, false);
                    ((HeaderHolder)holder).header = (TextView)convertView.findViewById(R.id.list_header);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = convertView.getTag();
        }

        if(holder instanceof ContentHolder) {
            Sessions.Session item = (Sessions.Session)getItem(position);

            DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            String start = null;
            String end = null;
            try {
                start = format.format(formatter.parse(item.startTime).getTime());
                end = format.format(formatter.parse(item.endTime).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ((ContentHolder)holder).time.setText(start + " -- " + end);
            ((ContentHolder)holder).info.setText(item.name);


        } else {
            ((HeaderHolder)holder).header.setText((String) getItem(position));
        }

        return convertView;
    }

    public static class ContentHolder{
        public TextView time;
        public TextView info;
    }

    public static class HeaderHolder{
        public TextView header;
    }
}
