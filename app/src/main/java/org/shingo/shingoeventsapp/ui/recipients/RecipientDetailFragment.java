package org.shingo.shingoeventsapp.ui.recipients;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.recipients.Recipients;

/**
 * A fragment representing a single Recipient detail screen.
 * This fragment is either contained in a {@link RecipientListActivity}
 * in two-pane mode (on tablets) or a {@link RecipientDetailActivity}
 * on handsets.
 */
public class RecipientDetailFragment<T> extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private T mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipientDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            if(mItem instanceof Recipients.AwardRecipient) {
                mItem = (T)Recipients.AWARD_RECIPIENT_MAP.get(getArguments().getString(ARG_ITEM_ID));
            } else if(mItem instanceof Recipients.ResearchRecipient){
                mItem = (T)Recipients.RESEARCH_RECIPIENT_MAP.get(getArguments().getString(ARG_ITEM_ID));
            }

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                if(mItem instanceof Recipients.AwardRecipient) {
                    appBarLayout.setTitle(((Recipients.AwardRecipient) mItem).name);
                } else if(mItem instanceof Recipients.ResearchRecipient){
                    appBarLayout.setTitle(((Recipients.ResearchRecipient) mItem).book);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipient_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            if(mItem instanceof Recipients.AwardRecipient) {
                ((TextView) rootView.findViewById(R.id.recipient_detail)).setText(((Recipients.AwardRecipient) mItem).Abstract);
            } else if(mItem instanceof Recipients.ResearchRecipient){
                ((TextView) rootView.findViewById(R.id.recipient_detail)).setText(((Recipients.ResearchRecipient) mItem).Abstract);
            }
        }

        return rootView;
    }
}
