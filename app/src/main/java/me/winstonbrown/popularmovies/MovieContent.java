package me.winstonbrown.popularmovies;

import android.graphics.Bitmap;
import android.media.Image;

/**
 * Created by Winston on 2/7/2017.
 */

public class MovieContent {

    Bitmap poster;
    String poster_path;
    String title;
    String vote_average;
    String release_date;
    String overview;

    public MovieContent(MovieContent content) {
        poster = content.poster;
        poster_path = content.poster_path;
        title = content.title;
        vote_average = content.vote_average;
        release_date = content.release_date;
        overview = content.overview;
    }

    public MovieContent(Bitmap poster, String poster_path, String title, String vote_average, String release_date, String overview) {
        this.poster = poster;
        this.poster_path = poster_path;
        this.title = title;
        this.vote_average = vote_average;
        this.release_date = release_date;
        this.overview = overview;
    }
}
