package me.winstonbrown.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements MovieFragment.OnListFragmentInteractionListener {

    final String MOVIEFRAGMENT = "moviefragement";
    final String DETAILSFRAGMENT = "detailsfragment";

    DetailsFragment detailsFragment = new DetailsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieFragment(),MOVIEFRAGMENT)
                    .commit();
        }
    }

    @Override
    public void onListFragmentInteraction(MovieContent item) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, detailsFragment, DETAILSFRAGMENT )
                .addToBackStack(DETAILSFRAGMENT)
                .commit();
        detailsFragment.UpdateView(item);

    }
}
