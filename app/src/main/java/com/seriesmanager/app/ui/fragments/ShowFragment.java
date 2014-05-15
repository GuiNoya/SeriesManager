package com.seriesmanager.app.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.seriesmanager.app.Comm;
import com.seriesmanager.app.R;
import com.seriesmanager.app.adapters.ShowListAdapter;
import com.seriesmanager.app.database.DBHelper;
import com.seriesmanager.app.entities.Season;
import com.seriesmanager.app.entities.Show;
import com.seriesmanager.app.interfaces.OnShowListInteractionListener;
import com.seriesmanager.app.ui.ShowActivity;

import java.util.Map;

public class ShowFragment extends ListFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnShowListInteractionListener mListener;

    private ShowListAdapter mAdapter;

    public ShowFragment() {
    }

    public static ShowFragment newInstance(String param1, String param2) {
        ShowFragment fragment = new ShowFragment();
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

        //setListAdapter(new ShowListAdapter(getActivity(), TestContent.SHOWS));
        mAdapter = new ShowListAdapter(getActivity(), Comm.showsList);
        setListAdapter(mAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnShowListInteractionListener) activity;
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
        Show sh = (Show) l.getAdapter().getItem(position);
        //Intent intent = new Intent(null, null, getActivity(), ShowActivity.class);
        /*DBHelper db = new DBHelper(Comm.mainContext, null);
        db.persistShow(sh);*/
        if (!sh.isLoaded()) {
            new DBHelper(Comm.mainContext, null).loadShowExtraInfo(sh);
            sh.setLoaded(true);
        }
        Intent intent = new Intent(Comm.mainContext, ShowActivity.class);
        Comm.actualShow = sh;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_list_shows, container, false);
    }

    public void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(int id, String name, Map<Integer, Season> seasons);
    }
}
