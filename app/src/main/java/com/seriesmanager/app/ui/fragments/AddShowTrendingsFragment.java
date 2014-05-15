package com.seriesmanager.app.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.seriesmanager.app.Comm;
import com.seriesmanager.app.R;
import com.seriesmanager.app.database.DBHelper;
import com.seriesmanager.app.entities.Show;
import com.seriesmanager.app.entities.ShowSummary;
import com.seriesmanager.app.interfaces.OnShowListInteractionListener;
import com.seriesmanager.app.parsers.trakt.ShowExtendedParser;
import com.seriesmanager.app.parsers.trakt.TrendingShowsParser;
import com.seriesmanager.app.ui.dialogs.ShowSummaryDialog;
import com.seriesmanager.app.ui.dialogs.WarningDialog;

import java.util.ArrayList;
import java.util.List;

public class AddShowTrendingsFragment extends ListFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ListView list;

    private OnShowListInteractionListener mListener;

    public AddShowTrendingsFragment() {
    }

    public static AddShowTrendingsFragment newInstance(String param1, String param2) {
        AddShowTrendingsFragment fragment = new AddShowTrendingsFragment();
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
        ShowSummary sh = (ShowSummary) l.getAdapter().getItem(position);
        //Intent intent = new Intent(null, null, getActivity(), ShowActivity.class);
        new ShowSummaryDialog(sh).show(getActivity().getSupportFragmentManager(), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //View view =  inflater.inflate(R.layout.fragment_add_show_trendings, container, false);

        list = (ListView) getActivity().findViewById(android.R.id.list);
        List<ShowSummary> shows = null;
        try {
            shows = new TrendingShowsParser().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        WarningDialog wd = new WarningDialog("Please Wait", "Getting data");
        wd.show(getActivity().getSupportFragmentManager(), "");
        try {
            list.setAdapter(new SemiShowAdapter(getActivity(), shows));
            wd.dismiss();
        } catch (Exception e) {
            shows = new ArrayList<ShowSummary>();
            list.setAdapter(new SemiShowAdapter(getActivity(), shows));
            wd.dismiss();
            new ShowSummaryDialog(new ShowSummary(0, "Network Problem", "Please enable the network."))
                    .show(getActivity().getSupportFragmentManager(), "");
        }
        setListAdapter(new SemiShowAdapter(getActivity(), shows));

        //return view;
        return null;
    }

    /*public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(int id, String nama);
    }*/

    public class SemiShowAdapter extends ArrayAdapter<ShowSummary> {
        private final Context context;
        private final List<ShowSummary> values;

        public SemiShowAdapter(Context context, List<ShowSummary> values) {
            super(context, R.layout.fragment_start_show_list, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.fragment_start_show_list, parent, false);
            final ShowSummary sh = values.get(position);
            ((TextView) rowView.findViewById(R.id.text_start_name)).setText(sh.getName());
            ((ImageView) rowView.findViewById(R.id.image_cover)).setImageResource(R.drawable.ic_launcher);
            ((TextView) rowView.findViewById(R.id.text_start_summary)).setText(sh.getSummary());
            final ImageView img = ((ImageView) rowView.findViewById(R.id.image_add));
            img.setImageResource(R.drawable.ic_plus);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    img.setImageResource(R.drawable.ic_correct);
                    Toast.makeText(context, sh.getName() + " adicionada", Toast.LENGTH_SHORT).show();
                    try {
                        Show show = new ShowExtendedParser(sh.getId()).get();
                        Comm.showsList.add(show);
                        new DBHelper(getContext(), null).persistCompleteShow(show);
                        mListener.onShowListInteraction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ShowSummaryDialog(sh).show(getActivity().getSupportFragmentManager(), "");
                }
            });

            return rowView;
        }
    }
}
