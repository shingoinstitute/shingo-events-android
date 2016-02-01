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
import org.shingo.shingoeventsapp.data.agendas.Agendas;
import org.shingo.shingoeventsapp.data.speakers.Speakers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by dustinehoman on 2/1/16.
 */
public class SessionsListAdapter extends BaseAdapter {

    private List<Agendas.Day.Session> data;
    private Context context;
    private static LayoutInflater inflater;

    public SessionsListAdapter(Context context, List<Agendas.Day.Session> data){
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
        View row = inflater.inflate(R.layout.agenda_list_row, parent, false);
        TextView top = (TextView)row.findViewById(R.id.top_row);
        TextView bottom = (TextView)row.findViewById(R.id.bottom_row);
        Agendas.Day.Session item = (Agendas.Day.Session) getItem(position);

        DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        String start = null;
        String end = null;
        try {
            start = format.format(formatter.parse(item.startTime).getTime());
            end = format.format(formatter.parse(item.endTime).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        top.setText(start + " - " + end);
        bottom.setText(item.name);

        return row;
    }
}
