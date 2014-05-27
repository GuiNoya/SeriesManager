package com.seriesmanager.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.seriesmanager.app.R;
import com.seriesmanager.app.adapters.CalendarAdapter;
import com.seriesmanager.app.database.DBHelper;
import com.seriesmanager.app.interfaces.OnEpisodeInteractionListener;

import java.util.List;

public class CalendarFragment extends ListFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnEpisodeInteractionListener mListener;

    private CalendarAdapter mAdapter;

    public CalendarFragment() {
    }

    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        ExpandableListView lv = (ExpandableListView) getActivity().findViewById(R.id.container_calendar).findViewById(android.R.id.list);

        List<CalendarAdapter.ParentGroup> list = new DBHelper(getActivity(), null).loadCalendarShows();
        mAdapter = new CalendarAdapter(getActivity(), list);
        lv.setAdapter(mAdapter);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getArrayChildren().size() == 0) {
                list.remove(i);
                i--;
            } else if (list.get(i).getTitle().equals("Today")) {
                lv.expandGroup(i);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnEpisodeInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.w("ListFragment", "onListItemClick");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_list_calendar, container, false);
    }

    public void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(int id, String name, boolean watched);
    }
}
