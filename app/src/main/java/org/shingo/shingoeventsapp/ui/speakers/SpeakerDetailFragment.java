package org.shingo.shingoeventsapp.ui.speakers;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.speakers.Speakers;

/**
 * A fragment representing a single Speaker detail screen.
 * This fragment is either contained in a {@link SpeakerListActivity}
 * in two-pane mode (on tablets) or a {@link SpeakerDetailActivity}
 * on handsets.
 */
public class SpeakerDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Speakers.Speaker mSpeaker;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SpeakerDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mSpeaker = Speakers.SPEAKER_MAP.get(getArguments().getString(ARG_ITEM_ID));

//            while (mSpeaker == null){
//                try {
//                    Thread.sleep(1000);
//
//                    mSpeaker = Speakers.SPEAKER_MAP.get(getArguments().getString(ARG_ITEM_ID));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mSpeaker.displayName);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_speaker_detail, container, false);
        Activity activity = this.getActivity();

        // Show the dummy content as text in a TextView.
        if (mSpeaker != null) {
            ((TextView) rootView.findViewById(R.id.speaker_detail)).setText(Html.fromHtml(mSpeaker.bio), TextView.BufferType.SPANNABLE);
            ImageView picture = (ImageView) activity.findViewById(R.id.speaker_image);
            if(picture != null){
                picture.setImageDrawable(new BitmapDrawable(getResources(), mSpeaker.getRoundPicture(getContext())));
                picture.bringToFront();
            }
        }

        return rootView;
    }
}
