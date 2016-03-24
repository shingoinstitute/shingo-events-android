package org.shingo.shingoeventsapp.ui.affiliates;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.affiliates.Affiliates;

/**
 * A fragment representing a single Affiliate detail screen.
 * This fragment is either contained in a {@link AffiliateListActivity}
 * in two-pane mode (on tablets) or a {@link AffiliateDetailActivity}
 * on handsets.
 */
public class AffiliateDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Affiliates.Affiliate mAffiliate;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AffiliateDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mAffiliate = Affiliates.AFFILIATE_MAP.get(getArguments().getString(ARG_ITEM_ID));


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_affiliate_detail, container, false);

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mAffiliate.name);
            appBarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
            if(mAffiliate.logo != null)
                ((ImageView)activity.findViewById(R.id.affiliate_logo)).setImageDrawable(new BitmapDrawable(getResources(),mAffiliate.logo));
            else
                activity.findViewById(R.id.affiliate_logo).setVisibility(View.GONE);
        }

        if (mAffiliate != null) {
            ((TextView) rootView.findViewById(R.id.affiliate_website)).setText((mAffiliate.website.equals("null")) ? "" : mAffiliate.website);
            ((TextView) rootView.findViewById(R.id.affiliate_email)).setText((mAffiliate.email.equals("null")) ? "" : mAffiliate.email);
            ((TextView) rootView.findViewById(R.id.affiliate_detail)).setText(Html.fromHtml(mAffiliate.appAbstract));
        }

        return rootView;
    }
}
