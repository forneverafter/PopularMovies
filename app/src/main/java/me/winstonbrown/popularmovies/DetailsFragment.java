package me.winstonbrown.popularmovies;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * Display movie details
 */
public class DetailsFragment extends Fragment {

    final String LOG_TAG = "DetailsFragment ::::";

    View view;

    MovieContent mMovieContent;

    public DetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_details, container, false);
        updateViews();
        FetchPosterTask fetchPosterTask = new FetchPosterTask();
        fetchPosterTask.execute();

        return view;
    }

    private void updateViews() {

        ImageView imageView = (ImageView)view.findViewById(R.id.detailsImageView);
        TextView titleTextView = (TextView)view.findViewById(R.id.detailsTitle);
        TextView voteTextView = (TextView)view.findViewById(R.id.voteTextView);
        TextView releaseDateTextView = (TextView)view.findViewById(R.id.releaseDateTextView);
        TextView overviewTextView = (TextView)view.findViewById(R.id.overviewTextView);

        imageView.setImageBitmap(mMovieContent.poster);
        titleTextView.setText(mMovieContent.title);
        voteTextView.setText(mMovieContent.vote_average);
        releaseDateTextView.setText(mMovieContent.release_date);
        overviewTextView.setText(mMovieContent.overview);
    }

    public void UpdateView(MovieContent movie) {
        this.mMovieContent = new MovieContent(movie);
        //If view has already been created we can update the view fields
        if (view != null) {
            updateViews();
        }
    }

    private class FetchPosterTask extends AsyncTask<Void, Void, Void> {

        final String POSTER_QUERY = "https://image.tmdb.org/t/p/w500";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String urlString = POSTER_QUERY + mMovieContent.poster_path;
                URL url = new URL(urlString);
                mMovieContent.poster = BitmapFactory.decodeStream((InputStream)url.getContent());

            } catch (IOException e)
            {
                Log.e(LOG_TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateViews();
        }
    }

}
