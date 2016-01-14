package org.shingo.shingoeventsapp.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.GetAgendasTask;
import org.shingo.shingoeventsapp.data.agendas.Agendas;

/**
 * A placeholder fragment containing a simple view.
 */
public class AgendaActivityFragment extends Fragment implements OnTaskComplete{

    private String mId;
    private ListView agendas;

    public AgendaActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mId = getArguments().getString("event_id");
        getAgendas();

        View view = inflater.inflate(R.layout.fragment_agenda, container, false);
        agendas = (ListView)view.findViewById(R.id.agendas_list);
        return view;
    }

    private void getAgendas(){
        RestApi api = new RestApi(this, getContext());
        GetAgendasTask getAgendasTask = api.getAgendas(mId);
        getAgendasTask.execute((Void) null);
    }

    @Override
    public void onTaskComplete() {
        agendas.setAdapter(new ArrayAdapter<Agendas.Agenda>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                Agendas.AGENDAS));
    }
}
