package com.seriesmanager.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seriesmanager.app.Comm;
import com.seriesmanager.app.Constants;
import com.seriesmanager.app.R;
import com.seriesmanager.app.database.DBHelper;
import com.seriesmanager.app.entities.Episode;
import com.seriesmanager.app.interfaces.OnEpisodeInteractionListener;
import com.seriesmanager.app.interfaces.OnShowInteractionListener;
import com.seriesmanager.app.ui.fragments.EpisodeFragment;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class ShowActivity extends ActionBarActivity implements ActionBar.TabListener, OnEpisodeInteractionListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Remove this, separate all instances of the show (communication only by ids or numbers)
        if (Comm.actualShow == null) {
            Comm.actualShow = new DBHelper(this, null).loadCompleteShow(getIntent().getExtras().getInt("show"));
        }

        setContentView(R.layout.activity_show);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager_show);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }

        Bundle b = getIntent().getExtras();
        if (b != null && ((Integer) b.get("season")) != null) {
            mViewPager.setCurrentItem(1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_share_show) {
            shareShow(Comm.actualShow.getName());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onEpisodeInteraction(Episode ep) {
        Log.w("ShowActivity", "onEpisodeInteraction");
    }

    @Override
    public void onBackPressed() {
        Comm.actualShow = null;
        Comm.actualSeason = null;
        Comm.actualEpisode = null;
        super.onBackPressed();
    }

    public void shareShow(String showName) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, Constants.TRAKT_SHOW_URL + showName.trim().toLowerCase().replaceAll(" +", "+"));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share show via"));
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new PlaceholderFragment(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_show_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_show_section2).toUpperCase(l);
            }
            return null;
        }
    }

    public class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public PlaceholderFragment(int sectionNumber) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            setArguments(args);
        }

        public PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = null;
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.fragment_show_info, container, false);
                final ImageView img = (ImageView) rootView.findViewById(R.id.image_plus);
                if (Comm.actualShow.isFavorite()) {
                    img.setImageResource(android.R.drawable.btn_star_big_on);
                } else {
                    img.setImageResource(android.R.drawable.btn_star_big_off);
                }
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Comm.actualShow.setFavorite(!Comm.actualShow.isFavorite());
                        if (Comm.actualShow.isFavorite()) {
                            ((ImageView) view).setImageResource(android.R.drawable.btn_star_big_on);
                        } else {
                            ((ImageView) view).setImageResource(android.R.drawable.btn_star_big_off);
                        }
                        new DBHelper(getActivity(), null).updateShow(Comm.actualShow);
                        ((OnShowInteractionListener) Comm.mainContext).onShowInteraction(Comm.actualShow);
                    }
                });
                ((TextView) rootView.findViewById(R.id.text_name)).setText(Comm.actualShow.getName());
                ((TextView) rootView.findViewById(R.id.text_summary)).setText(Comm.actualShow.getSummary());
                SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
                ((TextView) rootView.findViewById(R.id.text_data)).setText(ft.format(Comm.actualShow.getFirstAired()));
                ((TextView) rootView.findViewById(R.id.text_emissora)).setText(Comm.actualShow.getNetwork());
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                rootView = inflater.inflate(R.layout.fragment_list_show_seasons, container, false);
                getSupportFragmentManager().beginTransaction().add(R.id.container_seasons, EpisodeFragment.newInstance("", "")).commit();
            }
            return rootView;
        }
    }
}
