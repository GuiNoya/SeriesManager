package com.seriesmanager.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.seriesmanager.app.Comm;
import com.seriesmanager.app.R;
import com.seriesmanager.app.adapters.CalendarAdapter;
import com.seriesmanager.app.adapters.OverdueAdapter;
import com.seriesmanager.app.adapters.ShowListAdapter;
import com.seriesmanager.app.database.DBHelper;
import com.seriesmanager.app.entities.Episode;
import com.seriesmanager.app.entities.Season;
import com.seriesmanager.app.entities.Show;
import com.seriesmanager.app.entities.ShowLite;
import com.seriesmanager.app.interfaces.OnCalendarInteractionListener;
import com.seriesmanager.app.interfaces.OnEpisodeInteractionListener;
import com.seriesmanager.app.interfaces.OnShowInteractionListener;
import com.seriesmanager.app.interfaces.OnShowListInteractionListener;
import com.seriesmanager.app.loaders.UpdateShowsLoader;
import com.seriesmanager.app.ui.fragments.CalendarFragment;
import com.seriesmanager.app.ui.fragments.ShowFragment;
import com.seriesmanager.app.ui.fragments.ShowOverdueFragment;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, LoaderManager.LoaderCallbacks<List<Show>>, OnCalendarInteractionListener, OnShowListInteractionListener, OnShowInteractionListener, OnEpisodeInteractionListener, ShowOverdueFragment.OnFragmentInteractionListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private boolean isNeedingRefresh = false;
    private boolean running;
    private short filterFlag = 0;
    private MenuItem filterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Comm.mainContext = MainActivity.this;

        findViewById(R.id.progress_bar_update).setVisibility(View.GONE);

        Comm.showsInstances = new DBHelper(this, null).loadShowsAll();
        Comm.showsFiltered = Comm.showsInstances;

        if (Comm.showsInstances.size() == 0) {
            Intent intent = new Intent(this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            getSupportLoaderManager().initLoader(3, null, this);
        }

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                if (position == 2) {
                    filterView.setVisible(true);
                } else {
                    filterView.setVisible(false);
                }
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
        if (b != null) {
            if (b.getBoolean("via_notification", false)) {
                actionBar.setSelectedNavigationItem(1);
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getSupportActionBar().setSelectedNavigationItem(1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        running = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        filterView = menu.findItem(R.id.action_filter);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_add) {
            Intent intent = new Intent(this, AddShowActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_filter) {
            return true;
        } else if (id == R.id.action_filter_favorite) {
            filterView.setIcon(R.drawable.ic_action_filter_active);
            filterFlag = 1;
            final ShowFragment frg = (ShowFragment) getSupportFragmentManager().findFragmentByTag("shows");
            if (frg != null) {
                filterShows();
                ShowListAdapter adapter = new ShowListAdapter(this, Comm.showsFiltered);
                frg.setListAdapter(adapter);
            }
            return true;
        } else if (id == R.id.action_filter_on_air) {
            filterView.setIcon(R.drawable.ic_action_filter_active);
            filterFlag = 2;
            final ShowFragment frg = (ShowFragment) getSupportFragmentManager().findFragmentByTag("shows");
            if (frg != null) {
                filterShows();
                ShowListAdapter adapter = new ShowListAdapter(this, Comm.showsFiltered);
                frg.setListAdapter(adapter);
            }
            return true;
        } else if (id == R.id.action_filter_ended) {
            filterView.setIcon(R.drawable.ic_action_filter_active);
            filterFlag = 3;
            final ShowFragment frg = (ShowFragment) getSupportFragmentManager().findFragmentByTag("shows");
            if (frg != null) {
                filterShows();
                ShowListAdapter adapter = new ShowListAdapter(this, Comm.showsFiltered);
                frg.setListAdapter(adapter);
            }
            return true;
        } else if (id == R.id.action_filter_all) {
            filterView.setIcon(R.drawable.ic_action_filter_default);
            filterFlag = 0;
            final ShowFragment frg = (ShowFragment) getSupportFragmentManager().findFragmentByTag("shows");
            if (frg != null) {
                filterShows();
                ShowListAdapter adapter = new ShowListAdapter(this, Comm.showsFiltered);
                frg.setListAdapter(adapter);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Show>> onCreateLoader(int id, Bundle args) {
        findViewById(R.id.progress_bar_update).setVisibility(View.VISIBLE);
        Map<Long, Long> map = new LinkedHashMap<Long, Long>();
        long now = new Date().getTime();
        for (ShowLite show : Comm.showsInstances) {
            if (show.getLastUpdated() < now - 86400000) {
                map.put((long) show.getId(), show.getLastUpdated());
            }
        }
        return new UpdateShowsLoader(this, map);
    }

    @Override
    public void onLoadFinished(Loader<List<Show>> loader, List<Show> data) {
        findViewById(R.id.progress_bar_update).setVisibility(View.GONE);
        //Toast.makeText(this, "Updated " + Integer.toString(data.size()) + " shows", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaderReset(Loader<List<Show>> loader) {
        if (Comm.updated)
            loader = null;
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
            refreshFragments();
            isNeedingRefresh = false;
        }
    }

    private void filterShows() {
        if (filterFlag == 0) {
            Comm.showsFiltered = Comm.showsInstances;
        } else if (filterFlag == 1) {
            Comm.showsFiltered = new DBHelper(this, null).getFavoriteShows();
        } else if (filterFlag == 2) {
            Comm.showsFiltered = new DBHelper(this, null).getOnAirShows();
        } else if (filterFlag == 3) {
            Comm.showsFiltered = new DBHelper(this, null).getEndedShows();
        }
    }

    private void refreshFragments() {
        FragmentManager fm = getSupportFragmentManager();
        final ShowFragment frg3 = (ShowFragment) fm.findFragmentByTag("shows");
        Comm.showsInstances = new DBHelper(this, null).loadShowsAll();
        filterShows();
        if (frg3 != null) {
            final ShowListAdapter adapter = new ShowListAdapter(this, Comm.showsFiltered);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    frg3.setListAdapter(adapter);
                }
            });
        }
        final ShowOverdueFragment frg1 = (ShowOverdueFragment) fm.findFragmentByTag("overdue");
        if (frg1 != null) {
            final OverdueAdapter adapter = new OverdueAdapter(this, new DBHelper(this, null).loadOverdueShows());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    frg1.setListAdapter(adapter);
                }
            });
        }
        final CalendarFragment frg2 = (CalendarFragment) fm.findFragmentByTag("calendar");
        if (frg2 != null) {
            final ExpandableListView lv = (ExpandableListView) findViewById(R.id.container_calendar).findViewById(android.R.id.list);

            final List<CalendarAdapter.ParentGroup> list = new DBHelper(this, null).loadCalendarShows();
            final CalendarAdapter adapter = new CalendarAdapter(this, list);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lv.setAdapter(adapter);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getArrayChildren().size() == 0) {
                            list.remove(i);
                            i--;
                        } else if (list.get(i).getTitle().equals("Today")) {
                            lv.expandGroup(i);
                        }
                    }
                }
            });
        }
        final View v = findViewById(R.id.relativeLayout0);
        if (v != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DBHelper dbHelper = new DBHelper(getApplication(), null);
                    ((TextView) v.findViewById(R.id.number_overdue_shows)).setText("" + dbHelper.getNumOverdueShows());
                    ((TextView) v.findViewById(R.id.number_onair_shows)).setText("" + dbHelper.getNumOnAirShows());
                    ((TextView) v.findViewById(R.id.number_ended_shows)).setText("" + dbHelper.getNumEndedShows());
                    ((TextView) v.findViewById(R.id.number_total_shows)).setText("" + dbHelper.getNumTotalShows());
                    int watchedEpisode = dbHelper.getNumWatchedEpisodes();
                    int unwatchedEpisode = dbHelper.getNumUnwatchedEpisodes();
                    ((TextView) v.findViewById(R.id.number_watched_episodes)).setText("" + watchedEpisode);
                    ((TextView) v.findViewById(R.id.number_unwatched_episodes)).setText("" + unwatchedEpisode);
                    ((TextView) v.findViewById(R.id.number_total_episodes)).setText("" + (watchedEpisode + unwatchedEpisode));
                }
            });
        }
    }

    private void refreshFragmentsWithoutCalendar() {
        FragmentManager fm = getSupportFragmentManager();
        final ShowFragment frg3 = (ShowFragment) fm.findFragmentByTag("shows");
        Comm.showsInstances = new DBHelper(this, null).loadShowsAll();
        filterShows();
        if (frg3 != null) {
            final ShowListAdapter adapter = new ShowListAdapter(this, Comm.showsFiltered);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    frg3.setListAdapter(adapter);
                }
            });
        }
        final ShowOverdueFragment frg1 = (ShowOverdueFragment) fm.findFragmentByTag("overdue");
        if (frg1 != null) {
            final OverdueAdapter adapter = new OverdueAdapter(this, new DBHelper(this, null).loadOverdueShows());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    frg1.setListAdapter(adapter);
                }
            });
        }
        final View v = findViewById(R.id.relativeLayout0);
        if (v != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DBHelper dbHelper = new DBHelper(getApplication(), null);
                    ((TextView) v.findViewById(R.id.number_overdue_shows)).setText("" + dbHelper.getNumOverdueShows());
                    ((TextView) v.findViewById(R.id.number_onair_shows)).setText("" + dbHelper.getNumOnAirShows());
                    ((TextView) v.findViewById(R.id.number_ended_shows)).setText("" + dbHelper.getNumEndedShows());
                    ((TextView) v.findViewById(R.id.number_total_shows)).setText("" + dbHelper.getNumTotalShows());
                    int watchedEpisode = dbHelper.getNumWatchedEpisodes();
                    int unwatchedEpisode = dbHelper.getNumUnwatchedEpisodes();
                    ((TextView) v.findViewById(R.id.number_watched_episodes)).setText("" + watchedEpisode);
                    ((TextView) v.findViewById(R.id.number_unwatched_episodes)).setText("" + unwatchedEpisode);
                    ((TextView) v.findViewById(R.id.number_total_episodes)).setText("" + (watchedEpisode + unwatchedEpisode));
                }
            });
        }
    }

    @Override
    public void onEpisodeInteraction(Episode ep) {
        Log.w("MainActivity", "onEpisodeInteraction");
        if (running) {
            refreshFragments();
            isNeedingRefresh = false;
        } else {
            isNeedingRefresh = true;
        }
    }


    @Override
    public void onCalendarEpisodeInteraction() {
        Log.w("MainActivity", "onCalendarInteraction");
        if (running) {
            refreshFragmentsWithoutCalendar();
            isNeedingRefresh = false;
        } else {
            isNeedingRefresh = true;
        }
    }

    @Override
    public void onShowInteraction(Show show) {
        Log.w("MainActivity", "onShowInteraction");
        if (running) {
            refreshFragments();
            isNeedingRefresh = false;
        } else {
            isNeedingRefresh = true;
        }
    }

    @Override
    public void onShowListInteraction() {
        Log.w("MainActivity", "onShowListInteraction");
        if (running) {
            refreshFragments();
            isNeedingRefresh = false;
        } else {
            isNeedingRefresh = true;
        }
    }

    @Override
    public void onFragmentInteraction(int id, String name, Map<Integer, Season> seasons) {
        Log.w("MainActivity", "onFragmentInteraction");
    }

    /*public void onButtonClick(View vi) {
        if (vi.getId() == R.id.button_new) {
            Intent intent = new Intent(this, AddShowActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (vi.getId() == R.id.button_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }*/

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
                DBHelper dbHelper = new DBHelper(getActivity(), null);
                ((TextView) rootView.findViewById(R.id.number_overdue_shows)).setText("" + dbHelper.getNumOverdueShows());
                ((TextView) rootView.findViewById(R.id.number_onair_shows)).setText("" + dbHelper.getNumOnAirShows());
                ((TextView) rootView.findViewById(R.id.number_ended_shows)).setText("" + dbHelper.getNumEndedShows());
                ((TextView) rootView.findViewById(R.id.number_total_shows)).setText("" + dbHelper.getNumTotalShows());
                int watchedEpisode = dbHelper.getNumWatchedEpisodes();
                int unwatchedEpisode = dbHelper.getNumUnwatchedEpisodes();
                ((TextView) rootView.findViewById(R.id.number_watched_episodes)).setText("" + watchedEpisode);
                ((TextView) rootView.findViewById(R.id.number_unwatched_episodes)).setText("" + unwatchedEpisode);
                ((TextView) rootView.findViewById(R.id.number_total_episodes)).setText("" + (watchedEpisode + unwatchedEpisode));
            }
            return rootView;
        }
    }
}
