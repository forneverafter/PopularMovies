package me.winstonbrown.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MovieFragment extends Fragment {

    final String LOG_TAG = "MovieFragment ::::";
    final String POPULAR_QUERY = "https://api.themoviedb.org/3/discover/movie?api_key=61e87f5514f6028779a53b20565c9a4c&language=en-US&sort_by=popularity.desc";
    final String HIGHEST_RATED_QUERY = "https://api.themoviedb.org/3/discover/movie?api_key=61e87f5514f6028779a53b20565c9a4c&sort_by=vote_average.desc";
    final String THUMBNAIL_QUERY = "https://image.tmdb.org/t/p/w154";

    final int mColumnCount = 4;

    MyMovieRecyclerViewAdapter mMyMovieRecyclerViewAdapter;
    RecyclerView recyclerView;

    List<MovieContent> mMovieList = new ArrayList<>();

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        //updateMovies();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_menu, menu);
        MenuItem filterItem = menu.findItem(R.id.movie_filter);
        Spinner spinner = (Spinner)MenuItemCompat.getActionView(filterItem);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (position == 0) {
                    sharedPreferences.edit().putString("filter", "popular").commit();
                    updateMovies();
                } else if (position == 1) {
                    sharedPreferences.edit().putString("filter", "highest_rated").commit();
                    updateMovies();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_spinner) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            //recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            mMyMovieRecyclerViewAdapter = new MyMovieRecyclerViewAdapter(mMovieList, mListener);
            recyclerView.setAdapter(mMyMovieRecyclerViewAdapter);
        }
        return view;
    }

    private void updateMovies() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String filter = sharedPreferences.getString("filter", "popular");

        Uri query = Uri.EMPTY;
        //Set uri query based on preference
        if (filter.equals("popular")) {
            query = Uri.parse(POPULAR_QUERY);
        } else if (filter.equals("highest_rated")) {
            query = Uri.parse(HIGHEST_RATED_QUERY);
        }
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        if (query != Uri.EMPTY) {
            fetchMoviesTask.execute(query);
        }
        //recyclerView.invalidate();
        //mMyMovieRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(MovieContent item);
    }

    private class FetchMoviesTask extends AsyncTask<Uri, Void, List<MovieContent>> {

        private List<MovieContent> getMovieListFromJson(String moviesJsonStr) throws JSONException {

            List<MovieContent> movieList = new ArrayList<>();

            //Objects to extract
            final String RESULTS = "results";
            final String POSTER_PATH = "poster_path";
            final String TITLE = "title";
            final String VOTE_AVERAGE = "vote_average";
            final String RELEASE_DATE = "release_date";
            final String OVERVIEW = "overview";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray movieArray = moviesJson.getJSONArray(RESULTS);

            MovieContent tempMovieContent;

            for (int i = 0; i < movieArray.length(); i++) {
                String poster_path;
                String title;
                String vote_average;
                String release_date;
                String overview;

                JSONObject movie = movieArray.getJSONObject(i);

                poster_path = movie.getString(POSTER_PATH);
                title = movie.getString(TITLE);
                vote_average = movie.getString(VOTE_AVERAGE);
                release_date = movie.getString(RELEASE_DATE);
                overview = movie.getString(OVERVIEW);

                //Attempt to get bitmap image for thumbnail
                Bitmap posterBitmap = null;

                try {
                    String urlString = THUMBNAIL_QUERY + poster_path;
                    URL url = new URL(urlString);
                    posterBitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
                    Log.d(LOG_TAG, posterBitmap.toString());

                } catch (IOException e)
                {
                    Log.e(LOG_TAG, e.getMessage());
                }

                tempMovieContent = new MovieContent(posterBitmap, poster_path,title,vote_average,release_date,overview);

                movieList.add(tempMovieContent);
            }
            return movieList;
        }

        @Override
        protected List<MovieContent> doInBackground(Uri... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;


            try {
                //params[0] is pass a uri of the search string

                URL url = new URL(params[0].toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieListFromJson(moviesJsonStr); //And finally return organized data!
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<MovieContent> movieContents) {
            mMovieList.clear();
            mMovieList.addAll(movieContents);
            mMyMovieRecyclerViewAdapter.notifyDataSetChanged();
            Log.d(LOG_TAG, Integer.toString(mMovieList.size()));
            //TODO : Update recyclerView
        }
    }
}
