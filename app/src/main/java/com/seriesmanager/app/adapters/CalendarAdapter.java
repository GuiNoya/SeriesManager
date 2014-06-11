package com.seriesmanager.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.seriesmanager.app.Comm;
import com.seriesmanager.app.R;
import com.seriesmanager.app.database.DBHelper;
import com.seriesmanager.app.entities.Episode;
import com.seriesmanager.app.entities.ShowLite;
import com.seriesmanager.app.interfaces.OnEpisodeInteractionListener;
import com.seriesmanager.app.ui.ShowActivity;

import java.util.Date;
import java.util.List;

public class CalendarAdapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater;
    private List<ParentGroup> mParent;
    private Context context;

    public CalendarAdapter(Context context, List<ParentGroup> parent) {
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
        return mParent.get(i).getArrayChildren().get(i1).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_text, viewGroup, false);
        }
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(getGroup(i).toString());

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_calendar_show, viewGroup, false);
        }

        final CalendarEpisode ep = mParent.get(i).getArrayChildren().get(i1);

        ((TextView) view.findViewById(R.id.text_name)).setText(ep.getShow().getName());
        String se = "S" + ep.getSeasonNumber() + "E" + ep.getEpisodeNumber();
        ((TextView) view.findViewById(R.id.text_episode)).setText(se);
        String[] datas = ep.getAirDate().toString().split(" ");
        String data = datas[0] + " " + datas[2] + " " + datas[1];
        ((TextView) view.findViewById(R.id.text_date)).setText(data);
        final CheckBox cb = (CheckBox) view.findViewById(R.id.check_box_watched);
        cb.setChecked(ep.isWatched());

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ep.setWatched(cb.isChecked());
                new DBHelper(context, null).updateEpisodeWatched(ep.getId(), ep.isWatched());
                ((OnEpisodeInteractionListener) context).onEpisodeInteraction(new Episode());
                //TODO: fix interactions
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Comm.mainContext, ShowActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("show", ep.getShow().getId());
                intent.putExtra("season", ep.getSeasonNumber());
                intent.putExtra("episode", ep.getEpisodeNumber());
                Comm.mainContext.startActivity(intent);
            }
        });
        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Episode Click")
                        .setMessage(ep.getName() + "\n" + ep.getEpisodeNumber() + "\n" + ep.getAirDate().toString() +
                                "\nWatched: " + ep.isWatched())
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

    public static class ParentGroup {
        private String mTitle;
        private List<CalendarEpisode> mArrayChildren;

        public ParentGroup() {
        }

        public ParentGroup(String mTitle) {
            this.mTitle = mTitle;
        }

        public ParentGroup(String mTitle, List<CalendarEpisode> mArrayChildren) {
            this.mTitle = mTitle;
            this.mArrayChildren = mArrayChildren;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String mTitle) {
            this.mTitle = mTitle;
        }

        public List<CalendarEpisode> getArrayChildren() {
            return mArrayChildren;
        }

        public void setArrayChildren(List<CalendarEpisode> mArrayChildren) {
            this.mArrayChildren = mArrayChildren;
        }
    }

    public static class CalendarEpisode {

        private long id;
        private ShowLite show;
        private int seasonNumber;
        private int episodeNumber;
        private String name;
        private Date airDate;
        private boolean watched;

        public CalendarEpisode() {
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public ShowLite getShow() {
            return show;
        }

        public void setShow(ShowLite show) {
            this.show = show;
        }

        public int getSeasonNumber() {
            return seasonNumber;
        }

        public void setSeasonNumber(int seasonNumber) {
            this.seasonNumber = seasonNumber;
        }

        public int getEpisodeNumber() {
            return episodeNumber;
        }

        public void setEpisodeNumber(int episodeNumber) {
            this.episodeNumber = episodeNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getAirDate() {
            return airDate;
        }

        public void setAirDate(Date airDate) {
            this.airDate = airDate;
        }

        public boolean isWatched() {
            return watched;
        }

        public void setWatched(boolean watched) {
            this.watched = watched;
        }
    }
}
