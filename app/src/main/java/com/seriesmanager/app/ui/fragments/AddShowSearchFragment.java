package com.seriesmanager.app.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.seriesmanager.app.Comm;
import com.seriesmanager.app.R;
import com.seriesmanager.app.database.DBHelper;
import com.seriesmanager.app.entities.Show;
import com.seriesmanager.app.entities.ShowSummary;
import com.seriesmanager.app.interfaces.OnShowListInteractionListener;
import com.seriesmanager.app.loaders.SearchShowLoader;
import com.seriesmanager.app.parsers.trakt.ShowExtendedParser;
import com.seriesmanager.app.ui.dialogs.ShowSummaryDialog;

import java.util.ArrayList;
import java.util.List;

public class AddShowSearchFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<ShowSummary>> {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static OnShowListInteractionListener mListener;
    private final int LOADER_ID = 2;
    private String mParam1;
    private String mParam2;
    private ListView list;
    private ProgressBar progressBar;
    private EditText editText;
    private TextView empty;

    public AddShowSearchFragment() {
    }

    public static AddShowSearchFragment newInstance(String param1, String param2) {
        AddShowSearchFragment fragment = new AddShowSearchFragment();
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
        super.onListItemClick(l, v, position, id);
        ShowSummary sh = (ShowSummary) l.getAdapter().getItem(position);
        new ShowSummaryDialog(sh).show(getActivity().getSupportFragmentManager(), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_add_show_search, container, false);

        list = (ListView) view.findViewById(android.R.id.list);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_add_search);
        progressBar.setVisibility(View.GONE);
        empty = (TextView) view.findViewById(R.id.text_status_search);

        setListAdapter(new SemiShowAdapter(getActivity(), new ArrayList<ShowSummary>()));

        editText = (EditText) view.findViewById(R.id.edit_text_add_search_show);

        return view;
    }

    public String getQuery() {
        return editText.getText().toString().trim();
    }

    @Override
    public Loader<List<ShowSummary>> onCreateLoader(int id, Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        //editText.clearFocus();
        empty.setVisibility(View.GONE);
        list.setVisibility(View.GONE);
        return new SearchShowLoader(getActivity(), args.getString("text"));
    }

    @Override
    public void onLoadFinished(Loader<List<ShowSummary>> loader, List<ShowSummary> data) {
        if (data != null) {
            list.setAdapter(new SemiShowAdapter(getActivity(), data));
            //((SemiShowAdapter) getListAdapter()).addAll(data);
            progressBar.setVisibility(View.GONE);
            empty.setVisibility(View.GONE);
            //((SemiShowAdapter) getListAdapter()).notifyDataSetChanged();
            list.setVisibility(View.VISIBLE);
            if (data.size() == 0) {
                empty.setText("No shows found.");
                empty.setVisibility(View.VISIBLE);
            }
        } else {
            empty.setText("Problem loading data!\nVerify your network.");
            empty.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.w("Fragment", "onLoaderReset");
    }

    public static class SemiShowAdapter extends ArrayAdapter<ShowSummary> {
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
                        new ShowAdder().execute(sh.getId());
                        mListener.onShowListInteraction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return rowView;
        }

        private class ShowAdder extends AsyncTask<Integer, Void, Void> {

            @Override
            protected Void doInBackground(Integer... integers) {
                final Show show;
                try {
                    show = new ShowExtendedParser(integers[0]).get();
                    new DBHelper(context, null).persistCompleteShow(show);
                    Comm.showsList.add(show);
                    ((OnShowListInteractionListener) Comm.mainContext).onShowListInteraction();
                    ((Activity) mListener).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), show.getName() + " adicionada", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    ((Activity) mListener).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Couldn't add the show", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return null;
            }
        }
    }
}
