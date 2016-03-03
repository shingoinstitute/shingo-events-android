package org.shingo.shingoeventsapp.data.sponsors;

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
import org.shingo.shingoeventsapp.data.recipients.Recipients;
import org.shingo.shingoeventsapp.data.recipients.RecipientsListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by dustinehoman on 2/1/16.
 */
public class SponsorsListAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private List<Object> data = new ArrayList<>();
    private TreeSet<Integer> sectionHeader = new TreeSet<>();
    private Context context;
    private static LayoutInflater inflater;

    public SponsorsListAdapter(Context context){
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
                    convertView = inflater.inflate(R.layout.img_adapter_row, parent, false);
                    ((ContentHolder)holder).image = (ImageView)convertView.findViewById(R.id.list_image);
                    ((ContentHolder)holder).name = (TextView)convertView.findViewById(R.id.list_name);
                    ((ContentHolder)holder).detail = (TextView)convertView.findViewById(R.id.list_title);
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
            Object item = getItem(position);
            ((ContentHolder) holder).image.setImageDrawable(new BitmapDrawable(context.getResources(), ((Sponsors.Sponsor)item).logo));
            ((ContentHolder) holder).name.setText(((Sponsors.Sponsor) item).name);
            ((ContentHolder) holder).detail.setText(((Sponsors.Sponsor) item).level);
        } else {
            ((HeaderHolder)holder).header.setText((String) getItem(position));
        }

        return convertView;
    }

    public static class ContentHolder{
        public ImageView image;
        public TextView name;
        public TextView detail;
    }

    public static class HeaderHolder{
        public TextView header;
    }
}