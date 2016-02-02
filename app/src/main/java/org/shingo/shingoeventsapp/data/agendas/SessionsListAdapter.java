package org.shingo.shingoeventsapp.data.agendas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.agendas.Agendas;
import org.shingo.shingoeventsapp.data.sessions.Sessions;
import org.shingo.shingoeventsapp.data.speakers.Speakers;
import org.shingo.shingoeventsapp.ui.AgendaListActivity;
import org.shingo.shingoeventsapp.ui.SessionListActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by dustinehoman on 2/1/16.
 */
public class SessionsListAdapter extends BaseAdapter {

    private List<Sessions.Session> data;
    private Context context;
    private static LayoutInflater inflater;

    public SessionsListAdapter(Context context, List<Sessions.Session> data){
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
        Sessions.Session item = (Sessions.Session) getItem(position);

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

        top.setText(start + " - " + end);
        bottom.setText(item.name);

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
