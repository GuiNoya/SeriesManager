package com.seriesmanager.app.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.seriesmanager.app.R;
import com.seriesmanager.app.database.DBHelper;
import com.seriesmanager.app.entities.Episode;
import com.seriesmanager.app.ui.fragments.EpisodeFragment;


public class EpisodeRatingDialog extends Dialog {

    Episode ep;
    Context context;
    EpisodeFragment episodeFragment;

    public EpisodeRatingDialog(Context context, EpisodeFragment episodeFragment, Episode ep) {
        super(context);
        this.context = context;
        this.episodeFragment = episodeFragment;
        this.ep = ep;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final EpisodeRatingDialog dialog = this;
        setTitle("Rating");
        setContentView(R.layout.fragment_rating_dialog);
        final Button btn_ok = (Button) findViewById(R.id.button_episode_rating_ok);
        final Button btn_share = (Button) findViewById(R.id.button_episode_rating_share);
        final NumberPicker np = (NumberPicker) findViewById(R.id.number_picker_rating_episode);
        np.setMinValue(1);
        np.setMaxValue(10);
        np.setValue(ep.getRating() == 0 ? 6 : ep.getRating());
        np.setWrapSelectorWheel(false);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                np.clearFocus();
                ep.setRating((short) np.getValue());
                new DBHelper(context, null).updateEpisode(ep);
                dialog.dismiss();
            }
        });
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                np.clearFocus();
                ep.setRating((short) np.getValue());
                new DBHelper(context, null).updateEpisode(ep);
                episodeFragment.shareEpisode(ep.getShow().getName(), ep.getSeason().getSeasonNumber(),
                        ep.getEpisodeNumber());
                dialog.dismiss();
            }
        });
    }
}
