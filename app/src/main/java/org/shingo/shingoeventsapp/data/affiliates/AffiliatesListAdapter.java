package org.shingo.shingoeventsapp.data.affiliates;

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
 * Created by dustinehoman on 2/2/16.
 */
public class AffiliatesListAdapter extends BaseAdapter {

    private List<Affiliates.Affiliate> data;
    private Context context;
    private static LayoutInflater inflater;

    public AffiliatesListAdapter(Context context, List<Affiliates.Affiliate> data){
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
        Affiliates.Affiliate item = (Affiliates.Affiliate) getItem(position);
        img.setImageDrawable(new BitmapDrawable(context.getResources(), item.logo));
        name.setText(item.name);
        title.setText(item.website);

        row.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    v.setBackgroundColor(context.getResources().getColor(R.color.colorTransAccent));
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    v.setBackgroundColor(Color.TRANSPARENT);

                return false;
            }
        });

        return row;
    }
}
