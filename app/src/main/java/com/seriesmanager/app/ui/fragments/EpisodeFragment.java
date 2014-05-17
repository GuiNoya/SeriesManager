package com.seriesmanager.app.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.seriesmanager.app.Comm;
import com.seriesmanager.app.R;
import com.seriesmanager.app.adapters.ShowTempAdapter;
import com.seriesmanager.app.database.DBHelper;
import com.seriesmanager.app.entities.Episode;
import com.seriesmanager.app.entities.Season;
import com.seriesmanager.app.entities.Show;
import com.seriesmanager.app.interfaces.OnEpisodeInteractionListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EpisodeFragment extends ListFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnEpisodeInteractionListener mListener;

    public EpisodeFragment() {
    }

    public static EpisodeFragment newInstance(String param1, String param2) {
        EpisodeFragment fragment = new EpisodeFragment();
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

        final ExpandableListView lv = (ExpandableListView) getActivity().findViewById(R.id.container_seasons).findViewById(android.R.id.list);

        List<ShowTempAdapter.ParentGroup> list = new ArrayList<ShowTempAdapter.ParentGroup>();
        Map<Integer, Season> seasons = Comm.actualShow.getSeasons();
        for (int i : seasons.keySet()) {
            ShowTempAdapter.ParentGroup pai = new ShowTempAdapter.ParentGroup("Season " + i);
            list.add(pai);
            Map<Integer, Episode> hm = seasons.get(i).getEpisodes();
            List<Episode> al = new ArrayList<Episode>();
            pai.setArrayChildren(al);
            for (int j : hm.keySet()) {
                al.add(hm.get(j));
            }
        }

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int pos, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    new AlertDialog.Builder(getActivity()).setTitle("Mark all watched")
                            .setMessage("Are you sure do you want to mark all the episodes in the season watched?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ShowTempAdapter adapter = ((ShowTempAdapter) lv.getExpandableListAdapter());
                                    Iterator<Episode> it = adapter.getSeasonEpisodes(pos).iterator();
                                    while (it.hasNext()) {
                                        Episode ep = it.next();
                                        if (!ep.isWatched()) {
                                            ep.setWatched(true);
                                            new DBHelper(getActivity(), null).updateEpisode(ep);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).show();
                    return true;
                }
                return false;
            }
        });

        lv.setAdapter(new ShowTempAdapter(getActivity(), list));
        //setListAdapter(new ShowTempAdapter(getActivity(), list));
        if (Comm.actualSeason != null) {
            lv.expandGroup(Comm.actualSeason.getSeasonNumber() - 1);
            if (Comm.actualEpisode != null) {
                //lv.getExpandableListAdapter().get
                //lv.setSelection(Comm.actualEpisode.getEpisodeNumber());
                Log.w("TODO", "make focus on expanded episode");
                //lv.setSelectedChild(Comm.actualSeason.getSeasonNumber()-1, Comm.actualEpisode.getEpisodeNumber()-1, true);
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

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(int id, Show show, String name, boolean watched);
    }

}
