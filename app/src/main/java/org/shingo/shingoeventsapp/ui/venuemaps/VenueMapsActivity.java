package org.shingo.shingoeventsapp.ui.venuemaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.venuemaps.GetVenueMapsTask;
import org.shingo.shingoeventsapp.data.venuemaps.VenueMaps;
import org.shingo.shingoeventsapp.ui.events.EventListActivity;

public class VenueMapsActivity extends AppCompatActivity implements OnTaskComplete{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String mEvent;
    private ProgressDialog pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mEvent = getIntent().getExtras().getString("event_id");
        pb = new ProgressDialog(this);
        pb.setMessage("Loading Images...");
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        RestApi api = new RestApi(this, this);
        GetVenueMapsTask getVenueMapsTask = api.getVenueMaps(mEvent);
        getVenueMapsTask.execute((Void) null);
        pb.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Intent i = new Intent(this, EventListActivity.class);
            i.putExtra("event_id", mEvent);
            navigateUpTo(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskComplete() {
        mViewPager.setAdapter(mSectionsPagerAdapter);
        pb.dismiss();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class VenueMapFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public VenueMapFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static VenueMapFragment newInstance(int sectionNumber) {
            VenueMapFragment fragment = new VenueMapFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            VenueMaps.VMap map = VenueMaps.MAPS.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1);
            View rootView = inflater.inflate(R.layout.fragment_venue_maps, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(map.name);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.section_image);
            imageView.setImageDrawable(new BitmapDrawable(getResources(), map.map));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return VenueMapFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return VenueMaps.MAPS.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return VenueMaps.MAPS.get(position).name;
        }
    }
}
