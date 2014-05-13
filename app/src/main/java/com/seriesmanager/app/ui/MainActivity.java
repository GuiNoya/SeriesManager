package com.seriesmanager.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.seriesmanager.app.Comm;
import com.seriesmanager.app.R;
import com.seriesmanager.app.StartActivity;
import com.seriesmanager.app.entities.Episode;
import com.seriesmanager.app.entities.Season;
import com.seriesmanager.app.entities.Show;
import com.seriesmanager.app.fillers.TestContent;
import com.seriesmanager.app.interfaces.OnEpisodeInteractionListener;
import com.seriesmanager.app.interfaces.OnShowInteractionListener;
import com.seriesmanager.app.interfaces.OnShowListInteractionListener;
import com.seriesmanager.app.ui.fragments.CalendarFragment;
import com.seriesmanager.app.ui.fragments.ShowFragment;
import com.seriesmanager.app.ui.fragments.ShowOverdueFragment;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, OnShowListInteractionListener, OnShowInteractionListener, OnEpisodeInteractionListener, ShowOverdueFragment.OnFragmentInteractionListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private boolean isNeedingRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Comm.mainContext = MainActivity.this;

        Comm.showsList = new ArrayList<Show>();
        Comm.showsList.addAll(TestContent.SHOWS);

        Intent intent = new Intent(this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
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
        Comm.mainContext = MainActivity.this;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (isNeedingRefresh) {
            Fragment frg = getSupportFragmentManager().findFragmentByTag("overdue");
            ((ShowOverdueFragment) frg).notifyDataChanged();
            frg = getSupportFragmentManager().findFragmentByTag("calendar");
            ((CalendarFragment) frg).notifyDataChanged();
            frg = getSupportFragmentManager().findFragmentByTag("shows");
            ((ShowFragment) frg).notifyDataChanged();
        }
    }

    @Override
    public void onEpisodeInteraction(Episode ep) {
        Log.w("MainActivity", "onEpisodeInteraction");
        isNeedingRefresh = true;
    }

    @Override
    public void onShowInteraction(Show show) {
        Log.w("MainActivity", "onShowInteraction");
        isNeedingRefresh = true;
    }

    @Override
    public void onShowListInteraction() {
        Log.w("MainActivity", "onShowListInteraction");
        isNeedingRefresh = true;
    }

    @Override
    public void onFragmentInteraction(int id, String name, Map<Integer, Season> seasons) {
        Log.w("MainActivity", "onFragmentInteraction");
    }

    public void onButtonClick(View vi) {
        if (vi.getId() == R.id.button_new) {
            Intent intent = new Intent(this, AddShowActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (vi.getId() == R.id.button_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
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
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
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
                rootView = inflater.inflate(R.layout.fragment_list_overdue, container, false);
                getSupportFragmentManager().beginTransaction().add(R.id.container_overdue, ShowOverdueFragment.newInstance("", ""), "overdue").commit();
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                rootView = inflater.inflate(R.layout.fragment_list_calendar, container, false);
                getSupportFragmentManager().beginTransaction().add(R.id.container_calendar, CalendarFragment.newInstance("", ""), "calendar").commit();
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
                rootView = inflater.inflate(R.layout.fragment_list_shows, container, false);
                getSupportFragmentManager().beginTransaction().add(R.id.container_shows, ShowFragment.newInstance("", ""), "shows").commit();
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 4) {
                rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
            } else {
                Log.e("NOT EXPECTED", "This Shouldn't br reached! (.ui.MainActivity: onCreateView)");
            }
            return rootView;
        }
    }
}
