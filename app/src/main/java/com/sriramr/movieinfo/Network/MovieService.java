package com.sriramr.movieinfo.Network;

import com.sriramr.movieinfo.ui.Discover.DiscoverDetailActivity.Models.DiscoverMoviesResponse;
import com.sriramr.movieinfo.ui.Discover.DiscoverDetailActivity.Models.DiscoverShowResponse;
import com.sriramr.movieinfo.ui.Movies.MovieDetailActivity.Models.MovieDetailResponse;
import com.sriramr.movieinfo.ui.Movies.MovieListActivity.Models.MovieListResponse;
import com.sriramr.movieinfo.ui.People.PeopleDetailActivity.Models.PeopleDetailResponse;
import com.sriramr.movieinfo.ui.People.PopularPeopleActivity.Models.PopularPeopleResponse;
import com.sriramr.movieinfo.ui.TvShows.TvShows.Models.TvShowsResponse;
import com.sriramr.movieinfo.ui.TvShows.TvShowsDetail.Model.TvShowDetailResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieService {

    // movies
    @GET("movie/now_playing")
    Observable<MovieListResponse> getNowPlayingMovies(@Query("page") int page, @Query("api_key") String API_KEY);

    @GET("movie/popular")
    Observable<MovieListResponse> getPopularMovies(@Query("page") int page, @Query("api_key") String API_KEY);

    @GET("movie/top_rated")
    Observable<MovieListResponse> getTopRatedMovies(@Query("page") int page, @Query("api_key") String API_KEY);

    @GET("movie/upcoming")
    Observable<MovieListResponse> getUpcomingMovies(@Query("page") int page, @Query("api_key") String API_KEY);

    @GET("movie/{movie_id}")
    Observable<MovieDetailResponse> getMovieDetails(@Path("movie_id") String movieId, @Query("api_key") String api_key, @Query("append_to_response") String appendToResponse);

    // tv shows
    @GET("tv/airing_today")
    Observable<TvShowsResponse> getAiringTodayTvShows(@Query("api_key") String api_key, @Query("page") int page);

    @GET("tv/on_the_air")
    Observable<TvShowsResponse> getOnTheAirTvShows(@Query("api_key") String api_key, @Query("page") int page);

    @GET("tv/popular")
    Observable<TvShowsResponse> getPopularTv(@Query("api_key") String api_key, @Query("page") int page);

    @GET("tv/top_rated")
    Observable<TvShowsResponse> getTopRatedTv(@Query("api_key") String api_key, @Query("page") int page);

    @GET("tv/{show-id}")
    Observable<TvShowDetailResponse> getDetailTvShow(@Path("show-id") String showId, @Query("api_key") String api_key, @Query("append_to_response") String appendToResponse);

    // people
    @GET("person/popular")
    Observable<PopularPeopleResponse> getPopularPeople(@Query("api_key") String api_key, @Query("page") int page);

    @GET("person/{person_id}")
    Observable<PeopleDetailResponse> getPersonDetail(@Path("person_id") String person_id, @Query("api_key") String api_key, @Query("append_to_response") String append_to_response);

    // discover
    @GET("discover/movie")
    Observable<DiscoverMoviesResponse> getDiscoveredMovies(@Query("api_key") String api_key, @Query("with_genres") String genre, @Query("page") int page);

    @GET("discover/tv")
    Observable<DiscoverShowResponse> getDiscoverShows(@Query("api_key") String api_key, @Query("with_genres") String genre, @Query("page") int page);


/**     //search
    @GET("search/movie")
    Observable<MovieSearchResponse> getSearchedMovie(@Query("api_key") String api_key, @Query("query") String query, @Query("page") int page);

    @GET("search/person")
    Observable<PeopleSearchResponse> getSearchedPeople(@Query("api_key") String api_key, @Query("query") String query, @Query("page") int page);

    @GET("search/tv") Observable<ShowSearchResponse> getSearchShows(@Query("api_key") String api_key, @Query("query") String query, @Query("page") int page);
**/

}
