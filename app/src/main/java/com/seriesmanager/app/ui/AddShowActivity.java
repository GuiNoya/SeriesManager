package com.seriesmanager.app.ui;

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
import android.widget.EditText;
import android.widget.ListView;

import com.seriesmanager.app.R;
import com.seriesmanager.app.entities.ShowSummary;
import com.seriesmanager.app.interfaces.OnShowListInteractionListener;
import com.seriesmanager.app.parsers.trakt.ShowSearchParser;
import com.seriesmanager.app.ui.dialogs.ShowSummaryDialog;
import com.seriesmanager.app.ui.dialogs.WarningDialog;
import com.seriesmanager.app.ui.fragments.AddShowSearchFragment;
import com.seriesmanager.app.ui.fragments.AddShowTrendingsFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//import com.seriesmanager.app.parsers.TheTVDB.ShowSearchParser;

public class AddShowActivity extends ActionBarActivity implements ActionBar.TabListener, OnShowListInteractionListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_show);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_show, menu);
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onShowListInteraction() {
        Log.w("AddShowActivity", "onShowListInteraction");
    }

    public void onButtonSearchClick(View vi) {
        List<ShowSummary> shows = null;
        View pai = (View) vi.getParent();
        EditText editText = (EditText) pai.findViewById(R.id.edit_text_add_search_show);
        if (editText.getText().toString() == null) {
            return;
        }
        try {
            shows = new ShowSearchParser(editText.getText().toString()).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        WarningDialog wd = new WarningDialog("Please Wait", "Getting data");
        wd.show(getSupportFragmentManager(), "");
        try {
            ListView list = (ListView) pai.findViewById(android.R.id.list);
            AddShowSearchFragment.SemiShowAdapter adapter = new AddShowSearchFragment.SemiShowAdapter(this, shows);
            list.setAdapter(adapter);
            wd.dismiss();
        } catch (Exception e) {
            shows = new ArrayList<ShowSummary>();
            ListView list = (ListView) pai.findViewById(android.R.id.list);
            AddShowSearchFragment.SemiShowAdapter adapter = new AddShowSearchFragment.SemiShowAdapter(this, shows);
            list.setAdapter(adapter);
            wd.dismiss();
            new ShowSummaryDialog(new ShowSummary(0, "Network Problem", "Please enable the network."))
                    .show(getSupportFragmentManager(), "");
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
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_add_show_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_add_show_section2).toUpperCase(l);
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
                rootView = inflater.inflate(R.layout.fragment_add_show_trendings, container, false);
                //getSupportFragmentManager().beginTransaction().add(R.id.container_add_trendings, AddShowTrendingsFragment.newInstance("", "")).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.container_add_trendings, AddShowTrendingsFragment.newInstance("", "")).commit();
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                rootView = inflater.inflate(R.layout.fragment_add_show_search, container, false);
                getSupportFragmentManager().beginTransaction().add(R.id.container_add_search, AddShowSearchFragment.newInstance("", "")).commit();
            }
            return rootView;
        }
    }
}
