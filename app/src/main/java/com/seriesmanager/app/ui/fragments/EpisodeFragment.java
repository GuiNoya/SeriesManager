package com.seriesmanager.app.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.seriesmanager.app.Constants;
import com.seriesmanager.app.R;
import com.seriesmanager.app.adapters.ShowTempAdapter;
import com.seriesmanager.app.database.DBHelper;
import com.seriesmanager.app.entities.Episode;
import com.seriesmanager.app.entities.Season;
import com.seriesmanager.app.entities.Show;
import com.seriesmanager.app.interfaces.OnEpisodeInteractionListener;
import com.seriesmanager.app.ui.MainActivity;
import com.seriesmanager.app.ui.ShowActivity;
import com.seriesmanager.app.ui.anim.ExpandAnimation;

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
    private ShowTempAdapter mAdapter;

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

        final Show showA = ((ShowActivity) getActivity()).getShow();

        List<ShowTempAdapter.ParentGroup> list = new ArrayList<ShowTempAdapter.ParentGroup>();
        Map<Integer, Season> seasons = showA.getSeasons();
        for (int i : seasons.keySet()) {
            ShowTempAdapter.ParentGroup pai;
            if (i == 0) {
                pai = new ShowTempAdapter.ParentGroup("Specials");
            } else {
                pai = new ShowTempAdapter.ParentGroup("Season " + i);
            }
            pai.setSeason(seasons.get(i));
            list.add(pai);
            Map<Integer, Episode> hm = seasons.get(i).getEpisodes();
            List<Episode> al = new ArrayList<Episode>();
            pai.setArrayChildren(al);
            for (int j : hm.keySet()) {
                al.add(hm.get(j));
            }
        }

        lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
                View moreInfo = view.findViewById(R.id.more_info);
                ExpandAnimation anim = new ExpandAnimation(moreInfo, 1000); // Not working as planned
                moreInfo.startAnimation(anim);
                return true;
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int pos, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    CharSequence[] options = new CharSequence[]{"Share season", "Mark all watched"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Select option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            long p = lv.getExpandableListPosition(pos);
                            int position = ExpandableListView.getPackedPositionGroup(p);
                            if (which == 0) {
                                shareSeason(showA.getName(),
                                        mAdapter.getGroupSeasonNumber(position));
                            } else if (which == 1) {
                                showMarkSeasonWatchedDialog(position);
                            }
                        }
                    });
                    builder.show();
                    return true;
                } else if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    CharSequence[] options = new CharSequence[]{"Share episode"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Select option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            long p = lv.getExpandableListPosition(pos);
                            int positionGroup = ExpandableListView.getPackedPositionGroup(p);
                            int positionEpisode = ExpandableListView.getPackedPositionChild(p);
                            if (which == 0) {
                                Episode ep = (Episode) mAdapter.getChild(positionGroup, positionEpisode);
                                shareEpisode(showA.getName(),
                                        mAdapter.getGroupSeasonNumber(positionGroup),
                                        ep.getEpisodeNumber());
                            }
                        }
                    });
                    builder.show();
                    return true;
                }
                return false;
            }
        });

        lv.setAdapter(new ShowTempAdapter(getActivity(), this, list));
        //setListAdapter(new ShowTempAdapter(getActivity(), list));
        Bundle b = getActivity().getIntent().getExtras();
        if (b != null) {
            Integer i = (Integer) b.get("season");
            Integer j = (Integer) b.get("episode");
            if ((i != null) && (j != null)) {
                lv.expandGroup(i, false);
                lv.smoothScrollToPosition(i * 100 + j * 20);
            }
        }

        mAdapter = ((ShowTempAdapter) lv.getExpandableListAdapter());
    }

    private void showMarkSeasonWatchedDialog(final int pos) {
        new AlertDialog.Builder(getActivity()).setTitle("Mark all watched")
                .setMessage("Are you sure do you want to mark all the episodes in the season watched?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Iterator<Episode> it = mAdapter.getSeasonEpisodes(pos).iterator();
                        while (it.hasNext()) {
                            Episode ep = it.next();
                            if (!ep.isWatched()) {
                                ep.setWatched(true);
                                new DBHelper(getActivity(), null).updateEpisodeWatched(ep.getId(), ep.isWatched());
                            }
                            mAdapter.notifyDataSetChanged();
                            ((MainActivity) Comm.mainContext).onEpisodeInteraction(ep);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
    }

    public void shareEpisode(String showName, int seasonNumber, int episodeNumber) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String url = Constants.TRAKT_SHOW_URL + showName.trim().toLowerCase().replaceAll(" +", "+")
                + "/season/" + Integer.toString(seasonNumber) + "/episode/"
                + Integer.toString(episodeNumber);
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share episode via"));
    }

    private void shareSeason(String showName, int seasonNumber) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String url = Constants.TRAKT_SHOW_URL + showName.trim().toLowerCase().replaceAll(" +", "+")
                + "/season/" + Integer.toString(seasonNumber);
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share season via"));
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
