package com.seriesmanager.app.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seriesmanager.app.Comm;
import com.seriesmanager.app.R;
import com.seriesmanager.app.database.DBHelper;
import com.seriesmanager.app.entities.Episode;
import com.seriesmanager.app.entities.Season;
import com.seriesmanager.app.interfaces.OnEpisodeInteractionListener;
import com.seriesmanager.app.ui.dialogs.EpisodeRatingDialog;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

public class ShowTempAdapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater;
    private List<ParentGroup> mParent;
    private Context context;

    public ShowTempAdapter(Context context, List<ParentGroup> parent) {
        mParent = parent;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return mParent.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mParent.get(i).getArrayChildren().size();
    }

    @Override
    public Object getGroup(int i) {
        return mParent.get(i).getTitle();
    }

    public int getGroupSeasonNumber(int i) {
        return mParent.get(i).getSeasonNumber();
    }

    @Override
    public Object getChild(int i, int i1) {
        return mParent.get(i).getArrayChildren().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return (i * 100) + i1;
    }

    public List<Episode> getSeasonEpisodes(int i) {
        return mParent.get(i).getArrayChildren();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_season_list, viewGroup, false);
        }
        ((TextView) view.findViewById(R.id.text)).setText(getGroup(i).toString());
        final ProgressBar pb = (ProgressBar) view.findViewById(R.id.progress_bar_season);
        final Season se = Comm.actualShow.getSeasons().get(mParent.get(i).getSeasonNumber());
        if (se.getEpisodes().size() > 0) {
            pb.setMax(se.getEpisodes().values().size());
            int count = 0;
            Iterator<Episode> it = se.getEpisodes().values().iterator();
            while (it.hasNext()) {
                if (it.next().isWatched())
                    count++;
            }
            pb.setProgress(count);
        }

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_episode_season_list, viewGroup, false);
        }

        final Episode ep = mParent.get(i).getArrayChildren().get(i1);
        final CheckBox cb = ((CheckBox) view.findViewById(R.id.check_box_watched));

        ((TextView) view.findViewById(R.id.name)).setText(ep.getName());
        TextView tvName = ((TextView) view.findViewById(R.id.text_date));
        try {
            tvName.setText(new SimpleDateFormat("dd/MM/yyyy").format(ep.getAirDate()));
        } catch (Exception e) {
            tvName.setText("Date unknown");
        }
        ((TextView) view.findViewById(R.id.text_summary)).setText(ep.getSummary());
        cb.setChecked(ep.isWatched());

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ep.setWatched(cb.isChecked());
                new DBHelper(context, null).updateEpisode(ep);
                if (ep.isWatched())
                    new EpisodeRatingDialog(context, ep).show();
                ((OnEpisodeInteractionListener) Comm.mainContext).onEpisodeInteraction(ep);
                notifyDataSetChanged();
            }
        });

        View moreInfo = view.findViewById(R.id.more_info);
        //((RelativeLayout.LayoutParams) moreInfo.getLayoutParams()).bottomMargin = -50;
        moreInfo.setVisibility(View.GONE);
        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View moreInfo = view.findViewById(R.id.more_info);
                ExpandAnimation anim = new ExpandAnimation(moreInfo, 1000); // Not working as planned
                moreInfo.startAnimation(anim);
            }
        });*/
        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Episode Click")
                        .setMessage(ep.getName() + "\n" + ep.getEpisodeNumber() + "\n" +
                                ep.getAirDate().toLocaleString() + "\nWatched: " + ep.isWatched())
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });*/

        return view;
    }


    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    public static class ParentGroup {
        private String mTitle;
        private List<Episode> mArrayChildren;
        private int SeasonNumber;

        public ParentGroup() {
        }

        public ParentGroup(String mTitle) {
            this.mTitle = mTitle;
        }

        public ParentGroup(String mTitle, List<Episode> mArrayChildren) {
            this.mTitle = mTitle;
            this.mArrayChildren = mArrayChildren;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String mTitle) {
            this.mTitle = mTitle;
        }

        public List<Episode> getArrayChildren() {
            return mArrayChildren;
        }

        public void setArrayChildren(List<Episode> mArrayChildren) {
            this.mArrayChildren = mArrayChildren;
        }

        public int getSeasonNumber() {
            return SeasonNumber;
        }

        public void setSeasonNumber(int SeasonNumber) {
            this.SeasonNumber = SeasonNumber;
        }
    }
}
