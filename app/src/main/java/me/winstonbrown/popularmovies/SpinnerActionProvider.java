package me.winstonbrown.popularmovies;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Winston on 2/8/2017.
 */

public class SpinnerActionProvider extends ActionProvider {

    Context mContext;

    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public SpinnerActionProvider(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public View onCreateActionView() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.spinner_action, null);
        return view;
    }
}
