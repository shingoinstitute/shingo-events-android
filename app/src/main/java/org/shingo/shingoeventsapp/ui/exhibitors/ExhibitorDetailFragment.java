package org.shingo.shingoeventsapp.ui.exhibitors;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.exhibitors.Exhibitors;

/**
 * A fragment representing a single Exhibitor detail screen.
 * This fragment is either contained in a {@link ExhibitorListActivity}
 * in two-pane mode (on tablets) or a {@link ExhibitorDetailActivity}
 * on handsets.
 */
public class ExhibitorDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Exhibitors.Exhibitor mExhibitor;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExhibitorDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mExhibitor = Exhibitors.EXHIBITOR_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mExhibitor.name);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.exhibitor_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mExhibitor != null) {
            ((TextView) rootView.findViewById(R.id.exhibitor_detail)).setText(mExhibitor.email);
        }

        return rootView;
    }
}
