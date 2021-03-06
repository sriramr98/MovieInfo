package com.sriramr.movieinfo.ui.tvshows.showsdetail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sriramr.movieinfo.R;
import com.sriramr.movieinfo.ui.people.peopledetail.PeopleDetailActivity;
import com.sriramr.movieinfo.ui.people.peoplepopular.PopularPeopleActivity;
import com.sriramr.movieinfo.ui.people.peoplepopular.models.PopularPeople;
import com.sriramr.movieinfo.ui.tvshows.showsdetail.models.Cast;
import com.sriramr.movieinfo.ui.tvshows.showsdetail.models.Genres;
import com.sriramr.movieinfo.ui.tvshows.showsdetail.models.Recommendation;
import com.sriramr.movieinfo.ui.tvshows.showsdetail.models.TvShowDetailResponse;
import com.sriramr.movieinfo.utils.AppConstants;
import com.sriramr.movieinfo.utils.Status;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TvShowDetailActivity extends AppCompatActivity implements TvShowCastAdapter.CastClickListener, TvShowRecommendationsAdapter.RecommendationClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.detail_poster_icon)
    ImageView detailPosterIcon;
    @BindView(R.id.detail_title)
    TextView detailTitle;
    @BindView(R.id.detail_status)
    TextView detailStatus;
    @BindView(R.id.detail_genre)
    TextView detailGenre;
    @BindView(R.id.detail_votes)
    TextView detailVotes;
    @BindView(R.id.detail_plot)
    TextView detailPlot;
    @BindView(R.id.detail_release_date)
    TextView detailReleaseDate;
    @BindView(R.id.detail_runtime)
    TextView detailRuntime;
    @BindView(R.id.detail_meta_score)
    TextView detailMetaScore;
    @BindView(R.id.detail_images_view)
    ImageView detailImagesView;
    @BindView(R.id.detail_videos_view)
    ImageView detailVideosView;
    @BindView(R.id.detail_rv_seasons)
    RecyclerView detailRvSeasons;
    @BindView(R.id.detail_cast_see_all)
    TextView detailCastSeeAll;
    @BindView(R.id.detail_rv_cast)
    RecyclerView detailRvCast;
    @BindView(R.id.detail_rv_recommendations)
    RecyclerView detailRvRecommendations;
    @BindView(R.id.tv_show_detail_scroll_image)
    ImageView tvShowDetailScrollImage;
    @BindView(R.id.detail_season_count)
    TextView detailSeasonCount;
    @BindView(R.id.detail_episodes_count)
    TextView detailEpisodesCount;
    @BindView(R.id.layout_seasons)
    CardView layoutSeasons;
    @BindView(R.id.layout_casts)
    CardView layoutCasts;
    @BindView(R.id.layout_recommendations)
    CardView layoutRecommendations;

    TvShowSeasonsAdapter seasonsAdapter;
    TvShowCastAdapter castAdapter;
    TvShowRecommendationsAdapter recommendationsAdapter;

    private TvShowsViewModel mViewModel;

    // TODO add similar tv shows later on
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_show_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();

        if (i == null || i.getExtras() == null) {
            Toast.makeText(this, "Error.. Please try agin. If the error persists, contact the developer", Toast.LENGTH_SHORT).show();
            finish();
            // finish wont stop the code below from executing. Once finish() is called, the onCreate() is executed and then closed.
            return;
        }

        mViewModel = ViewModelProviders.of(this).get(TvShowsViewModel.class);

        String tvShowId = i.getExtras().getString(AppConstants.TV_SHOW_ID);
        String showTitle = i.getExtras().getString(AppConstants.TV_SHOW_TITLE);
        mViewModel.init(tvShowId, showTitle);

        //seasons rv
        RecyclerView.LayoutManager seasonsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        detailRvSeasons.setLayoutManager(seasonsLayoutManager);
        detailRvSeasons.setNestedScrollingEnabled(false);
        detailRvSeasons.setHasFixedSize(true);
        seasonsAdapter = new TvShowSeasonsAdapter(this);
        detailRvSeasons.setAdapter(seasonsAdapter);

        // recommendation rv
        final RecyclerView.LayoutManager recommendationLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        detailRvRecommendations.setLayoutManager(recommendationLayoutManager);
        detailRvRecommendations.setHasFixedSize(true);
        detailRvRecommendations.setNestedScrollingEnabled(false);
        recommendationsAdapter = new TvShowRecommendationsAdapter(this, this);
        detailRvRecommendations.setAdapter(recommendationsAdapter);

        // cast rv
        RecyclerView.LayoutManager castLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        detailRvCast.setLayoutManager(castLayoutManager);
        detailRvCast.setHasFixedSize(true);
        detailRvCast.setNestedScrollingEnabled(false);
        castAdapter = new TvShowCastAdapter(this, this);
        detailRvCast.setAdapter(castAdapter);

        detailCastSeeAll.setOnClickListener(view -> {
            ArrayList<PopularPeople> popularPeople = new ArrayList<>();
            for (Cast c : mViewModel.getCasts()) {
                PopularPeople p = new PopularPeople();
                p.setName(c.getName());
                p.setId(c.getId());
                p.setProfilePath(c.getProfilePath());
                popularPeople.add(p);
            }

            Parcelable p = Parcels.wrap(popularPeople);

            Intent tvShowIntent = new Intent(TvShowDetailActivity.this, PopularPeopleActivity.class);
            tvShowIntent.putExtra(AppConstants.CAST_LIST, p);
            tvShowIntent.putExtra(AppConstants.TAG, AppConstants.MOVIE_CAST);
            startActivity(tvShowIntent);
        });

        observeDataFromApi();

    }

    private void observeDataFromApi() {
        mViewModel.getTvShowDetails().observe(this, showItem -> {
            if (showItem == null || showItem.getStatus() == Status.FAILURE) {
                Toast.makeText(this, "Error getting data from API", Toast.LENGTH_SHORT).show();
                return;
            }

            completeUI(showItem.getItem());
        });
    }

    private void completeUI(TvShowDetailResponse show) {

        // load image in collapsing toolbar
        Picasso.with(this).load(AppConstants.IMAGE_BASE_URL + AppConstants.BACKDROP_SIZE + show.getBackdropPath())
                .fit()
                .into(tvShowDetailScrollImage);

        // poster icon
        Picasso.with(this).load(AppConstants.IMAGE_BASE_URL + AppConstants.POSTER_SIZE + show.getPosterPath())
                .fit().centerCrop()
                .into(detailPosterIcon);

        //title
        detailTitle.setText(show.getName());

        // release status
        detailStatus.setText(show.getStatus());

        //genre
        StringBuilder genreBuilder = new StringBuilder();
        for (Genres g : show.getGenres()) {
            genreBuilder.append(g.getName());
            genreBuilder.append(", ");
        }
        String genre = genreBuilder.toString();
        detailGenre.setText(genre);

        // votes
        detailVotes.setText(String.valueOf(show.getVoteAverage()));

        //overview
        detailPlot.setText(show.getOverview());

        //release date
        detailReleaseDate.setText(show.getLastAirDate());

        // runtime
        detailRuntime.setText(String.valueOf(show.getEpisodeRunTime().get(0)));

        //metascore
        detailMetaScore.setText(String.valueOf(show.getVoteCount()));

        // episodes
        detailEpisodesCount.setText(String.valueOf(show.getNumberOfEpisodes()));

        // seasons
        detailSeasonCount.setText(String.valueOf(show.getNumberOfSeasons()));

        //images view
        Picasso.with(this).load(AppConstants.IMAGE_BASE_URL + AppConstants.POSTER_SIZE + show.getPosterPath())
                .centerCrop().fit().into(detailImagesView);

        // videos view
        // TODO improve this
        Picasso.with(this).load(AppConstants.IMAGE_BASE_URL + AppConstants.POSTER_SIZE + show.getPosterPath())
                .centerCrop().fit().into(detailVideosView);

        // seasons adapter
        if (mViewModel.getSeasons() != null || mViewModel.getSeasons().isEmpty())
            seasonsAdapter.setSeasons(mViewModel.getSeasons());
        else layoutSeasons.setVisibility(View.GONE);

        // cast adapter
        if (mViewModel.getCasts() != null || mViewModel.getCasts().isEmpty())
            castAdapter.setCast(mViewModel.getCasts());
        else layoutCasts.setVisibility(View.GONE);

        // recommendations adapter
        if (mViewModel.getRecommendations().getResults() != null || mViewModel.getRecommendations().getResults().isEmpty() || mViewModel.getRecommendations().getTotalResults() == 0)
            recommendationsAdapter.setRecommendations(mViewModel.getRecommendations().getResults());
        else layoutRecommendations.setVisibility(View.GONE);
    }

    @Override
    public void onCastClicked(Cast cast) {
        String id = String.valueOf(cast.getId());
        Intent i = new Intent(TvShowDetailActivity.this, PeopleDetailActivity.class);
        i.putExtra(AppConstants.STAR_ID, id);
        startActivity(i);
    }

    @Override
    public void onRecommendationItemClicked(Recommendation recommendation) {
        Intent i = new Intent(TvShowDetailActivity.this, TvShowDetailActivity.class);
        i.putExtra(AppConstants.TV_SHOW_ID, String.valueOf(recommendation.getId()));
        i.putExtra(AppConstants.TV_SHOW_TITLE, recommendation.getName());
        startActivity(i);
        finish();
    }
}
