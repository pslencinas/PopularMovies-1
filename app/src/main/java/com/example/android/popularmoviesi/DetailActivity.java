package com.example.android.popularmoviesi;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private String sURLMovie;
        private String sTitleMovie;
        private String sOverview;
        private String sRelease;
        private String sAverage;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Context c = getActivity().getApplicationContext();

            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("URL")) {
                sURLMovie = intent.getStringExtra("URL");
                sTitleMovie = intent.getStringExtra("TITLE");
                sOverview = intent.getStringExtra("OVERVIEW");
                sRelease = intent.getStringExtra("RELEASE");
                sAverage = intent.getStringExtra("AVERAGE");

                Picasso.with(c).load(sURLMovie).fit()
                        .into((ImageView) rootView.findViewById(R.id.imageView));
                ((TextView) rootView.findViewById(R.id.title_text)).setText(sTitleMovie);
                ((TextView) rootView.findViewById(R.id.overview_text)).setText(sOverview);
                ((TextView) rootView.findViewById(R.id.release_text)).setText(sRelease);
                ((TextView) rootView.findViewById(R.id.average_text)).setText(sAverage);
            }

            return rootView;
        }




    }

}

